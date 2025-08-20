package com.bryan.system.config;

import com.bryan.system.handler.AuditFieldInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * MyBatisPlusConfig
 *
 * @author Bryan Long
 */
@Configuration
@MapperScan("com.bryan.system.mapper")
@EnableTransactionManagement
public class MyBatisConfig {

    @Bean
    public AuditFieldInterceptor auditFieldInterceptor() {
        return new AuditFieldInterceptor();
    }
}
