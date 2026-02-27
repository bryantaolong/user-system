# API 接口文档

## 概述

用户模块提供 RESTful API 接口，所有接口返回统一的响应格式。

## 统一响应格式

```json
{
    "code": 200,
    "message": "success",
    "data": { ... }
}
```

## 认证方式

大部分接口需要 JWT Token 认证，在请求头中添加：

```
Authorization: Bearer YOUR_JWT_TOKEN
```

---

## 用户管理 API

### 1. 创建用户

创建新用户（仅管理员）。

**请求**

```http
POST /api/users
Content-Type: application/json
Authorization: Bearer {admin_token}

{
    "username": "testuser",
    "password": "password123",
    "phone": "13800138000",
    "email": "test@example.com",
    "roleIds": [2]
}
```

**请求参数**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| username | String | 是 | 用户名，唯一 |
| password | String | 是 | 密码 |
| phone | String | 否 | 手机号 |
| email | String | 否 | 邮箱 |
| roleIds | List\<Long\> | 否 | 角色ID列表，默认使用系统默认角色 |

**响应**

```json
{
    "code": 200,
    "message": "success",
    "data": {
        "id": 1,
        "username": "testuser",
        "phone": "13800138000",
        "email": "test@example.com",
        "status": 0,
        "roles": "ROLE_USER",
        "createdAt": "2024-01-01T10:00:00"
    }
}
```

**权限要求**: `ROLE_ADMIN`

---

### 2. 获取用户列表

分页获取所有用户列表。

**请求**

```http
GET /api/users?pageNum=1&pageSize=10
Authorization: Bearer {admin_token}
```

**查询参数**

| 参数 | 类型 | 必填 | 默认值 | 说明 |
|------|------|------|--------|------|
| pageNum | Integer | 否 | 1 | 页码 |
| pageSize | Integer | 否 | 10 | 每页数量 |

**响应**

```json
{
    "code": 200,
    "message": "success",
    "data": {
        "rows": [
            {
                "id": 1,
                "username": "admin",
                "email": "admin@example.com",
                "status": 0,
                "roles": "ROLE_ADMIN"
            }
        ],
        "pageNum": 1,
        "pageSize": 10,
        "total": 1
    }
}
```

**权限要求**: `ROLE_ADMIN`

---

### 3. 根据ID查询用户

根据用户ID获取用户详细信息。

**请求**

```http
GET /api/users/{userId}
Authorization: Bearer {admin_token}
```

**路径参数**

| 参数 | 类型 | 说明 |
|------|------|------|
| userId | Long | 用户ID |

**响应**

```json
{
    "code": 200,
    "message": "success",
    "data": {
        "id": 1,
        "username": "testuser",
        "phone": "13800138000",
        "email": "test@example.com",
        "status": 0,
        "roles": "ROLE_USER",
        "lastLoginAt": "2024-01-01T10:00:00",
        "lastLoginIp": "192.168.1.1"
    }
}
```

**权限要求**: `ROLE_ADMIN`

---

### 4. 根据用户名查询用户

根据用户名获取用户信息。

**请求**

```http
GET /api/users/username/{username}
Authorization: Bearer {admin_token}
```

**路径参数**

| 参数 | 类型 | 说明 |
|------|------|------|
| username | String | 用户名 |

**响应**

```json
{
    "code": 200,
    "message": "success",
    "data": {
        "id": 1,
        "username": "testuser",
        "email": "test@example.com"
    }
}
```

**权限要求**: `ROLE_ADMIN`

---

### 5. 搜索用户

多条件模糊搜索用户，支持分页。

**请求**

```http
POST /api/users/search?pageNum=1&pageSize=10
Content-Type: application/json
Authorization: Bearer {admin_token}

{
    "username": "test",
    "phone": "138",
    "email": "example.com",
    "status": 0
}
```

**请求参数**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| username | String | 否 | 用户名（模糊匹配） |
| phone | String | 否 | 手机号（模糊匹配） |
| email | String | 否 | 邮箱（模糊匹配） |
| status | Integer | 否 | 用户状态（0-正常 1-锁定 2-封禁） |

**查询参数**

| 参数 | 类型 | 必填 | 默认值 | 说明 |
|------|------|------|--------|------|
| pageNum | Integer | 否 | 1 | 页码 |
| pageSize | Integer | 否 | 10 | 每页数量 |

**响应**

```json
{
    "code": 200,
    "message": "success",
    "data": {
        "rows": [...],
        "pageNum": 1,
        "pageSize": 10,
        "total": 5
    }
}
```

**权限要求**: `ROLE_ADMIN`

---

### 6. 更新用户信息

更新用户的基本信息。

**请求**

```http
PUT /api/users/{userId}
Content-Type: application/json
Authorization: Bearer {token}

{
    "phone": "13900139000",
    "email": "newemail@example.com"
}
```

**路径参数**

| 参数 | 类型 | 说明 |
|------|------|------|
| userId | Long | 用户ID |

**请求参数**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| phone | String | 否 | 手机号 |
| email | String | 否 | 邮箱 |

**响应**

```json
{
    "code": 200,
    "message": "success",
    "data": {
        "id": 1,
        "username": "testuser",
        "phone": "13900139000",
        "email": "newemail@example.com"
    }
}
```

**权限要求**: `ROLE_ADMIN` 或用户本人

---

### 7. 修改用户角色

修改指定用户的角色。

**请求**

```http
PUT /api/users/roles/{userId}
Content-Type: application/json
Authorization: Bearer {admin_token}

{
    "roleIds": [1, 2]
}
```

**路径参数**

| 参数 | 类型 | 说明 |
|------|------|------|
| userId | Long | 用户ID |

**请求参数**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| roleIds | List\<Long\> | 是 | 角色ID列表 |

**响应**

```json
{
    "code": 200,
    "message": "success",
    "data": {
        "id": 1,
        "username": "testuser",
        "roles": "ROLE_ADMIN,ROLE_USER"
    }
}
```

**权限要求**: `ROLE_ADMIN`

---

### 8. 重置用户密码

管理员强制重置用户密码。

**请求**

```http
PUT /api/users/password/{userId}
Content-Type: application/json
Authorization: Bearer {admin_token}

{
    "newPassword": "newpassword123"
}
```

**路径参数**

| 参数 | 类型 | 说明 |
|------|------|------|
| userId | Long | 用户ID |

**请求参数**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| newPassword | String | 是 | 新密码 |

**响应**

```json
{
    "code": 200,
    "message": "success",
    "data": {
        "id": 1,
        "username": "testuser",
        "passwordResetAt": "2024-01-01T10:00:00"
    }
}
```

**权限要求**: `ROLE_ADMIN`

---

### 9. 封禁用户

封禁指定用户，用户将无法登录。

**请求**

```http
PUT /api/users/block/{userId}
Authorization: Bearer {admin_token}
```

**路径参数**

| 参数 | 类型 | 说明 |
|------|------|------|
| userId | Long | 用户ID |

**响应**

```json
{
    "code": 200,
    "message": "success",
    "data": {
        "id": 1,
        "username": "testuser",
        "status": 2
    }
}
```

**权限要求**: `ROLE_ADMIN`

---

### 10. 解封用户

解除用户封禁状态。

**请求**

```http
PUT /api/users/unblock/{userId}
Authorization: Bearer {admin_token}
```

**路径参数**

| 参数 | 类型 | 说明 |
|------|------|------|
| userId | Long | 用户ID |

**响应**

```json
{
    "code": 200,
    "message": "success",
    "data": {
        "id": 1,
        "username": "testuser",
        "status": 0
    }
}
```

**权限要求**: `ROLE_ADMIN`

---

### 11. 删除用户

逻辑删除用户（软删除）。

**请求**

```http
DELETE /api/users/{userId}
Authorization: Bearer {admin_token}
```

**路径参数**

| 参数 | 类型 | 说明 |
|------|------|------|
| userId | Long | 用户ID |

**响应**

```json
{
    "code": 200,
    "message": "success",
    "data": 1
}
```

**权限要求**: `ROLE_ADMIN`

---

## 用户资料 API

### 1. 获取当前用户资料

获取当前登录用户的详细资料。

**请求**

```http
GET /api/user-profiles/me
Authorization: Bearer {token}
```

**响应**

```json
{
    "code": 200,
    "message": "success",
    "data": {
        "userId": 1,
        "username": "testuser",
        "email": "test@example.com",
        "phone": "13800138000",
        "realName": "张三",
        "gender": 1,
        "birthday": "1990-01-01T00:00:00",
        "avatar": "/uploads/avatar/user1.jpg"
    }
}
```

**权限要求**: 已认证用户

---

### 2. 根据用户ID查询资料

查询指定用户的资料（公开访问）。

**请求**

```http
GET /api/user-profiles/{userId}
```

**路径参数**

| 参数 | 类型 | 说明 |
|------|------|------|
| userId | Long | 用户ID |

**响应**

```json
{
    "code": 200,
    "message": "success",
    "data": {
        "userId": 1,
        "username": "testuser",
        "realName": "张三",
        "gender": 1,
        "avatar": "/uploads/avatar/user1.jpg"
    }
}
```

**权限要求**: 无（公开访问）

---

### 3. 根据真实姓名查询资料

根据真实姓名查询用户资料。

**请求**

```http
GET /api/user-profiles/name/{realName}
Authorization: Bearer {token}
```

**路径参数**

| 参数 | 类型 | 说明 |
|------|------|------|
| realName | String | 真实姓名 |

**响应**

```json
{
    "code": 200,
    "message": "success",
    "data": {
        "userId": 1,
        "username": "testuser",
        "realName": "张三"
    }
}
```

**权限要求**: 已认证用户

---

### 4. 更新当前用户资料

更新当前登录用户的资料信息。

**请求**

```http
PUT /api/user-profiles
Content-Type: application/json
Authorization: Bearer {token}

{
    "realName": "李四",
    "gender": 1,
    "birthday": "1990-01-01T00:00:00",
    "avatar": "/uploads/avatar/new.jpg"
}
```

**请求参数**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| realName | String | 否 | 真实姓名 |
| gender | Integer | 否 | 性别（0-女 1-男） |
| birthday | LocalDateTime | 否 | 生日 |
| avatar | String | 否 | 头像URL |

**响应**

```json
{
    "code": 200,
    "message": "success",
    "data": {
        "userId": 1,
        "username": "testuser",
        "realName": "李四",
        "gender": 1,
        "birthday": "1990-01-01T00:00:00",
        "avatar": "/uploads/avatar/new.jpg"
    }
}
```

**权限要求**: 已认证用户

---

### 5. 上传头像

上传当前用户的头像图片。

**请求**

```http
POST /api/user-profiles/avatar
Content-Type: multipart/form-data
Authorization: Bearer {token}

file: [binary data]
```

**请求参数**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| file | MultipartFile | 是 | 头像图片文件 |

**响应**

```json
{
    "code": 200,
    "message": "success",
    "data": "/uploads/avatar/user1_20240101.jpg"
}
```

**权限要求**: 已认证用户

---

## 用户角色 API

### 1. 获取所有角色选项

获取系统中所有可用的角色列表（用于下拉选择）。

**请求**

```http
GET /api/user-roles
Authorization: Bearer {admin_token}
```

**响应**

```json
{
    "code": 200,
    "message": "success",
    "data": [
        {
            "id": 1,
            "roleName": "ROLE_ADMIN"
        },
        {
            "id": 2,
            "roleName": "ROLE_USER"
        }
    ]
}
```

**权限要求**: `ROLE_ADMIN`

---

## 错误响应

当请求失败时，API 返回错误信息：

```json
{
    "code": 400,
    "message": "用户名已存在",
    "data": null
}
```

### 常见错误码

| 错误码 | 说明 |
|--------|------|
| 400 | 请求参数错误 |
| 401 | 未认证或Token无效 |
| 403 | 权限不足 |
| 404 | 资源不存在 |
| 500 | 服务器内部错误 |

### 常见错误消息

- `用户名已存在` - 创建用户时用户名重复
- `用户不存在` - 查询的用户ID不存在
- `角色不存在` - 指定的角色ID无效
- `上传文件不能为空` - 上传头像时未提供文件
- `权限不足` - 当前用户无权访问该接口
