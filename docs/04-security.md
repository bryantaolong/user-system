# 权限与安全

## 概述

用户模块采用 Spring Security 框架实现认证和授权，提供完善的安全机制保护系统资源。

## 认证机制

### JWT Token 认证

系统使用 JWT（JSON Web Token）进行用户认证：

1. 用户登录成功后，系统生成 JWT Token
2. 客户端在后续请求中携带 Token
3. 服务端验证 Token 的有效性和权限

**Token 格式**

```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

### UserDetails 实现

`SysUser` 实体类实现了 Spring Security 的 `UserDetails` 接口：

```java
public class SysUser implements UserDetails {
    
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // 从 roles 字段解析权限
        return Arrays.stream(this.roles.split(","))
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }
    
    @Override
    public boolean isAccountNonLocked() {
        // 正常状态返回 true
        if (this.status == UserStatusEnum.NORMAL) {
            return true;
        }
        // 锁定状态：判断是否已过1小时
        if (this.status == UserStatusEnum.LOCKED && this.lockedAt != null) {
            return LocalDateTime.now().isAfter(this.lockedAt.plusHours(1));
        }
        return false;
    }
    
    @Override
    public boolean isEnabled() {
        return this.status != UserStatusEnum.BANNED && this.deleted == 0;
    }
}
```

## 授权机制

### 角色定义

系统预置两个角色：

- `ROLE_ADMIN` - 管理员角色，拥有所有权限
- `ROLE_USER` - 普通用户角色，默认角色

### 权限注解

使用 `@PreAuthorize` 注解进行方法级权限控制：

```java
// 仅管理员可访问
@PreAuthorize("hasRole('ADMIN')")
public Result<SysUser> createUser(...) { }

// 管理员或用户本人可访问
@PreAuthorize("hasRole('ADMIN') or (#userId == authentication.principal.id)")
public Result<SysUser> updateUser(@PathVariable Long userId, ...) { }

// 已认证用户可访问
@PreAuthorize("isAuthenticated()")
public Result<UserProfileVO> getCurrentUserProfile() { }

// 公开访问
@PreAuthorize("permitAll()")
public Result<UserProfileVO> getUserProfileByUserId(...) { }
```

### 权限控制矩阵

| 接口 | 管理员 | 用户本人 | 其他用户 | 未认证 |
|------|--------|----------|----------|--------|
| 创建用户 | ✓ | ✗ | ✗ | ✗ |
| 查询用户列表 | ✓ | ✗ | ✗ | ✗ |
| 查询用户详情 | ✓ | ✗ | ✗ | ✗ |
| 更新用户信息 | ✓ | ✓ | ✗ | ✗ |
| 修改用户角色 | ✓ | ✗ | ✗ | ✗ |
| 重置密码 | ✓ | ✗ | ✗ | ✗ |
| 封禁/解封用户 | ✓ | ✗ | ✗ | ✗ |
| 删除用户 | ✓ | ✗ | ✗ | ✗ |
| 查询用户资料 | ✓ | ✓ | ✓ | ✓ |
| 更新用户资料 | ✓ | ✓ | ✗ | ✗ |
| 上传头像 | ✓ | ✓ | ✗ | ✗ |

## 安全特性

### 1. 密码加密

使用 BCrypt 算法加密存储密码：

```java
@Service
public class UserService {
    private final PasswordEncoder passwordEncoder;
    
    public SysUser createUser(UserCreateRequest req) {
        String encodedPassword = passwordEncoder.encode(req.getPassword());
        // ...
    }
}
```

**特点**：
- 单向加密，无法解密
- 每次加密结果不同（加盐）
- 验证时使用 `passwordEncoder.matches(rawPassword, encodedPassword)`

### 2. 登录失败锁定

连续登录失败会触发账户锁定：

- 失败次数达到阈值（如5次）后，账户状态变为 `LOCKED`
- 锁定时间为1小时
- 1小时后自动解锁

```java
@Override
public boolean isAccountNonLocked() {
    if (this.status == UserStatusEnum.LOCKED && this.lockedAt != null) {
        return LocalDateTime.now().isAfter(this.lockedAt.plusHours(1));
    }
    return this.status == UserStatusEnum.NORMAL;
}
```

### 3. 用户状态管理

用户有三种状态：

```java
public enum UserStatusEnum {
    NORMAL(0, "正常"),    // 可正常登录
    LOCKED(1, "锁定"),    // 临时锁定，1小时后自动解锁
    BANNED(2, "封禁");    // 永久封禁，需管理员手动解封
}
```

### 4. 逻辑删除

用户删除采用逻辑删除（软删除）：

- 设置 `deleted = 1`
- 数据仍保留在数据库中
- 查询时自动过滤已删除记录

```xml
<select id="selectById" resultMap="BaseResultMap">
    SELECT * FROM sys_user
    WHERE id = #{id} AND deleted = 0
</select>
```

### 5. 乐观锁

使用版本号实现乐观锁，防止并发更新冲突：

```java
public class SysUser {
    private Integer version;  // 版本号
}
```

更新时检查版本号：

```xml
<update id="update">
    UPDATE sys_user
    SET ..., version = version + 1
    WHERE id = #{id} AND version = #{version}
</update>
```

### 6. 敏感信息保护

密码字段使用 `@JsonIgnore` 注解，防止序列化泄露：

```java
public class SysUser {
    @JsonIgnore
    private String password;
}
```

### 7. 审计日志

所有表包含审计字段，记录操作信息：

```java
private LocalDateTime createdAt;   // 创建时间
private LocalDateTime updatedAt;   // 更新时间
private String createdBy;           // 创建人
private String updatedBy;           // 更新人
```

## 安全配置

### 密码策略建议

虽然系统未强制密码复杂度，但建议实施以下策略：

- 最小长度：8位
- 包含大小写字母、数字、特殊字符
- 定期更换密码（如90天）
- 禁止使用常见弱密码

### Token 配置

JWT Token 配置建议：

```yaml
jwt:
  secret: ${JWT_SECRET}           # 使用环境变量
  expiration: 86400000            # 24小时（毫秒）
  refresh-expiration: 604800000   # 7天（毫秒）
```

### CORS 配置

跨域资源共享配置：

```java
@Configuration
public class CorsConfig {
    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        config.addAllowedOrigin("http://localhost:5173");
        config.addAllowedMethod("*");
        config.addAllowedHeader("*");
        config.setAllowCredentials(true);
        // ...
    }
}
```

## 安全最佳实践

### 1. 环境变量管理

敏感配置使用环境变量：

```yaml
# application.yaml
spring:
  datasource:
    password: ${DB_PASSWORD}

jwt:
  secret: ${JWT_SECRET}
```

### 2. HTTPS 部署

生产环境必须使用 HTTPS：

```yaml
server:
  ssl:
    enabled: true
    key-store: classpath:keystore.p12
    key-store-password: ${SSL_PASSWORD}
```

### 3. 限流保护

对登录等敏感接口实施限流：

```java
@RateLimiter(name = "login", fallbackMethod = "loginFallback")
public Result<LoginResponse> login(...) { }
```

### 4. SQL 注入防护

使用 MyBatis 参数化查询：

```xml
<!-- 正确：使用 #{} -->
<select id="selectByUsername">
    SELECT * FROM sys_user WHERE username = #{username}
</select>

<!-- 错误：使用 ${} 会导致 SQL 注入 -->
<select id="selectByUsername">
    SELECT * FROM sys_user WHERE username = '${username}'
</select>
```

### 5. XSS 防护

前端对用户输入进行转义，后端验证数据格式：

```java
@NotBlank(message = "用户名不能为空")
@Pattern(regexp = "^[a-zA-Z0-9_]{4,20}$", message = "用户名格式不正确")
private String username;
```

## 常见安全问题

### Q1: Token 泄露怎么办？

- 立即使 Token 失效（加入黑名单）
- 强制用户重新登录
- 记录安全日志

### Q2: 如何防止暴力破解？

- 实施登录失败锁定机制
- 添加验证码
- 使用限流策略

### Q3: 如何安全地重置密码？

- 发送验证码到注册邮箱/手机
- 验证用户身份后才允许重置
- 记录密码重置时间

### Q4: 多设备登录如何管理？

- 记录每个设备的 Token
- 提供"踢出其他设备"功能
- 限制同时在线设备数量
