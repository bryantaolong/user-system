package com.bryan.system.model.request;

import lombok.Getter;

/**
 * UserSearchRequest
 *
 * @author Bryan Long
 * @version 1.0
 * @since 2025/7/26
 */
@Getter
public class UserSearchRequest {
    private String username;

    private String phoneNumber;

    private String email;

    private Integer gender;

    private Integer status;
}