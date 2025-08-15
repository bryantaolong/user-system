-- user
create sequence "user_id_seq";

create table "user"
(
    id                  bigint  default nextval('user_id_seq'::regclass) not null
        primary key,
    username            varchar(255)                                     not null,
    password            varchar(255)                                     not null,
    phone_number        varchar(50),
    email               varchar(255),
    status              integer default 0,
    roles               varchar(255),
    last_login_time     timestamp,
    last_login_ip       varchar(255),
    password_reset_at   timestamp,
    login_fail_count    integer default 0,
    locked_at           timestamp,
    deleted             integer default 0,
    version             integer default 0,
    created_at          timestamp                                        not null,
    created_by          varchar(255),
    updated_at          timestamp,
    updated_by          varchar(255)
);

comment on table "user" is '用户表，存储系统用户的基本信息、认证信息和状态';
comment on column "user".id is '用户ID，主键，自增长';
comment on column "user".username is '用户名，用于登录的唯一标识';
comment on column "user".password is '加密后的用户密码';
comment on column "user".phone_number is '用户手机号码';
comment on column "user".email is '用户电子邮箱';
comment on column "user".status is '用户状态(0-正常 1-禁用 2-锁定)';
comment on column "user".roles is '用户角色，多个角色用逗号分隔';
comment on column "user".last_login_time is '最后一次登录时间';
comment on column "user".last_login_ip is '最后一次登录IP地址';
comment on column "user".password_reset_at is '密码重置时间';
comment on column "user".login_fail_count is '登录失败次数';
comment on column "user".locked_at is '账户锁定时间';
comment on column "user".deleted is '软删除标记(0-未删除 1-已删除)';
comment on column "user".version is '乐观锁版本号';
comment on column "user".created_at is '记录创建时间';
comment on column "user".updated_at is '记录更新时间';
comment on column "user".created_by is '记录创建人';
comment on column "user".updated_by is '记录更新人';

create index idx_user_username
    on "user" (username);

comment on index idx_user_username is '用户名索引，用于加速用户名查询';

-- user_role
create table user_role
(
    id         integer   default nextval('user_role_id_seq'::regclass) not null
        primary key,
    role_name  varchar(50)                                             not null,
    is_default boolean   default false                                 not null,
    deleted    integer   default 0,
    version    integer   default 0,
    created_at timestamp default now()                                 not null,
    updated_at timestamp,
    created_by varchar(255),
    updated_by varchar(255)
);

comment on table user_role is '用户角色表，存储角色权限';
comment on column user_role.id is '用户ID，关联user表的主键';
comment on column user_role.role_name is '用户真实姓名';
comment on column user_role.deleted is '软删除标记(0-未删除 1-已删除)';
comment on column user_role.version is '乐观锁版本号';
comment on column user_role.created_at is '记录创建时间';
comment on column user_role.updated_at is '记录更新时间';
comment on column user_role.created_by is '记录创建人';
comment on column user_role.updated_by is '记录更新人';

create unique index uk_user_role_default_true
    on user_role (is_default)
    where (is_default = true);


-- user_profile
CREATE TABLE "user_profile" (
                                user_id             BIGINT PRIMARY KEY,
                                real_name           VARCHAR(255),
                                gender              INTEGER,
                                birthday            TIMESTAMP,
                                avatar              VARCHAR(255),
                                deleted             integer default 0,
                                version             integer default 0,
                                created_at          timestamp                                        not null,
                                updated_at          timestamp,
                                created_by          varchar(255),
                                updated_by          varchar(255)
);

COMMENT ON TABLE "user_profile" IS '用户资料表，存储用户的详细信息';
COMMENT ON COLUMN "user_profile".user_id IS '用户ID，关联user表的主键';
COMMENT ON COLUMN "user_profile".real_name IS '用户真实姓名';
COMMENT ON COLUMN "user_profile".gender IS '性别(0-未知 1-男 2-女)';
COMMENT ON COLUMN "user_profile".birthday IS '用户生日';
COMMENT ON COLUMN "user_profile".avatar IS '用户头像URL';
comment on column "user_profile".deleted is '软删除标记(0-未删除 1-已删除)';
comment on column "user_profile".version is '乐观锁版本号';
COMMENT ON COLUMN "user_profile".created_at IS '记录创建时间';
COMMENT ON COLUMN "user_profile".updated_at IS '记录更新时间';
COMMENT ON COLUMN "user_profile".created_by IS '记录创建人';
COMMENT ON COLUMN "user_profile".updated_by IS '记录更新人';

