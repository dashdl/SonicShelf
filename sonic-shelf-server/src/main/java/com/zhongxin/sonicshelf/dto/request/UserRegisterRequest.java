package com.zhongxin.sonicshelf.dto.request;

import com.zhongxin.sonicshelf.entity.User;
import lombok.Data;

@Data
public class UserRegisterRequest {
    private String username;
    private String password;
    private String email;
    private String nickname;

    public User dtoToUser(){
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setEmail(email);
        user.setNickname(nickname);
        return user;
    }

    public UserRegisterRequest(){}

    public UserRegisterRequest(User user){
        this.username = user.getUsername();
        this.password = user.getPassword();
        this.email = user.getEmail();
        this.nickname = user.getNickname();
    }
}
