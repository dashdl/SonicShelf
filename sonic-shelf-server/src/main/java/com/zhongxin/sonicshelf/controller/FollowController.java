package com.zhongxin.sonicshelf.controller;

import com.zhongxin.sonicshelf.service.FollowService;
import com.zhongxin.sonicshelf.util.Result;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/follows")
public class FollowController {

    @Resource
    private FollowService followService;

    @PostMapping("/follow/{id}")
    public Result addFavoriteAlbum(@PathVariable("id") Long id) {
        followService.followById(id);
        return Result.success("关注成功");
    }

    @DeleteMapping("/unfollow/{id}")
    public Result unfollow(@PathVariable("id") Long id) {
        followService.unFollowById(id);
        return Result.success("取消关注");
    }
}
