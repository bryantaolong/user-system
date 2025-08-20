package com.bryan.system.domain.converter;

import com.bryan.system.domain.enums.SysUserStatusEnum;
import com.bryan.system.domain.entity.SysUser;
import com.bryan.system.domain.vo.SysUserExportVO;

/**
 * UserConvert
 *
 * @author Bryan Long
 */
public class SysUserConverter {

    public static SysUserExportVO toExportVO(SysUser sysUser) {
        if (sysUser == null) {
            return null;
        }

        return SysUserExportVO.builder()
                .id(sysUser.getId())
                .username(sysUser.getUsername())
                .phone(sysUser.getPhone())
                .email(sysUser.getEmail())
                .status(convertStatus(sysUser.getStatus()))
                .roles(sysUser.getRoles())
                .lastLoginAt(sysUser.getLastLoginAt())
                .lastLoginIp(sysUser.getLastLoginIp())
                .passwordResetAt(sysUser.getPasswordResetAt())
                .loginFailCount(sysUser.getLoginFailCount())
                .lockedAt(sysUser.getLockedAt())
                .deleted(convertDeletedStatus(sysUser.getDeleted()))
                .createdAt(sysUser.getCreatedAt())
                .createdBy(sysUser.getCreatedBy())
                .updatedAt(sysUser.getUpdatedAt())
                .updatedBy(sysUser.getUpdatedBy())
                .build();
    }

    private static String convertStatus(SysUserStatusEnum status) {
        if (status == null) return "";
        return switch (status) {
            case NORMAL -> "正常";
            case BANNED -> "封禁";
            case LOCKED -> "锁定";
            // 如果以后再加枚举，保留 default 分支
            default -> "未知";
        };
    }

    private static String convertDeletedStatus(Integer deleted) {
        if (deleted == null) return "";
        return deleted == 0 ? "未删除" : "已删除";
    }
}
