package com.bryan.system.config;

import com.bryan.system.interceptor.MyBatisAuditFieldInterceptor;
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
    public MyBatisAuditFieldInterceptor auditFieldInterceptor() {
        return new MyBatisAuditFieldInterceptor();
    }
}
