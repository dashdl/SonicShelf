package com.zhongxin.sonicshelf.dto.response;

import lombok.Data;

@Data
public class CommentCountResponse {
    private Integer music;
    private Integer playlist;
    private Integer artist;
    private Integer album;
    private Integer dynamic;

    public CommentCountResponse(Integer music, Integer playlist, Integer artist, Integer album, Integer dynamic) {
        this.music = music;
        this.playlist = playlist;
        this.artist = artist;
        this.album = album;
        this.dynamic = dynamic;
    }
}
