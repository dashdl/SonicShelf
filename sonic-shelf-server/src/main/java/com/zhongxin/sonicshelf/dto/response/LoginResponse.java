package com.zhongxin.sonicshelf.dto.response;

import com.zhongxin.sonicshelf.entity.User;
import lombok.Data;

@Data
public class LoginResponse {
    private String access_token;
    private String token_type="Bearer";
    private int expires_in=3600;
    private UserResponse userResponse;

    public LoginResponse() {}

    public LoginResponse(String access_token, String token_type, int expires_in, UserResponse userResponse) {
        this.access_token = access_token;
        this.token_type = token_type;
        this.expires_in = expires_in;
        this.userResponse = userResponse;
    }

    public LoginResponse(User user){
        this.userResponse = new UserResponse(user);
    }
}
