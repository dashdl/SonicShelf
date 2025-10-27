package com.zhongxin.sonicshelf.dto.response;

import com.zhongxin.sonicshelf.entity.User;
import lombok.Data;

@Data
public class RegisterResponse {
    private Long user_id;
    private String username;
    private String nickname;
    private Boolean email_verified;

    public RegisterResponse(User user) {
        this.user_id=user.getId();
        this.username=user.getUsername();
        this.nickname=user.getNickname();
        this.email_verified=user.getEmailVerified();
    }

    public RegisterResponse() {
    }
}
