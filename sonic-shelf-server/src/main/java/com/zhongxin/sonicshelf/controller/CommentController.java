package com.zhongxin.sonicshelf.controller;

import com.zhongxin.sonicshelf.entity.Comment;
import com.zhongxin.sonicshelf.exception.CustomException;
import com.zhongxin.sonicshelf.service.CommentService;
import com.zhongxin.sonicshelf.util.Result;
import jakarta.annotation.Resource;
import org.apache.ibatis.annotations.Delete;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/comments")
public class CommentController {

    @Resource
    private CommentService commentService;

    @GetMapping("")
    public Result getComments(@RequestParam(required = false) Integer pageNum,
                              @RequestParam(required = false) Integer pageSize,
                              @RequestParam(required = false) String targetType,
                              @RequestParam(required = false) Long targetId) {

        if (pageNum == null || pageNum < 1) {
            return Result.success(commentService.findCommentsByTargetTypeAndTargetId(targetType, targetId));
        }

        return Result.success(commentService.findCommentsByTargetTypeAndTargetIdAsPage(pageNum, pageSize, targetType, targetId));
    }

    @PostMapping("")
    public Result addComment(@RequestBody Comment comment) {
        commentService.addComment(comment);
        return Result.success("评论成功");
    }

    @DeleteMapping("")
    public Result deleteComment(@RequestParam(required = false) String targetType,
                                @RequestParam(required = false) Long targetId,
                                @RequestParam(required = false) Long commentId) {
        if (commentId == null) throw new CustomException("评论不存在");
        if (commentService.isCommentFromUser(targetType, targetId, commentId)) {
            try {
                commentService.deleteCommentById(commentId);
            } catch (Exception e) {
                throw new CustomException("删除失败");
            }
        } else {
            throw new CustomException("删除失败");
        }

        return Result.success("删除成功");
    }

}
