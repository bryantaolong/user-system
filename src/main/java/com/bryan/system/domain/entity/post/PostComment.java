package com.bryan.system.domain.entity.post;

import com.bryan.system.domain.enums.post.CommentStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * PostComment
 *
 * @author Bryan Long
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostComment implements Serializable {

    private Long id;

    private Long postId;

    private Long rootId;

    private Long parentId;

    private Integer type;

    private String content;

    private Long replyToUserId;

    private Integer floor;

    private Long likeCount;

    private Long dislikeCount;

    private Long childCount;

    private String path;

    private CommentStatusEnum status;

    /* ======== 通用字段 ======== */
    private Integer deleted;

    private Integer version;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private String createdBy;

    private String updatedBy;
}
