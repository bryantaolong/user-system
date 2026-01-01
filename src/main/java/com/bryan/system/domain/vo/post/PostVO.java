package com.bryan.system.domain.vo.post;

import lombok.Builder;
import lombok.Data;

/**
 * PostVO
 *
 * @author Bryan Long
 */
@Data
@Builder
public class PostVO {

    private Long id;

    private Long userId;

    private String author;

    private String title;

    private String content;

    private Long categoryId;

    private String tags;

    private String commentAreaStatus;

    // count numbers
    private Long viewCount;

    private Long likeCount;

    private Long commentCount;

    private Long collectCount;

    private Long shareCount;
}
