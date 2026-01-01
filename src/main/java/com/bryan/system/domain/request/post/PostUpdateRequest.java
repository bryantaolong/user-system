package com.bryan.system.domain.request.post;

import lombok.Getter;

/**
 * PostUpdateRequest
 *
 * @author Bryan Long
 */
@Getter
public class PostUpdateRequest {

    private Long    id;

    private String  title;

    private String  content;

    private Long categoryId;

    private String tags;

    private Integer commentStatus;

    private Integer weight;
}
