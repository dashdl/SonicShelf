package com.zhongxin.sonicshelf.service;

import com.github.pagehelper.PageInfo;
import com.zhongxin.sonicshelf.dto.response.CommentCountResponse;
import com.zhongxin.sonicshelf.dto.response.CommentManageResponse;
import com.zhongxin.sonicshelf.dto.response.CommentResponse;
import com.zhongxin.sonicshelf.entity.Comment;

import java.util.List;

public interface CommentService {
    List<CommentResponse> findCommentsByTargetTypeAndTargetId(String targetType, Long targetId);

    PageInfo<CommentResponse> findCommentsByTargetTypeAndTargetIdAsPage(Integer pageNum, Integer pageSize, String targetType, Long targetId);

    void addComment(Comment comment);

    void deleteCommentById(Long commentId);

    boolean isCommentFromUser(String targetType, Long targetId, Long commentId);

    Integer countCommentCount();

    CommentCountResponse countDistribution();

    PageInfo<CommentManageResponse> findCommentsAspage(Integer pageNum, Integer pageSize, String keyword, String targetType, String startDate, String endDate);

    void adminDeleteCommentById(Long id);

    void batchDeleteCommentById(Long[] ids);

    CommentManageResponse findCommentById(Long id);
}
