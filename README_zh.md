# 用户系统

[English README here (英文版说明)](./README.md)

## 项目简介

本项目为基于 Spring Boot 3 的用户管理系统，支持用户注册、登录、信息管理、权限控制、数据导出等功能。后端采用 PostgreSQL 作为主数据库，Redis 用于缓存和分布式场景，支持 JWT 无状态认证和基于角色的权限控制。

## 技术栈

* Java 17
* Spring Boot 3.5.4
* MyBatis
* PostgreSQL 17.x
* Redis
* Spring Security
* EasyExcel (阿里巴巴 Excel 导出)
* Lombok
* JJWT (JWT 令牌)
* Maven 3.9.x

## 项目结构

```
src/
  main/
    java/com/bryan/system/
      config/         # 配置类（安全、Redis、MyBatis-Plus等）
      controller/     # RESTful 控制器
      domain/         # 实体、请求/响应对象、VO
      filter/         # JWT 认证过滤器
      handler/        # MyBatis 自动填充、全局异常处理
      mapper/         # MyBatis mapper 接口
      service/        # 业务服务层
      util/           # 工具类（JWT、HTTP等）
    resources/
      application.yaml
      application-dev.yaml
      mapper/         # MyBatis Mapper XML 文件
      sql/            # 数据库建表脚本
  test/
    java/com/bryan/system/
      UserSystemApplicationTests.java
```

## 环境要求

* JDK 17+
* Maven 3.9.9+
* PostgreSQL 17.x
* Redis 6.x 或更高

## 配置说明

* 数据库连接、Redis 配置请在 `src/main/resources/application-dev.yaml` 中修改。
* 日志、MyBatis-Plus 逻辑删除等通用配置见 `src/main/resources/application.yaml`。
* 数据库建表脚本见 [`src/main/resources/sql/create_table.sql`](src/main/resources/sql/create_table.sql)。

## 启动方式

1. 初始化数据库（PostgreSQL），执行建表脚本：

   ```sh
   psql -U postgres -d postgres -f src/main/resources/sql/create_table.sql
   ```
2. 启动 Redis 服务。
3. 使用 Maven 构建并运行项目：

   ```sh
   ./mvnw spring-boot:run
   ```

   或直接运行打包后的 JAR：

   ```sh
   mvn clean package
   java -jar target/user-system-0.0.1-SNAPSHOT.jar
   ```

## 🐳 容器化部署 (Docker)

本项目支持使用 **Docker** 和 **Docker Compose** 进行容器化部署。

### 1. 构建项目

确保已安装 **Docker** 和 **Docker Compose**，然后打包 JAR：

```bash
mvn clean package -DskipTests
```

### 2. 创建 Dockerfile

在项目根目录下新建 `Dockerfile`：

```dockerfile
# 使用官方 OpenJDK 17 作为基础镜像
FROM eclipse-temurin:17-jdk-alpine

# 设置工作目录
WORKDIR /app

# 复制构建产物
COPY target/user-system-0.0.1-SNAPSHOT.jar app.jar

# 暴露端口
EXPOSE 8080

# 启动应用
ENTRYPOINT ["java", "-jar", "app.jar"]
```

### 3. 创建 docker-compose.yml

在项目根目录下新建 `docker-compose.yml`：

```yaml
version: "3.9"
services:
  postgres:
    image: postgres:17
    container_name: user-postgres
    environment:
      POSTGRES_USER: postgres
      POSTGRES_PASSWORD: postgres
      POSTGRES_DB: user_system
    volumes:
      - postgres_data:/var/lib/postgresql/data
      - ./src/main/resources/sql/create_table.sql:/docker-entrypoint-initdb.d/create_table.sql
    ports:
      - "5432:5432"

  redis:
    image: redis:6
    container_name: user-redis
    ports:
      - "6379:6379"

  app:
    build: .
    container_name: user-system
    depends_on:
      - postgres
      - redis
    environment:
      SPRING_DATASOURCE_URL: jdbc:postgresql://postgres:5432/user_system
      SPRING_DATASOURCE_USERNAME: postgres
      SPRING_DATASOURCE_PASSWORD: postgres
      SPRING_REDIS_HOST: redis
      SPRING_REDIS_PORT: 6379
    ports:
      - "8080:8080"

volumes:
  postgres_data:
```

### 4. 更新 Spring 配置

将 `src/main/resources/application-dev.yaml` 中的数据库和 Redis 主机名改为容器名：

```yaml
spring:
  datasource:
    url: jdbc:postgresql://postgres:5432/user_system
    username: postgres
    password: postgres
  redis:
    host: redis
    port: 6379
```

### 5. 启动服务

执行以下命令构建并启动所有服务：

```bash
docker-compose up -d --build
```

### 6. 访问应用

* 应用接口: [http://localhost:8080/api](http://localhost:8080/api)
* PostgreSQL: `localhost:5432` (用户: `postgres` / 密码: `postgres`)
* Redis: `localhost:6379`

## 常用接口

* 用户注册：`POST /api/auth/register`
* 用户登录：`POST /api/auth/login`
* 查询所有用户：`GET /api/user/all`（管理员权限）
* 用户搜索：`POST /api/user/search`
* 用户更新、角色变更、密码修改、封禁/解封、逻辑删除等详见 [`UserController`](src/main/java/com/bryan/system/controller/UserController.java)
* 用户数据导出：`GET /api/user/export/all`、`POST /api/user/export/field`（管理员权限）

## 其他说明

* JWT 密钥建议在生产环境通过配置文件注入，避免硬编码。
* 全局异常处理类见 [`GlobalExceptionHandler`](src/main/java/com/bryan/system/handler/GlobalExceptionHandler.java)。
* 逻辑删除字段为 `deleted`：0 表示未删除，1 表示已删除。

## License

本项目采用 MIT 协议。详见 [LICENSE](LICENSE)。
