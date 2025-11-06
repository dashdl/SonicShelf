package com.zhongxin.sonicshelf.dto.response;

import lombok.Data;

@Data
public class CommentResponse {
    private Long id;
    private Long userId;
    private String nickname;
    private String avatar;
    private String content;
    private String createdAt;
    private Long parentId;
    private Long likeCount;
    private boolean isLike;
    private CommentResponse parentComment;
}
