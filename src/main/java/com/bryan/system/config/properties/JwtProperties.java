package com.bryan.system.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * JWT 配置属性类
 * 用于从配置文件中读取 JWT 相关配置，避免硬编码。
 *
 * @author Bryan Long
 */
@Component
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {

    /**
     * JWT 签名密钥
     * 建议至少 256 位（32 字符）
     */
    private String secretKey;

    /**
     * JWT Token 过期时间（毫秒）
     * 默认 24 小时
     */
    private Long expirationMs = 86400000L;

    /**
     * JWT Token 前缀
     * 默认 "Bearer "
     */
    private String tokenPrefix = "Bearer ";

    /**
     * 获取 JWT 签名密钥
     *
     * @return 密钥字符串
     */
    public String getSecretKey() {
        return secretKey;
    }

    /**
     * 设置 JWT 签名密钥
     *
     * @param secretKey 密钥字符串
     */
    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    /**
     * 获取 Token 过期时间（毫秒）
     *
     * @return 过期时间毫秒值
     */
    public Long getExpirationMs() {
        return expirationMs;
    }

    /**
     * 设置 Token 过期时间（毫秒）
     *
     * @param expirationMs 过期时间毫秒值
     */
    public void setExpirationMs(Long expirationMs) {
        this.expirationMs = expirationMs;
    }

    /**
     * 获取 Token 前缀
     *
     * @return 前缀字符串
     */
    public String getTokenPrefix() {
        return tokenPrefix;
    }

    /**
     * 设置 Token 前缀
     *
     * @param tokenPrefix 前缀字符串
     */
    public void setTokenPrefix(String tokenPrefix) {
        this.tokenPrefix = tokenPrefix;
    }
}