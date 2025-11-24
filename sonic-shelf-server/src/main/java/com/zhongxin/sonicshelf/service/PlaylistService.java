package com.zhongxin.sonicshelf.service;

import com.github.pagehelper.PageInfo;
import com.zhongxin.sonicshelf.dto.request.PlaylistManageRequest;
import com.zhongxin.sonicshelf.dto.response.*;
import com.zhongxin.sonicshelf.entity.Playlist;

import java.util.List;

public interface PlaylistService {
    PageInfo<PlaylistsResponse> findAsPage(Integer pageNum, Integer pageSize, Long id);

    PlaylistsResponse findByPlaylistId(Long id);

    List<PlaylistsResponse> findAllUserId(Long id);

    PlaylistsResponse updatePlaylist(Playlist playlist);

    Long[] updatePlaylistTags(Long id, Long[] tags);

    void updatePlaylistCover(String url,Long id);

    List<PlaylistCardResponse> findAll(Integer limit);

    void collectMusicByPlaylistIdAndMusicId(Long playlistId, Long musicId);

    void deleteMusicByPlaylistIdAndMusicId(Long playlistId, Long musicId);

    Long findUserIdByPlaylistId(Long playlistId);

    PageInfo<PlaylistManageResponse> findPlaylistAsPage(Integer pageNum, Integer pageSize, String keyword, Integer[] categoryId, Byte status);

    Long addPlaylist(PlaylistManageRequest playlist);

    void updateOfficialPlaylistCover(String s, Long id);

    void updateOfficialPlaylist(PlaylistManageRequest playlist);

    void deleteOfficialPlaylist(Long playlistId);

    boolean isPublic(Long playlistId);

    boolean isOfficial(Long playlistId);

    void addMusic(Long playlistId, Long[] musicIds);

    void removeMusic(Long playlistId, Long musicId);

    void updateMusicCount(Long playlistId);

    void addPlayCount(Long id);

    PageInfo<PlaylistBaseResponse> findPlaylistsAsPageForUser(Integer pageNum, Integer pageSize, String keyword);

    PageInfo<PlaylistBaseResponse> searchPlaylistsByKeyword(Integer pageNum, Integer pageSize, String keyword);
}
