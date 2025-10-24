package com.zhongxin.sonicshelf.controller;

import com.zhongxin.sonicshelf.dto.request.LoginRequest;
import com.zhongxin.sonicshelf.dto.request.RegisterRequest;
import com.zhongxin.sonicshelf.dto.response.Result;
import com.zhongxin.sonicshelf.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {



    @Resource
    private UserService userService;

    @PostMapping("/register")
    public Result register(@RequestBody RegisterRequest registerRequest) {

        return Result.success("注册成功",userService.register(registerRequest));
    }

    @PostMapping("/login")
    public Result login(@RequestBody LoginRequest loginRequest){

        return Result.success("登录成功", userService.login(loginRequest.toUser()));
    }

    @PostMapping("/refresh")
    public Result refresh(HttpServletRequest request){

        Object obj = userService.refresh(request);

        return Result.success("Token刷新成功",obj);
    }
}

