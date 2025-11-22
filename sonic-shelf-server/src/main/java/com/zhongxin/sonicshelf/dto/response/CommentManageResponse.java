package com.zhongxin.sonicshelf.dto.response;

import lombok.Data;

@Data
public class CommentManageResponse {
    private Long id;
    private String content;
    private String nickname;
    private String avatar;
    private String targetType;
    private Long parentId;
    private Integer likeCount;
    private Integer replyCount;
    private Byte status;
    private String createdAt;
}
