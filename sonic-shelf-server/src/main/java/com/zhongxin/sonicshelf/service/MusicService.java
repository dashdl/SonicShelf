package com.zhongxin.sonicshelf.service;

import com.github.pagehelper.PageInfo;
import com.zhongxin.sonicshelf.dto.response.*;

import java.util.List;

public interface MusicService {
    PageInfo<MusicResponse> findAsPageByListId(Integer pageNum, Integer pageSize, Long id);

    List<MusicResponse> findByListId(Long id);

    MusicResponse findById(Long id);

    List<Long> findByCategoryId(List<Long> ids);

    List<MusicInfoResponse> findByIds(List<Long> musics);

    String findLyricsById(Long id);

    List<MusicResponse> findMusicsByArtistId(Long id);

    List<MusicResponse> findMusicsByAlbumId(Long id);

    PageInfo<MusicManageResponse> findAlbumsAsPage(Integer pageNum, Integer pageSize, String keyword, Long artistId, Long albumId);
}
