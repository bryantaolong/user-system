# User System

*[English  [README.md](README.md)]*

## 项目简介

本项目为基于 Spring Boot 3 的用户管理系统，支持用户注册、登录、信息管理、权限控制、数据导出等功能。后端采用 PostgreSQL 作为主数据库，Redis 用于缓存和分布式场景，支持 JWT 无状态认证和基于角色的权限控制。

## 技术栈

- Java 17
- Spring Boot 3.5.4
- MyBatis-Plus
- PostgreSQL 17.x
- Redis
- Spring Security
- EasyExcel (阿里巴巴 Excel 导出)
- Hutool (工具库)
- Lombok
- JJWT (JWT 令牌)
- Maven 3.9.x

## 项目结构

```
src/
  main/
    java/com/bryan/system/
      config/         # 配置类（安全、Redis、MyBatis-Plus等）
      controller/     # RESTful 控制器
      filter/         # JWT 认证过滤器
      handler/        # MyBatis 自动填充、全局异常处理
      mapper/         # MyBatis-Plus Mapper接口
      model/          # 实体、请求/响应对象、VO
      service/        # 业务服务层
      util/           # 工具类（JWT、HTTP等）
    resources/
      application.yaml
      application-dev.yaml
      sql/            # 数据库建表脚本
  test/
    java/com/bryan/system/
      UserSystemApplicationTests.java
```

## 环境要求

- JDK 17+
- Maven 3.9.9+
- PostgreSQL 17.x
- Redis 6.x 或更高

## 配置说明

- 数据库连接、Redis 配置请在 `src/main/resources/application-dev.yaml` 中修改。
- 日志、MyBatis-Plus 逻辑删除等通用配置见 `src/main/resources/application.yaml`。
- 数据库建表脚本见 [`sql/create_table.sql`](sql/create_table.sql)。

## 启动方式

1. 初始化数据库（PostgreSQL），执行建表脚本：

   ```sh
   psql -U postgres -d postgres -f sql/create_table.sql
   ```
2. 启动 Redis 服务。
3. 使用 Maven 构建并运行项目：

   ```sh
   ./mvnw spring-boot:run
   ```

   或直接运行主类：

   ```sh
   mvn clean package
   java -jar target/user-system-0.0.1-SNAPSHOT.jar
   ```

## 常用接口

- 用户注册：`POST /api/auth/register`
- 用户登录：`POST /api/auth/login`
- 查询用户：`GET /api/user/all`（管理员权限）
- 用户搜索：`POST /api/user/search`
- 用户信息更新、角色变更、密码修改、封禁/解封、逻辑删除等接口详见 [`UserController`](src/main/java/com/bryan/system/controller/UserController.java)
- 用户数据导出：`GET /api/user/export/all`、`POST /api/user/export/field`（管理员权限）

## 其他说明

- JWT 密钥建议生产环境通过配置文件注入，避免硬编码。
- 统一异常处理见 [`GlobalExceptionHandler`](src/main/java/com/bryan/system/handler/GlobalExceptionHandler.java)。
- 逻辑删除字段为 `deleted`，0 表示未删除，1 表示已删除。

## License

本项目仅供学习交流，未设置开源协议
