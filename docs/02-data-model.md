# 数据模型

## 概述

用户模块包含三张核心数据表，采用关联设计，支持用户基本信息、扩展资料和角色权限的分离存储。

## 表结构

### 1. sys_user（用户表）

存储用户的基本信息、认证信息和状态。

#### 表结构

| 字段名 | 类型 | 约束 | 说明 |
|--------|------|------|------|
| id | BIGINT | PRIMARY KEY, AUTO_INCREMENT | 用户ID |
| username | VARCHAR(255) | NOT NULL, UNIQUE | 用户名 |
| password | VARCHAR(255) | NOT NULL | 加密后的密码 |
| phone | VARCHAR(50) | NULL | 手机号码 |
| email | VARCHAR(255) | NULL | 电子邮箱 |
| status | INTEGER | DEFAULT 0 | 用户状态（0-正常 1-锁定 2-封禁） |
| roles | VARCHAR(255) | NULL | 角色标识，逗号分隔 |
| last_login_at | TIMESTAMP | NULL | 最后登录时间 |
| last_login_ip | VARCHAR(255) | NULL | 最后登录IP |
| last_login_device | VARCHAR(255) | NULL | 最后登录设备 |
| password_reset_at | TIMESTAMP | NULL | 密码重置时间 |
| login_fail_count | INTEGER | DEFAULT 0 | 登录失败次数 |
| locked_at | TIMESTAMP | NULL | 账户锁定时间 |
| deleted | INTEGER | DEFAULT 0, NOT NULL | 逻辑删除标记（0-未删除 1-已删除） |
| version | INTEGER | DEFAULT 0, NOT NULL | 乐观锁版本号 |
| created_at | TIMESTAMP | DEFAULT NOW(), NOT NULL | 创建时间 |
| updated_at | TIMESTAMP | DEFAULT NOW(), NOT NULL | 更新时间 |
| created_by | VARCHAR(255) | NULL | 创建人 |
| updated_by | VARCHAR(255) | NULL | 更新人 |

#### 索引

- `idx_user_username` - 用户名索引，加速用户名查询

#### 实体类

```java
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SysUser implements Serializable, UserDetails {
    private Long id;
    private String username;
    @JsonIgnore
    private String password;
    private String phone;
    private String email;
    private UserStatusEnum status;
    private String roles;
    private LocalDateTime lastLoginAt;
    private String lastLoginIp;
    private String lastLoginDevice;
    private LocalDateTime passwordResetAt;
    private Integer loginFailCount;
    private LocalDateTime lockedAt;
    private Integer deleted;
    private Integer version;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
    
    // Spring Security UserDetails 接口实现
    // ...
}
```

#### 特殊说明

1. `SysUser` 实现了 Spring Security 的 `UserDetails` 接口，用于认证和授权
2. `password` 字段使用 `@JsonIgnore` 注解，防止密码泄露
3. `status` 字段使用枚举类型 `UserStatusEnum`
4. `roles` 字段存储逗号分隔的角色名称（如 "ROLE_ADMIN,ROLE_USER"）

### 2. user_profile（用户资料表）

存储用户的详细资料信息，与 `sys_user` 表一对一关联。

#### 表结构

| 字段名 | 类型 | 约束 | 说明 |
|--------|------|------|------|
| user_id | BIGINT | PRIMARY KEY, FK(sys_user.id) | 用户ID |
| real_name | VARCHAR(255) | NULL | 真实姓名 |
| gender | INTEGER | NULL | 性别（0-女 1-男） |
| birthday | TIMESTAMP | NULL | 生日 |
| avatar | VARCHAR(255) | NULL | 头像URL |
| deleted | INTEGER | DEFAULT 0, NOT NULL | 逻辑删除标记 |
| version | INTEGER | DEFAULT 0, NOT NULL | 乐观锁版本号 |
| created_at | TIMESTAMP | DEFAULT NOW(), NOT NULL | 创建时间 |
| updated_at | TIMESTAMP | DEFAULT NOW(), NOT NULL | 更新时间 |
| created_by | VARCHAR(255) | NULL | 创建人 |
| updated_by | VARCHAR(255) | NULL | 更新人 |

#### 实体类

```java
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfile {
    private Long userId;
    private String realName;
    private GenderEnum gender;
    private LocalDateTime birthday;
    private String avatar;
    private Integer deleted;
    private Integer version;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
}
```

#### 特殊说明

1. `user_id` 作为主键，与 `sys_user.id` 一对一关联
2. `gender` 字段使用枚举类型 `GenderEnum`
3. 用户资料在用户创建时自动初始化

### 3. user_role（用户角色表）

存储系统中的角色定义。

#### 表结构

| 字段名 | 类型 | 约束 | 说明 |
|--------|------|------|------|
| id | INTEGER | PRIMARY KEY | 角色ID |
| role_name | VARCHAR(50) | NOT NULL | 角色名称 |
| is_default | BOOLEAN | DEFAULT FALSE, NOT NULL | 是否为默认角色 |
| deleted | INTEGER | DEFAULT 0, NOT NULL | 逻辑删除标记 |
| version | INTEGER | DEFAULT 0, NOT NULL | 乐观锁版本号 |
| created_at | TIMESTAMP | DEFAULT NOW(), NOT NULL | 创建时间 |
| updated_at | TIMESTAMP | DEFAULT NOW(), NOT NULL | 更新时间 |
| created_by | VARCHAR(255) | NULL | 创建人 |
| updated_by | VARCHAR(255) | NULL | 更新人 |

#### 索引

- `uk_user_role_default_true` - 唯一索引，确保只有一个默认角色

#### 实体类

```java
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRole implements Serializable {
    private Long id;
    private String roleName;
    private Boolean isDefault;
    private Integer deleted;
    private Integer version;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
}
```

#### 预置数据

系统初始化时会插入两个默认角色：

```sql
-- 管理员角色
INSERT INTO user_role (id, role_name, is_default) 
VALUES (1, 'ROLE_ADMIN', false);

-- 普通用户角色（默认）
INSERT INTO user_role (id, role_name, is_default) 
VALUES (2, 'ROLE_USER', true);
```

## 枚举类型

### UserStatusEnum（用户状态）

```java
public enum UserStatusEnum {
    NORMAL(0, "正常"),
    LOCKED(1, "锁定"),
    BANNED(2, "封禁");
    
    private final Integer code;
    private final String description;
}
```

### GenderEnum（性别）

```java
public enum GenderEnum {
    FEMALE(0, "女"),
    MALE(1, "男");
    
    private final Integer code;
    private final String description;
}
```

## 表关系

```
sys_user (1) -------- (1) user_profile
    |
    | (N)
    |
    | (M)
user_role
```

- `sys_user` 与 `user_profile` 是一对一关系
- `sys_user` 与 `user_role` 是多对多关系（通过 `sys_user.roles` 字段存储）

## 数据完整性

### 逻辑删除

所有表都支持逻辑删除，通过 `deleted` 字段标记：
- `0` - 未删除
- `1` - 已删除

查询时需要添加 `WHERE deleted = 0` 条件。

### 乐观锁

所有表都支持乐观锁，通过 `version` 字段实现：
- 更新时检查版本号是否匹配
- 更新成功后版本号自动加1

### 审计字段

所有表都包含审计字段：
- `created_at` - 记录创建时间
- `updated_at` - 记录更新时间
- `created_by` - 创建人ID
- `updated_by` - 更新人ID

## 数据库兼容性

用户模块同时支持 PostgreSQL 和 MySQL：

- PostgreSQL: 使用 `sql/create_table.sql`
- MySQL: 使用 `sql/create_table_mysql.sql`

主要差异：
- 序列生成器（PostgreSQL）vs 自增主键（MySQL）
- 字符串拼接语法（`||` vs `CONCAT`）
