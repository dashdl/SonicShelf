package com.zhongxin.sonicshelf.service;

import com.github.pagehelper.PageInfo;
import com.zhongxin.sonicshelf.dto.response.FavoriteResponse;
import com.zhongxin.sonicshelf.dto.response.PlaylistsResponse;

public interface PlaylistService {
    PageInfo<PlaylistsResponse> findAsPage(Integer pageNum, Integer pageSize, Long id);

    PlaylistsResponse findByPlaylistId(Long id);


}
