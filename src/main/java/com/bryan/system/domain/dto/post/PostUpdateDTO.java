package com.bryan.system.domain.dto.post;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * PostDTO
 *
 * @author Bryan Long
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostUpdateDTO {

    private Long    id;

    private String  title;

    private String  content;

    private Long categoryId;

    private String tags;

    private Integer commentStatus;

    private Integer weight;
}