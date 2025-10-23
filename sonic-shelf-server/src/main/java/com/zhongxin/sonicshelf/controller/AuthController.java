package com.zhongxin.sonicshelf.controller;

import com.zhongxin.sonicshelf.dto.request.UserRegisterRequest;
import com.zhongxin.sonicshelf.dto.response.Result;
import com.zhongxin.sonicshelf.entity.User;
import com.zhongxin.sonicshelf.service.UserService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Resource
    private UserService userService;

    @PostMapping("/register")
    public Result register(@RequestBody UserRegisterRequest userRegisterRequest) {

        userService.register(userRegisterRequest);
        return Result.success("注册成功");
    }

}
