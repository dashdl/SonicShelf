package com.zhongxin.sonicshelf.service;

import com.zhongxin.sonicshelf.dto.request.UserRegisterRequest;
import org.springframework.stereotype.Service;

@Service
public interface UserService {
    public void register(UserRegisterRequest userRegisterRequest);
}
