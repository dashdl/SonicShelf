package com.zhongxin.sonicshelf.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zhongxin.sonicshelf.annotation.AdminAuth;
import com.zhongxin.sonicshelf.dto.response.CommentManageResponse;
import com.zhongxin.sonicshelf.dto.response.MusicManageResponse;
import com.zhongxin.sonicshelf.entity.Comment;
import com.zhongxin.sonicshelf.exception.CustomException;
import com.zhongxin.sonicshelf.service.CommentService;
import com.zhongxin.sonicshelf.util.Result;
import jakarta.annotation.Resource;
import org.apache.ibatis.annotations.Param;
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

    @AdminAuth
    @GetMapping("/count")
    public Result getCommentCount() {
        return Result.success(commentService.countCommentCount());
    }

    @AdminAuth
    @GetMapping("/distribution")
    public Result getCommentDistribution() {
        return Result.success(commentService.countDistribution());
    }

    @AdminAuth
    @GetMapping("/getAll")
    public Result getAllComments(@RequestParam Integer pageNum,
                                 @RequestParam Integer pageSize,
                                 @RequestParam(required = false) String keyword,
                                 @RequestParam(required = false) String targetType,
                                 @RequestParam(required = false) String startDate,
                                 @RequestParam(required = false) String endDate) {

        PageInfo<CommentManageResponse> cmr = commentService.findCommentsAspage(pageNum, pageSize, keyword, targetType, startDate, endDate);

        return Result.success(cmr);
    }

    @AdminAuth
    @DeleteMapping("/delete/{id}")
    public Result deleteComment(@PathVariable Long id) {
        commentService.adminDeleteCommentById(id);
        return Result.success();
    }

    @AdminAuth
    @DeleteMapping("/batchDelete")
    public Result batchDeleteComment(@RequestParam Long[] ids) {
        commentService.batchDeleteCommentById(ids);
        return Result.success();
    }

    @AdminAuth
    @GetMapping("/getById/{id}")
    public Result getCommentById(@PathVariable Long id) {
        return Result.success(commentService.findCommentById(id));
    }
}
