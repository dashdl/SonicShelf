package com.zhongxin.sonicshelf.entity;

import lombok.Data;

@Data
public class AdminInfo {
    private Long id;
    private String username;
    private String nickname;
    private String avatar;

    public AdminInfo() {}

    public AdminInfo(Admin admin) {
        this.id = admin.getId();
        this.username = admin.getUsername();
        this.nickname = admin.getNickname();
        this.avatar = admin.getAvatar();
    }
}
