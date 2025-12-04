package com.zhongxin.sonicshelf.controller;

import com.zhongxin.sonicshelf.dto.response.FollowingResponse;
import com.zhongxin.sonicshelf.exception.CustomException;
import com.zhongxin.sonicshelf.service.FollowService;
import com.zhongxin.sonicshelf.util.CurrentUserUtil;
import com.zhongxin.sonicshelf.util.Result;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @GetMapping("")
    public Result getAll() {

        Long id = null;
        try {
            id = CurrentUserUtil.getCurrentUserId();
        } catch (Exception e) {
            throw new CustomException("1003", "请先登录");
        }

        return Result.success(followService.findAll(id, ""));
    }

    @GetMapping("/{type}")
    public Result getAllByType(@PathVariable String type) {

        Long id = null;
        try {
            id = CurrentUserUtil.getCurrentUserId();
        } catch (Exception e) {
            throw new CustomException("1003", "请先登录");
        }

        return Result.success(followService.findAll(id, type));
    }

    @GetMapping("/follower")
    public Result getAllFollower() {

        Long id = null;
        try {
            id = CurrentUserUtil.getCurrentUserId();
        } catch (Exception e) {
            throw new CustomException("1003", "请先登录");
        }

        return Result.success(followService.findAllFollower(id));
    }
}
