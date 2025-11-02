package com.zhongxin.sonicshelf.service;

import com.github.pagehelper.PageInfo;
import com.zhongxin.sonicshelf.dto.response.FavoriteResponse;
import com.zhongxin.sonicshelf.dto.response.PlaylistsResponse;
import com.zhongxin.sonicshelf.util.Result;

import java.util.List;

public interface PlaylistService {
    PageInfo<PlaylistsResponse> findAsPage(Integer pageNum, Integer pageSize, Long id);

    PlaylistsResponse findByPlaylistId(Long id);

    List<PlaylistsResponse> findAll(Long id);
}
