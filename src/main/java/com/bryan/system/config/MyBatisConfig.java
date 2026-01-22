package com.bryan.system.config;

import com.bryan.system.interceptor.MyBatisAuditFieldInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * MyBatis 全局配置类
 * 负责注册 Mapper 扫描路径、事务管理以及审计字段拦截器。
 *
 * @author Bryan Long
 */
@Configuration
@MapperScan("com.bryan.system.mapper")
@EnableTransactionManagement
public class MyBatisConfig {

    /**
     * 注册审计字段拦截器
     * 在 MyBatis 执行插入/更新时自动注入创建人、创建时间、更新人、更新时间等公共字段。
     *
     * @return MyBatisAuditFieldInterceptor 实例
     */
    @Bean
    public MyBatisAuditFieldInterceptor auditFieldInterceptor() {
        return new MyBatisAuditFieldInterceptor();
    }
}