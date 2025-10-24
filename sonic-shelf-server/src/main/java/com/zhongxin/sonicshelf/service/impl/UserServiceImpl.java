package com.zhongxin.sonicshelf.service.impl;

import com.zhongxin.sonicshelf.dto.request.RegisterRequest;
import com.zhongxin.sonicshelf.dto.response.LoginResponse;
import com.zhongxin.sonicshelf.dto.response.RegisterResponse;
import com.zhongxin.sonicshelf.entity.User;
import com.zhongxin.sonicshelf.exception.AuthException;
import com.zhongxin.sonicshelf.exception.CustomException;
import com.zhongxin.sonicshelf.mapper.UserMapper;
import com.zhongxin.sonicshelf.service.UserService;
import com.zhongxin.sonicshelf.util.JwtUtil;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class UserServiceImpl implements UserService, UserDetailsService {

    @Resource
    UserMapper userMapper;
    
    @Autowired
    JwtUtil jwtUtil;

    @Override
    public RegisterResponse register(RegisterRequest registerRequest) {
        User user = userMapper.findByUsername(registerRequest.getUsername());
        if (user != null) {
            throw new AuthException("1004","用户名已存在");
        }
        user =userMapper.findByEmail(registerRequest.getEmail());
        if (user != null) {
            throw new AuthException("1003","邮箱已存在");
        }

        RegisterResponse registerResponse = new RegisterResponse(registerRequest.toUser());
        registerResponse.setUser_id(userMapper.insert(registerRequest.toUser()));

        return registerResponse;
    }

    @Override
    public LoginResponse login(User user) {
        User tempUser = userMapper.findByUsername(user.getUsername());
        if (tempUser==null){
            throw new CustomException("1001","用户不存在");
        }
        if(!tempUser.getPassword().equals(user.getPassword())){
            throw new CustomException("1002","账号或密码错误");
        }

        LoginResponse loginResponse = new LoginResponse(tempUser);
        loginResponse.setAccess_token(jwtUtil.generateToken(tempUser));

        return loginResponse;
    }

    @Override
    public Map<String, Object> refresh(HttpServletRequest request) {
        final String authorizationHeader = request.getHeader("Authorization");

        String jwt = null;
        User user = null;

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwt = authorizationHeader.substring(7);
            String username = jwtUtil.getUsernameFromToken(jwt);
            if (username != null) {
                user = userMapper.findByUsername(username);
            }
        }
        
        if(user==null){
            throw new CustomException("401","未授权(Token无效或过期)");
        }


        Map<String, Object> responseMap = new LinkedHashMap<>();
        responseMap.put("access_token",jwtUtil.generateToken(user));
        responseMap.put("expires_in",3600);
        return responseMap;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userMapper.findByUsername(username);
    }
}
