package com.zhongxin.sonicshelf.entity;

import com.zhongxin.sonicshelf.dto.request.PlaylistRequest;
import lombok.Data;

@Data
public class Playlist {
    private Long id;
    private String title;
    private String description;
    private String coverImage;
    private String userId;
    private String isPublic;
    private String musicCount;
    private String playCount;
    private String favoriteCount;
    private String shareCount;
    private String status;
    private String createTime;
    private String updateTime;

    public Playlist(){}

    public Playlist(PlaylistRequest playlistRequest){
        this.title = playlistRequest.getTitle();
        this.description = playlistRequest.getDescription();
        this.isPublic = playlistRequest.getIsPublic();
    }
}
