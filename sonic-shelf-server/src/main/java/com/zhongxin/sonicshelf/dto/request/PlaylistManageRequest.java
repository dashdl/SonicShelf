package com.zhongxin.sonicshelf.dto.request;

import com.zhongxin.sonicshelf.entity.Playlist;
import lombok.Data;

@Data
public class PlaylistManageRequest {
    private Long id;
    private String title;
    private Integer[] categoryIds;
    private String description;
    private Byte isPublic;

    public Playlist toPlaylist() {
        Playlist playlist = new Playlist();
        playlist.setId(id);
        playlist.setTitle(title);
        playlist.setDescription(description);
        playlist.setIsPublic(isPublic.toString());
        return playlist;
    }
}
