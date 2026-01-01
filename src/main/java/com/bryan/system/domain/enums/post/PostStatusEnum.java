package com.bryan.system.domain.enums.post;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * PostStatusEnum
 *
 * @author Bryan Long
 */
@Getter
@AllArgsConstructor
public enum PostStatusEnum {
    PUBLISHED(1,"已发布"),
    DRAFT(2,"草稿"),
    PRIVATE(3,"仅自己可见"),
    AUDITING(4,"审核中"),
    RECYCLED(5,"回收站");

    private final int code;
    private final String desc;

    public static PostStatusEnum of(int code) {
        for (PostStatusEnum e : values()) {
            if (e.code == code) return e;
        }
        throw new IllegalArgumentException("unknown post status " + code);
    }
}
