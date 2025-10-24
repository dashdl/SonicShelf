package com.zhongxin.sonicshelf.dto.response;

import com.zhongxin.sonicshelf.entity.User;
import lombok.Data;

@Data
public class UserResponse {
    private Long id;
    private String username;
    private String nickname;
    private String avatar;

    public UserResponse() {}
    public UserResponse(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.nickname = user.getNickname();
        this.avatar = user.getAvatar();
    }
}
