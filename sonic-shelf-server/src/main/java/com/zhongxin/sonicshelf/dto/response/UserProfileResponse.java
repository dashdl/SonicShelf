package com.zhongxin.sonicshelf.dto.response;

import com.zhongxin.sonicshelf.entity.User;
import lombok.Data;

@Data
public class UserProfileResponse {
    private Long id;
    private String nickname;
    private String avatar;
    private String bio;
    private byte gender;
    private String birthday;
    private String location;
    private int followers_count;
    private int following_count;


    public UserProfileResponse() {
    }

    public UserProfileResponse(User user) {
        this.id = user.getId();
        this.nickname = user.getNickname();
        this.avatar = user.getAvatar();
        this.bio = user.getBio();
        this.gender = user.getGender();
        this.birthday = user.getBirthday();
        this.location = user.getLocation();
        this.followers_count=user.getFollowerCount();
        this.following_count=user.getFollowingCount();
    }

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
}
