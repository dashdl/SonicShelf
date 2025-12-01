package com.zhongxin.sonicshelf.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zhongxin.sonicshelf.dto.request.RegisterRequest;
import com.zhongxin.sonicshelf.dto.response.*;
import com.zhongxin.sonicshelf.entity.User;
import com.zhongxin.sonicshelf.exception.AuthException;
import com.zhongxin.sonicshelf.exception.CustomException;
import com.zhongxin.sonicshelf.mapper.ArtistMapper;
import com.zhongxin.sonicshelf.mapper.FollowMapper;
import com.zhongxin.sonicshelf.mapper.UserMapper;
import com.zhongxin.sonicshelf.service.ArtistService;
import com.zhongxin.sonicshelf.service.UserService;
import com.zhongxin.sonicshelf.util.CurrentUserUtil;
import com.zhongxin.sonicshelf.util.JwtUtil;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class UserServiceImpl implements UserService {

    @Resource
    UserMapper userMapper;

    @Autowired
    JwtUtil jwtUtil;
    @Autowired
    private FollowMapper followMapper;

    @Override
    public RegisterResponse register(RegisterRequest registerRequest) {
        User user = userMapper.findByUsername(registerRequest.getUsername());
        if (user != null) {
            throw new AuthException("1004", "用户名已存在");
        }
        user = userMapper.findByEmail(registerRequest.getEmail());
        if (user != null) {
            throw new AuthException("1003", "邮箱已存在");
        }

        RegisterResponse registerResponse = new RegisterResponse(registerRequest.toUser());
        registerResponse.setUser_id(userMapper.insert(registerRequest.toUser()));

        return registerResponse;
    }

    @Override
    public LoginResponse login(User user) {
        User tempUser = userMapper.findByUsername(user.getUsername());
        if (tempUser == null) {
            throw new CustomException("1001", "用户不存在");
        }
        if (!tempUser.getPassword().equals(user.getPassword())) {
            throw new CustomException("1002", "账号或密码错误");
        }

        if (tempUser.getStatus()==0) throw new CustomException("1003", "该账号已被禁用，请等待解禁");

        LoginResponse loginResponse = new LoginResponse(tempUser);
        loginResponse.setAccess_token(jwtUtil.generateToken(tempUser));

        userMapper.updateLoginTime(LocalDateTime.now(),user.getUsername());

        return loginResponse;
    }

    @Override
    public Map<String, Object> refresh(HttpServletRequest request) {
        final String authorizationHeader = request.getHeader("Authorization");
        String token = null;
        if (authorizationHeader.startsWith("Bearer ")) {
            token = authorizationHeader.substring(7);
        }

        Map<String, Object> responseMap = new LinkedHashMap<>();
        responseMap.put("access_token", jwtUtil.generateToken(userMapper.findByUsername(jwtUtil.getUsernameFromToken(token))));
        responseMap.put("expires_in", 3600);
        userMapper.updateLoginTime(LocalDateTime.now(),CurrentUserUtil.getCurrentUser().getUsername());
        return responseMap;
    }

    @Override
    public UserProfileResponse getUserProfile(String token) {
        UserProfileResponse userProfileResponse;

        userProfileResponse = new UserProfileResponse(userMapper.findByUsername(jwtUtil.getUsernameFromToken(token)));

        return userProfileResponse;
    }

    @Override
    public Map<String, Object> updateUserProfile(String token, User user) {
        String username = jwtUtil.getUsernameFromToken(token);
        userMapper.updateUserProfile(username, user);

        user = userMapper.findByUsername(username);

        Map<String, Object> responseMap = new LinkedHashMap<>();
        responseMap.put("nickname", user.getNickname());
        responseMap.put("bio", user.getBio());
        responseMap.put("gender", user.getGender());
        responseMap.put("birthday", user.getBirthday());
        responseMap.put("location", user.getLocation());
        return responseMap;
    }

    @Override
    public void updateUserAvatar(String url, String token) {
        userMapper.updateUserAvatar(url, jwtUtil.getUsernameFromToken(token));
    }

    @Override
    public UserProfileResponse findUserById(Long id) {
        if(!CurrentUserUtil.isLoggedIn()) return null;
        UserProfileResponse userProfileResponse = new UserProfileResponse(userMapper.selectById(id));

        userProfileResponse.setFollowing(followMapper.selectFollow(CurrentUserUtil.getCurrentUserId(),id));

        return userProfileResponse;
    }

    @Override
    public PageInfo<UserManageResponse> findUsersAsPage(Integer pageNum, Integer pageSize, String keyword, Integer status) {
        PageHelper.startPage(pageNum, pageSize);
        return PageInfo.of(userMapper.selectUsersAsPage(keyword, status));
    }

    @Override
    public UserManageResponse updateUser(RegisterRequest user) {
        try {
            userMapper.updateUser(user);
            return new UserManageResponse(userMapper.selectById(user.getId()));
        } catch (RuntimeException e) {
            throw new CustomException("1004", "账号不存在");
        }
    }

    @Override
    public UserManageResponse addUser(RegisterRequest user) {
        try {
            userMapper.insertUser(user);
        } catch (DuplicateKeyException e) {
            throw new CustomException("1004", "账号已存在");
        }

        return new UserManageResponse(userMapper.findByUsername(user.getUsername()));
    }

    @Override
    public UserManageResponse updateUserStatus(Long id, byte status) {

        try {
            userMapper.updateUserStatus(id, status);
        } catch (DuplicateKeyException e) {
            throw new CustomException("1004", "账号已存在");
        }

        return null;
    }

    @Override
    public void deleteUser(Long id) {

//        try {
//            artistMapper.deleteUser(id);
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }

        userMapper.deleteUser(id);

    }

    @Override
    public PageInfo<CollectorResponse> searchUsersByKeyword(Integer pageNum, Integer pageSize, String keyword) {

        PageHelper.startPage(pageNum, pageSize);


        return PageInfo.of(userMapper.selectUsersByKeyword(keyword));
    }
}
