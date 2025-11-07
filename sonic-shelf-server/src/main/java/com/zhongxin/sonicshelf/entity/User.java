package com.zhongxin.sonicshelf.entity;

import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;

@Data
public class User implements UserDetails {

    private Long id;

    // ========== 认证信息字段 ==========
    private String username;
    private String email;
    private String password;

    // ========== 个人信息字段 ==========
    private String nickname;
    private String avatar;
    private String bio;
    private byte gender;
    private String birthday;
    private String location;
    private String phone;

    // ========== 状态字段 ==========
    private Boolean emailVerified = false;
    private Integer status = 1;

    // ========== 统计字段 ==========
    private Integer followerCount = 0;
    private Integer followingCount = 0;

    // ========== 时间字段 ==========
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime lastLoginAt;

    public String getGenderText() {
        if (gender == 0) {
            return "未知";
        }

        return switch (gender) {
            case 1 -> "男";
            case 2 -> "女";
            default -> "未知";
        };
    }

    public void setGenderText(String genderText) {
        switch (genderText) {
            case "男" -> this.gender = 1;
            case "女" -> this.gender = 2;
            default -> this.gender = 0;
        }
    }

    // ========== Spring Security 方法 ==========
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
    }
}