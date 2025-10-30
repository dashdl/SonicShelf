package com.zhongxin.sonicshelf.controller;


import com.zhongxin.sonicshelf.entity.User;
import com.zhongxin.sonicshelf.service.UserService;
import com.zhongxin.sonicshelf.util.Result;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
public class UsersController {

    @Resource
    UserService userService;

    @GetMapping("/profile")
    public Result profile(HttpServletRequest request) {

        final String authorizationHeader = request.getHeader("Authorization");
        String token = null;
        if (authorizationHeader.startsWith("Bearer ")) {
            token = authorizationHeader.substring(7);
        }

        return Result.success(userService.getUserProfile(token));
    }

    @PutMapping("/profile")
    public Result updateUserProfile(@RequestBody User user, HttpServletRequest request) {
        final String authorizationHeader = request.getHeader("Authorization");
        String token = null;
        if (authorizationHeader.startsWith("Bearer ")) {
            token = authorizationHeader.substring(7);
        }
        return Result.success(userService.updateUserProfile(token,user));
    }
}
