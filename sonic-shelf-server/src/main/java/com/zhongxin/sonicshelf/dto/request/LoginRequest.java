package com.zhongxin.sonicshelf.dto.request;

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
}
