package com.zhongxin.sonicshelf.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zhongxin.sonicshelf.dto.response.CommentResponse;
import com.zhongxin.sonicshelf.entity.Comment;
import com.zhongxin.sonicshelf.exception.CustomException;
import com.zhongxin.sonicshelf.mapper.CommentMapper;
import com.zhongxin.sonicshelf.service.CommentService;
import com.zhongxin.sonicshelf.service.LikeService;
import com.zhongxin.sonicshelf.util.CurrentUserUtil;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentServiceImpl implements CommentService {

    @Resource
    CommentMapper commentMapper;
    @Resource
    LikeService likeService;

    @Override
    public List<CommentResponse> findCommentsByTargetTypeAndTargetId(String targetType, Long targetId) {

        List<CommentResponse> commentList = commentMapper.findCommentsByTargetTypeAndTargetId(targetType, targetId);

        for (CommentResponse comment : commentList) {

            if (likeService.findByTargetIdAndTargetType("comment", comment.getId()) != null) comment.setLike(true);

            if (comment.getParentId() != null) {
                try {
                    comment.setParentComment(commentMapper.findCommentById(comment.getParentId()));
                } catch (RuntimeException e) {
                    throw new CustomException("未找到被您回复的评论");
                }

            }
        }

        return commentList;
    }

    @Override
    public PageInfo<CommentResponse> findCommentsByTargetTypeAndTargetIdAsPage(Integer pageNum, Integer pageSize, String targetType, Long targetId) {
        PageHelper.startPage(pageNum, pageSize);
        return PageInfo.of(this.findCommentsByTargetTypeAndTargetId(targetType, targetId));
    }

    @Override
    public void addComment(Comment comment) {
        comment.setUserId(CurrentUserUtil.getCurrentUserId());
        commentMapper.addComment(comment);
    }

    @Override
    public void deleteCommentById(Long commentId) {
        commentMapper.deleteById(commentId);
    }

    @Override
    public boolean isCommentFromUser(String targetType, Long targetId, Long commentId) {
        try {
            Comment comment = commentMapper.selectByCommentId(commentId);
            if (comment == null) {
                return false;
            }else {
                Comment commentRequest = new Comment();
                commentRequest.setUserId(CurrentUserUtil.getCurrentUserId());
                commentRequest.setTargetType(targetType);
                commentRequest.setTargetId(targetId);
                commentRequest.setId(commentId);
                return comment.equals(commentRequest);
            }
        } catch (RuntimeException e) {
            throw new CustomException("评论不存在");
        }
    }

}
