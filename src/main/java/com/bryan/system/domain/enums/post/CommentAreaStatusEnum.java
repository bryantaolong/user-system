package com.bryan.system.domain.enums.post;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * CommentAreaStatusEnum
 *
 * @author Bryan Long
 */
@Getter
@AllArgsConstructor
public enum CommentAreaStatusEnum {
    OPEN(1, "开启"),
    CLOSED(2, "关闭");

    private final int code;
    private final String desc;

    public static CommentAreaStatusEnum of(int code) {
        for (CommentAreaStatusEnum e : values()) {
            if (e.code == code) return e;
        }
        throw new IllegalArgumentException("unknown comment area status " + code);
    }
}
