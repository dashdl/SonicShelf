package com.zhongxin.sonicshelf.dto.response;

import com.zhongxin.sonicshelf.entity.User;
import lombok.Data;

@Data
public class UserManageResponse {
    private Long id;
    private String username;
    private String email;
    private String nickname;
    private byte status;
    private String createdAt;
    private String lastLoginAt;

    public UserManageResponse() {}

    public UserManageResponse(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.nickname = user.getNickname();
        this.status = user.getStatus();
        this.createdAt = user.getCreatedAt().toString();
    }
}
