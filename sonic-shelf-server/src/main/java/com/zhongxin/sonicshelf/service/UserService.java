package com.zhongxin.sonicshelf.service;

import com.zhongxin.sonicshelf.dto.request.UserRegisterRequest;

public interface UserService {
    public void register(UserRegisterRequest userRegisterRequest);
}