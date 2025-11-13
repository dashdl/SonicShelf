package com.zhongxin.sonicshelf.controller;

import com.zhongxin.sonicshelf.service.LikeService;
import com.zhongxin.sonicshelf.util.Result;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/like")
public class LikeController {

    @Resource
    private LikeService likeService;

    @PostMapping("")
    public Result like(@RequestParam(required = false) String targetType,
                       @RequestParam(required = false) Long targetId) {
        try{
            likeService.like(targetType,targetId);
        }catch (RuntimeException e){
            return Result.error("点赞失败");
        }
        return Result.success();
    }

    @DeleteMapping("")
    public Result unLike(@RequestParam(required = false) String targetType,
                       @RequestParam(required = false) Long targetId) {
        try{
            likeService.unLike(targetType,targetId);
        }catch (RuntimeException e){
            return Result.error("取消点赞失败");
        }
        return Result.success();
    }
}
