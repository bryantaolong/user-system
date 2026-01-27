package com.bryan.system.interceptor;

import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.Properties;

/**
 * MyBatis 审计字段自动填充拦截器
 * 在 INSERT / UPDATE 执行前，自动注入创建人、创建时间、更新人、更新时间。
 *
 * @author Bryan Long
 */
@Intercepts({
        @Signature(type = Executor.class,
                method = "update",
                args = {MappedStatement.class, Object.class})
})
public class MyBatisAuditFieldInterceptor implements Interceptor {

    /**
     * 拦截 Executor.update 方法
     *
     * @param invocation 调用上下文
     * @return 原方法返回值
     * @throws Throwable 继续抛出原异常
     */
    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        MappedStatement ms = (MappedStatement) invocation.getArgs()[0];
        Object parameter = invocation.getArgs()[1];

        // 只处理 INSERT / UPDATE
        if (ms.getSqlCommandType() == SqlCommandType.INSERT) {
            fillInsert(parameter);
        } else if (ms.getSqlCommandType() == SqlCommandType.UPDATE) {
            fillUpdate(parameter);
        }
        return invocation.proceed();
    }

    /**
     * 填充插入审计字段
     *
     * @param param 实体或参数 Map
     */
    private void fillInsert(Object param) {
        MetaObject meta = SystemMetaObject.forObject(param);
        LocalDateTime now = LocalDateTime.now();
        setValue(meta, "createdAt", now);
        setValue(meta, "updatedAt", now);
        setValue(meta, "createdBy", this.currentUser());
        setValue(meta, "updatedBy", this.currentUser());
        setValue(meta, "deleted", 0);
        setValue(meta, "version", 0);
    }

    /**
     * 填充更新审计字段
     *
     * @param param 实体或参数 Map
     */
    private void fillUpdate(Object param) {
        MetaObject meta = SystemMetaObject.forObject(param);
        setValue(meta, "updatedAt", LocalDateTime.now());
        setValue(meta, "updatedBy", this.currentUser());
        this.incrementVersion(meta);
    }

    /**
     * 递增 version 字段
     *
     * @param meta 元对象
     */
    private void incrementVersion(MetaObject meta) {
        if (meta.hasGetter("version")) {
            Object currentVersion = meta.getValue("version");
            if (currentVersion instanceof Integer) {
                meta.setValue("version", ((Integer) currentVersion) + 1);
            } else if (currentVersion == null) {
                meta.setValue("version", 1);
            }
        }
    }

    /**
     * 安全设置属性值
     *
     * @param meta     元对象
     * @param property 属性名
     * @param value    属性值
     */
    private void setValue(MetaObject meta, String property, Object value) {
        if (meta.hasSetter(property)) {
            meta.setValue(property, value);
        }
    }

    /**
     * 获取当前登录用户名
     *
     * @return 用户名；未登录则返回 "SYSTEM"
     */
    private String currentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth.getName().equals("anonymousUser")) {
            return "SYSTEM";
        }
        return auth.getName();
    }

    /**
     * 生成代理对象
     *
     * @param target 被代理对象
     * @return 代理对象
     */
    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    /**
     * 接收配置属性（当前未使用）
     *
     * @param properties 配置项
     */
    @Override
    public void setProperties(Properties properties) {
        // 预留扩展
    }
}
