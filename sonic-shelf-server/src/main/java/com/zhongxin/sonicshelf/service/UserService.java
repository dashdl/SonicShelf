package com.zhongxin.sonicshelf.service;

import com.zhongxin.sonicshelf.dto.request.RegisterRequest;
import com.zhongxin.sonicshelf.dto.response.LoginResponse;
import com.zhongxin.sonicshelf.dto.response.RegisterResponse;
import com.zhongxin.sonicshelf.entity.User;
import jakarta.servlet.http.HttpServletRequest;

public interface UserService {
    RegisterResponse register(RegisterRequest registerRequest);

    LoginResponse login(User user);


    Object refresh(HttpServletRequest request);
}