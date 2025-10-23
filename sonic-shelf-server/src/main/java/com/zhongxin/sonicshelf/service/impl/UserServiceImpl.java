package com.zhongxin.sonicshelf.service.impl;

import com.zhongxin.sonicshelf.dto.request.UserRegisterRequest;
import com.zhongxin.sonicshelf.entity.User;
import com.zhongxin.sonicshelf.exception.AuthException;
import com.zhongxin.sonicshelf.mapper.UserMapper;
import com.zhongxin.sonicshelf.service.UserService;
import jakarta.annotation.Resource;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService, UserDetailsService {

    @Resource
    UserMapper userMapper;

    @Override
    public void register(UserRegisterRequest userRegisterRequest) {
        User user = userMapper.findByUsername(userRegisterRequest.getUsername());
        if (user != null) {
            throw new AuthException("1004","用户名已存在");
        }
        user =userMapper.findByEmail(userRegisterRequest.getEmail());
        if (user != null) {
            throw new AuthException("1003","邮箱已存在");
        }
        userMapper.insertUser(userRegisterRequest.dtoToUser());
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userMapper.findByEmail(username);
    }
}
