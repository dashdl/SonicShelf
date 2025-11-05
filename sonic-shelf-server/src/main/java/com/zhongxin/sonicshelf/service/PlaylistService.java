package com.zhongxin.sonicshelf.service;

import com.github.pagehelper.PageInfo;
import com.zhongxin.sonicshelf.dto.response.PlaylistsResponse;
import com.zhongxin.sonicshelf.entity.Playlist;

import java.util.List;

public interface PlaylistService {
    PageInfo<PlaylistsResponse> findAsPage(Integer pageNum, Integer pageSize, Long id);

    PlaylistsResponse findByPlaylistId(Long id);

    List<PlaylistsResponse> findAll(Long id);

    PlaylistsResponse updatePlaylist(Playlist playlist);

    Long[] updatePlaylistTags(Long id, Long[] tags);

    void updatePlaylistCover(String url,Long id);
}
