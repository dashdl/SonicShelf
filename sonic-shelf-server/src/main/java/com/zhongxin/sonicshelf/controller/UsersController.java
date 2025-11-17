package com.zhongxin.sonicshelf.controller;


import com.github.pagehelper.PageInfo;
import com.zhongxin.sonicshelf.dto.response.PlaylistsResponse;
import com.zhongxin.sonicshelf.dto.response.UserManageResponse;
import com.zhongxin.sonicshelf.dto.response.UserProfileResponse;
import com.zhongxin.sonicshelf.entity.User;
import com.zhongxin.sonicshelf.exception.CustomException;
import com.zhongxin.sonicshelf.mapper.PlaylistMapper;
import com.zhongxin.sonicshelf.service.UserService;
import com.zhongxin.sonicshelf.util.CurrentUserUtil;
import com.zhongxin.sonicshelf.util.JwtUtil;
import com.zhongxin.sonicshelf.util.Result;
import com.zhongxin.sonicshelf.util.TokenExtractor;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
public class UsersController {

    @Resource
    UserService userService;
    @Autowired
    JwtUtil jwtUtil;

    @GetMapping("/profile")
    public Result profile(HttpServletRequest request) {

        final String authorizationHeader = request.getHeader("Authorization");
        String token = null;
        if (authorizationHeader.startsWith("Bearer ")) {
            token = authorizationHeader.substring(7);
        }

        return Result.success(userService.getUserProfile(token));
    }

    @PutMapping("/profile")
    public Result updateUserProfile(@RequestBody User user, HttpServletRequest request) {
        final String authorizationHeader = request.getHeader("Authorization");
        String token = null;
        if (authorizationHeader.startsWith("Bearer ")) {
            token = authorizationHeader.substring(7);
        }
        return Result.success(userService.updateUserProfile(token, user));
    }

    @GetMapping("/profile/{id}")
    public Result getUserProfile(@PathVariable Long id) {
        UserProfileResponse userProfileResponse;
        try {
            userProfileResponse = userService.findUserById(id);
        } catch (Exception e) {
            throw new CustomException("获取用户数据失败");
        }
        return Result.success(userProfileResponse);
    }

    @GetMapping("/getAll")
    public Result getAll(@RequestParam(required = true) Integer pageNum,
                         @RequestParam(required = true) Integer pageSize,
                         @RequestParam(required = false) String keyword,
                         @RequestParam(required = false) Integer status,
                         HttpServletRequest request) {
        String token = TokenExtractor.extractToken(request);
        if (!jwtUtil.isAdmin(token)) {
            return Result.error("需要管理员权限");
        }
        PageInfo<UserManageResponse> pageInfo = userService.findUsersAsPage(pageNum,pageSize,keyword,status);
        return Result.success(pageInfo);

    }
}
