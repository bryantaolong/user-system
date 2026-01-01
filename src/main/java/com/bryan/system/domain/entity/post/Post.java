package com.bryan.system.domain.entity.post;

import com.bryan.system.domain.enums.post.CommentAreaStatusEnum;
import com.bryan.system.domain.enums.post.PostStatusEnum;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Post
 *
 *
 * @author Bryan Long
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Post implements Serializable {

    private Long id;

    private Long userId;

    private String title;

    private String content;

    private PostStatusEnum status;

    private Long categoryId;

    private String tags;

    private CommentAreaStatusEnum commentAreaStatus;

    // count numbers
    private Long viewCount;

    private Long likeCount;

    private Long commentCount;

    private Long collectCount;

    private Long shareCount;

    private Integer weight;

    /* ======== 通用字段 ======== */
    private Integer deleted;

    private Integer version;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private String createdBy;

    private String updatedBy;
}
