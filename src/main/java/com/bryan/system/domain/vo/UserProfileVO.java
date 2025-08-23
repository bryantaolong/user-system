package com.bryan.system.domain.vo;

import com.bryan.system.domain.enums.GenderEnum;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * UserProfileVO
 *
 * @author Bryan Long
 */
@Data
@Builder
public class UserProfileVO {

    private String username;

    private String phone;

    private String email;

    private String realName;

    private GenderEnum gender;

    private LocalDateTime birthday;

    private String avatar;
}
