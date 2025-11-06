package com.zhongxin.sonicshelf.controller;

import com.zhongxin.sonicshelf.entity.Comment;
import com.zhongxin.sonicshelf.service.CommentService;
import com.zhongxin.sonicshelf.util.Result;
import jakarta.annotation.Resource;
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
                              @RequestParam(required = false) Long targetId){

        if (pageNum == null || pageNum < 1) {
            return Result.success(commentService.findCommentsByTargetTypeAndTargetId(targetType,targetId));
        }

        return Result.success(commentService.findCommentsByTargetTypeAndTargetIdAsPage(pageNum,pageSize,targetType,targetId));
    }

    @PostMapping("")
    public Result addComment(@RequestBody Comment comment){
        commentService.addComment(comment);
        return Result.success("评论成功");
    }

}
