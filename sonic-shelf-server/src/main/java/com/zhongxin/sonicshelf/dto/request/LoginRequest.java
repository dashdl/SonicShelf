package com.zhongxin.sonicshelf.dto.request;

import com.zhongxin.sonicshelf.entity.Admin;
import com.zhongxin.sonicshelf.entity.User;
import lombok.Data;

@Data
public class LoginRequest {

    private String username;
    private String password;

    public User toUser(){
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        return user;
    }

    public Admin toAdmin(){
        Admin admin = new Admin();
        admin.setUsername(username);
        admin.setPassword(password);
        return admin;
    }
}
