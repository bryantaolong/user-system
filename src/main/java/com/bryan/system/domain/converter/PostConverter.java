package com.bryan.system.domain.converter;

import com.bryan.system.domain.entity.post.Post;
import com.bryan.system.domain.enums.post.CommentAreaStatusEnum;
import com.bryan.system.domain.vo.post.PostVO;

/**
 * PostConverter
 *
 * @author Bryan Long
 */
public class PostConverter {

    public static PostVO toPostVO(Post post) {
        if (post == null) {
            return null;
        }

        return PostVO.builder()
                .id(post.getId())
                .userId(post.getUserId())
                .author(post.getCreatedBy())
                .title(post.getTitle())
                .content(post.getContent())
                .categoryId(post.getCategoryId())
                .tags(post.getTags())
                .commentAreaStatus(convertStatus(post.getCommentAreaStatus()))
                .viewCount(post.getViewCount())
                .likeCount(post.getLikeCount())
                .commentCount(post.getCommentCount())
                .collectCount(post.getCollectCount())
                .shareCount(post.getShareCount())
                .build();
    }

    private static String convertStatus(CommentAreaStatusEnum status) {
        if (status == null) return "";
        return switch (status) {
            case OPEN -> "开启";
            case CLOSED -> "关闭";
            // 如果以后再加枚举，保留 default 分支
            default -> "未知";
        };
    }

    private static String convertDeletedStatus(Integer deleted) {
        if (deleted == null) return "";
        return deleted == 0 ? "未删除" : "已删除";
    }
}
