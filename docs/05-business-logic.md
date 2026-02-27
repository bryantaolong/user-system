# 业务逻辑说明

## 概述

本文档详细说明用户模块的核心业务逻辑和处理流程。

## 用户生命周期

### 1. 用户创建流程

```
管理员发起创建请求
    ↓
验证用户名是否已存在
    ↓
处理角色分配（未指定则使用默认角色）
    ↓
验证角色ID是否有效
    ↓
密码加密（BCrypt）
    ↓
插入用户记录到 sys_user 表
    ↓
自动创建用户资料记录（user_profile）
    ↓
返回创建的用户信息
```

**关键代码**

```java
@Transactional
public SysUser createUser(UserCreateRequest req) {
    // 1. 检查用户名唯一性
    if (userMapper.selectByUsername(req.getUsername()) != null) {
        throw new BusinessException("用户名已存在");
    }
    
    // 2. 处理角色分配
    List<Long> roleIds = req.getRoleIds();
    if (roleIds == null || roleIds.isEmpty()) {
        UserRole defaultRole = userRoleService.getDefaultRole();
        roleIds.add(defaultRole.getId());
    }
    
    // 3. 验证角色有效性
    List<UserRole> roles = userRoleService.listByIds(roleIds);
    String roleNames = roles.stream()
            .map(UserRole::getRoleName)
            .collect(Collectors.joining(","));
    
    // 4. 创建用户
    SysUser sysUser = SysUser.builder()
            .username(req.getUsername())
            .password(passwordEncoder.encode(req.getPassword()))
            .roles(roleNames)
            .status(UserStatusEnum.NORMAL)
            .build();
    
    userMapper.insert(sysUser);
    
    // 5. 初始化用户资料
    UserProfile profile = UserProfile.builder()
            .userId(sysUser.getId())
            .build();
    userProfileService.createUserProfile(profile);
    
    return sysUser;
}
```

### 2. 用户查询流程

#### 分页查询

```
接收分页参数（pageNum, pageSize）
    ↓
计算偏移量（offset = (pageNum - 1) * pageSize）
    ↓
执行分页查询（LIMIT + OFFSET）
    ↓
查询总记录数
    ↓
构建分页结果对象
    ↓
返回分页数据
```

#### 搜索查询

```
接收搜索条件（username, phone, email, status）
    ↓
构建动态 SQL 查询条件
    ↓
执行模糊匹配查询（LIKE '%keyword%'）
    ↓
返回匹配结果
```

**MyBatis 动态 SQL**

```xml
<sql id="SearchWhere">
    <if test="req != null">
        <if test="req.username != null and req.username != ''">
            AND username LIKE '%' || #{req.username} || '%'
        </if>
        <if test="req.phone != null and req.phone != ''">
            AND phone LIKE '%' || #{req.phone} || '%'
        </if>
        <if test="req.email != null and req.email != ''">
            AND email LIKE '%' || #{req.email} || '%'
        </if>
        <if test="req.status != null">
            AND status = #{req.status}
        </if>
    </if>
</sql>
```

### 3. 用户更新流程

```
接收更新请求
    ↓
验证用户是否存在
    ↓
检查权限（管理员或用户本人）
    ↓
更新非空字段
    ↓
更新 updated_at 和 updated_by
    ↓
执行更新操作
    ↓
返回更新后的用户信息
```

**关键逻辑**

```java
public SysUser updateUser(Long userId, UserUpdateDTO dto) {
    // 1. 获取用户
    SysUser user = this.getUserById(userId);
    
    // 2. 更新非空字段
    if (dto.getPhone() != null) user.setPhone(dto.getPhone());
    if (dto.getEmail() != null) user.setEmail(dto.getEmail());
    
    // 3. 执行更新
    userMapper.update(user);
    
    return user;
}
```

### 4. 用户删除流程

```
接收删除请求
    ↓
验证用户是否存在
    ↓
检查管理员权限
    ↓
执行逻辑删除（设置 deleted = 1）
    ↓
记录删除时间和操作人
    ↓
返回删除结果
```

**逻辑删除实现**

```xml
<update id="deleteById">
    UPDATE sys_user
    SET deleted = 1,
        updated_at = NOW(),
        updated_by = #{updatedBy}
    WHERE id = #{id} AND deleted = 0
</update>
```

## 角色管理

### 角色分配流程

```
接收角色ID列表
    ↓
验证所有角色ID是否存在
    ↓
查询角色详细信息
    ↓
将角色名称拼接为字符串（逗号分隔）
    ↓
更新用户的 roles 字段
    ↓
返回更新后的用户信息
```

**关键代码**

```java
@Transactional
public SysUser changeRoleByIds(Long userId, ChangeRoleRequest req) {
    List<Long> ids = req.getRoleIds();
    
    // 1. 验证角色存在性
    List<UserRole> roles = userRoleService.listByIds(ids);
    if (roles.size() != ids.size()) {
        Set<Long> exist = roles.stream()
                .map(UserRole::getId)
                .collect(Collectors.toSet());
        ids.removeAll(exist);
        throw new IllegalArgumentException("角色不存在：" + ids);
    }
    
    // 2. 拼接角色名称
    String roleNames = roles.stream()
            .map(UserRole::getRoleName)
            .collect(Collectors.joining(","));
    
    // 3. 更新用户角色
    SysUser user = this.getUserById(userId);
    user.setRoles(roleNames);
    userMapper.update(user);
    
    return user;
}
```

### 默认角色机制

- 系统维护一个默认角色（`is_default = true`）
- 创建用户时未指定角色，自动分配默认角色
- 通过唯一索引确保只有一个默认角色

```sql
create unique index uk_user_role_default_true
    on user_role (is_default)
    where (is_default = true);
```

## 用户状态管理

### 状态转换图

```
NORMAL (正常)
    ↓ 登录失败次数达到阈值
LOCKED (锁定)
    ↓ 1小时后自动解锁 或 管理员手动解锁
NORMAL (正常)
    ↓ 管理员封禁
BANNED (封禁)
    ↓ 管理员解封
NORMAL (正常)
```

### 封禁用户

```java
public SysUser blockUser(Long userId) {
    SysUser user = this.getUserById(userId);
    user.setStatus(UserStatusEnum.BANNED);
    userMapper.update(user);
    log.info("用户ID: {} 封禁成功", userId);
    return user;
}
```

### 解封用户

```java
public SysUser unblockUser(Long userId) {
    SysUser user = this.getUserById(userId);
    user.setStatus(UserStatusEnum.NORMAL);
    userMapper.update(user);
    log.info("用户ID: {} 解封成功", userId);
    return user;
}
```

### 自动解锁机制

```java
@Override
public boolean isAccountNonLocked() {
    // 正常状态直接返回 true
    if (this.status == UserStatusEnum.NORMAL) {
        return true;
    }
    
    // 锁定状态：判断锁定时间是否已过1小时
    if (this.status == UserStatusEnum.LOCKED && this.lockedAt != null) {
        return LocalDateTime.now().isAfter(this.lockedAt.plusHours(1));
    }
    
    return false;
}
```

## 密码管理

### 密码重置流程

```
管理员发起重置请求
    ↓
验证用户是否存在
    ↓
使用 BCrypt 加密新密码
    ↓
更新密码和重置时间
    ↓
记录操作日志
    ↓
返回更新结果
```

**关键代码**

```java
public SysUser resetPassword(Long userId, String newPassword) {
    SysUser user = this.getUserById(userId);
    
    // 加密新密码
    user.setPassword(passwordEncoder.encode(newPassword));
    
    // 记录重置时间
    user.setPasswordResetAt(LocalDateTime.now());
    
    userMapper.update(user);
    log.info("用户ID: {} 的密码强制修改成功", userId);
    
    return user;
}
```

### 密码安全策略

1. 使用 BCrypt 算法加密
2. 每次加密结果不同（自动加盐）
3. 记录密码重置时间
4. 建议定期更换密码

## 用户资料管理

### 资料更新流程

```
用户发起更新请求
    ↓
验证用户身份
    ↓
查询现有资料
    ↓
更新非空字段
    ↓
保存更新
    ↓
返回完整资料信息
```

### 头像上传流程

```
用户上传头像文件
    ↓
验证文件类型和大小
    ↓
生成唯一文件名
    ↓
保存文件到指定目录
    ↓
更新用户资料中的 avatar 字段
    ↓
返回头像访问路径
```

**关键代码**

```java
public String updateAvatar(Long userId, MultipartFile file) {
    // 1. 验证文件
    if (file.isEmpty()) {
        throw new BusinessException("上传文件不能为空");
    }
    
    // 2. 保存文件
    String avatarPath = fileService.saveFile(file, "avatar");
    
    // 3. 更新资料
    UserProfile profile = getUserProfileByUserId(userId);
    profile.setAvatar(avatarPath);
    userProfileMapper.update(profile);
    
    return avatarPath;
}
```

## 数据一致性

### 事务管理

关键操作使用 `@Transactional` 注解确保事务一致性：

```java
@Transactional
public SysUser createUser(UserCreateRequest req) {
    // 创建用户
    userMapper.insert(sysUser);
    
    // 创建用户资料
    userProfileService.createUserProfile(profile);
    
    // 任一操作失败，整体回滚
}
```

### 乐观锁控制

使用版本号防止并发更新冲突：

```java
public class SysUser {
    private Integer version;  // 更新前：version = 1
}

// 更新时检查版本号
UPDATE sys_user 
SET ..., version = version + 1  // 更新后：version = 2
WHERE id = #{id} AND version = #{version}  // 必须匹配原版本号
```

### 外键约束

虽然数据库层面未设置外键，但业务层面维护数据一致性：

```java
// 创建用户时自动创建资料
UserProfile profile = UserProfile.builder()
        .userId(sysUser.getId())
        .build();
userProfileService.createUserProfile(profile);
```

## 异常处理

### 业务异常

```java
// 用户名重复
if (userMapper.selectByUsername(username) != null) {
    throw new BusinessException("用户名已存在");
}

// 用户不存在
return Optional.ofNullable(userMapper.selectById(userId))
        .orElseThrow(() -> new ResourceNotFoundException("用户不存在"));

// 角色不存在
if (roles.size() != roleIds.size()) {
    throw new IllegalArgumentException("角色不存在：" + missingIds);
}
```

### 全局异常处理

```java
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(BusinessException.class)
    public Result<?> handleBusinessException(BusinessException e) {
        return Result.error(HttpStatus.BAD_REQUEST, e.getMessage());
    }
    
    @ExceptionHandler(ResourceNotFoundException.class)
    public Result<?> handleNotFoundException(ResourceNotFoundException e) {
        return Result.error(HttpStatus.NOT_FOUND, e.getMessage());
    }
}
```

## 性能优化

### 1. 索引优化

```sql
-- 用户名索引（高频查询）
create index idx_user_username on sys_user (username);

-- 复合索引（搜索场景）
create index idx_user_search on sys_user (status, deleted, created_at);
```

### 2. 分页查询

使用 LIMIT + OFFSET 实现分页：

```sql
SELECT * FROM sys_user
WHERE deleted = 0
ORDER BY updated_at DESC
LIMIT #{pageSize} OFFSET #{offset}
```

### 3. 批量查询

```java
// 批量查询用户
public List<SysUser> getUsersByIds(List<Long> userIds) {
    if (userIds == null || userIds.isEmpty()) {
        return new ArrayList<>();
    }
    return userMapper.selectByIdList(userIds);
}
```

```xml
<select id="selectByIdList" resultMap="BaseResultMap">
    SELECT * FROM sys_user
    WHERE id IN
    <foreach collection="ids" item="id" open="(" close=")" separator=",">
        #{id}
    </foreach>
</select>
```

## 日志记录

### 操作日志

关键操作记录日志：

```java
log.info("管理员创建用户成功: id: {}, username: {}", sysUser.getId(), sysUser.getUsername());
log.info("用户ID: {} 的信息更新成功", userId);
log.info("用户ID: {} 封禁成功", userId);
log.info("用户ID: {} 删除成功 (逻辑删除)", userId);
```

### 审计字段

所有表记录操作信息：

```java
LocalDateTime now = LocalDateTime.now();
String operator = String.valueOf(JwtUtils.getCurrentUserId());

sysUser.setCreatedAt(now);
sysUser.setUpdatedAt(now);
sysUser.setCreatedBy(operator);
sysUser.setUpdatedBy(operator);
```
