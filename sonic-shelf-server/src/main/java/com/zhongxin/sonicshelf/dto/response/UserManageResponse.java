package com.zhongxin.sonicshelf.dto.response;

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
}
