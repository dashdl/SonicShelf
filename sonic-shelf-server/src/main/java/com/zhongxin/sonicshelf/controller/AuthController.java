package com.zhongxin.sonicshelf.controller;

import com.zhongxin.sonicshelf.dto.request.LoginRequest;
import com.zhongxin.sonicshelf.dto.request.RegisterRequest;

import com.zhongxin.sonicshelf.exception.CustomException;
import com.zhongxin.sonicshelf.service.AdminService;
import com.zhongxin.sonicshelf.service.UserService;
import com.zhongxin.sonicshelf.service.impl.UnifiedUserDetailsService;
import com.zhongxin.sonicshelf.util.JwtUtil;
import com.zhongxin.sonicshelf.util.Result;
import com.zhongxin.sonicshelf.util.TokenExtractor;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    @Resource
    private UserService userService;
    @Resource
    private AdminService adminService;
    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/register")
    public Result register(@RequestBody RegisterRequest registerRequest) {

        return Result.success("注册成功", userService.register(registerRequest));
    }

    @PostMapping("/login")
    public Result login(@RequestBody LoginRequest loginRequest) {

        return Result.success("登录成功", userService.login(loginRequest.toUser()));
    }

    @PostMapping("/refresh")
    public Result refresh(HttpServletRequest request) {

        Object obj = userService.refresh(request);

        return Result.success("Token刷新成功", obj);
    }

    @PostMapping("/admin/login")
    public Result adminLogin(@RequestBody LoginRequest loginRequest) {
        return Result.success(adminService.login(loginRequest.toAdmin()));
    }

    @GetMapping("/admin/getInfo")
    public Result getInfo(HttpServletRequest request) {

        String token = TokenExtractor.extractToken(request);

        if (TokenExtractor.isValidTokenFormat(token)) {

            return Result.success(adminService.getInfoByUsername(jwtUtil.getUsernameFromToken(token)));
        } else {
            throw new CustomException("token无效");
        }


    }
}

