package com.zhongxin.sonicshelf.service;

import com.github.pagehelper.PageInfo;
import com.zhongxin.sonicshelf.dto.response.CommentResponse;
import com.zhongxin.sonicshelf.entity.Comment;

import java.util.List;

public interface CommentService {
    List<CommentResponse> findCommentsByTargetTypeAndTargetId(String targetType, Long targetId);

    PageInfo<CommentResponse> findCommentsByTargetTypeAndTargetIdAsPage(Integer pageNum, Integer pageSize, String targetType, Long targetId);

    void addComment(Comment comment);
}
