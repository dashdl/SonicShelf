package com.zhongxin.sonicshelf.service;

import com.github.pagehelper.PageInfo;
import com.zhongxin.sonicshelf.dto.request.RegisterRequest;
import com.zhongxin.sonicshelf.dto.response.*;
import com.zhongxin.sonicshelf.entity.User;
import com.zhongxin.sonicshelf.mapper.MusicMapper;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;

public interface UserService {
    RegisterResponse register(RegisterRequest registerRequest);

    LoginResponse login(User user);


    Object refresh(HttpServletRequest request);

    UserProfileResponse getUserProfile(String token);

    Map<String,Object> updateUserProfile(String token, User user);

    void updateUserAvatar(String url,String token);

    UserProfileResponse findUserById(Long id);

    PageInfo<UserManageResponse> findUsersAsPage(Integer pageNum, Integer pageSize, String keyword, Integer status);

    UserManageResponse updateUser(RegisterRequest user);

    UserManageResponse addUser(RegisterRequest user);

    UserManageResponse updateUserStatus(Long id, byte status);

    void deleteUser(Long id);
}