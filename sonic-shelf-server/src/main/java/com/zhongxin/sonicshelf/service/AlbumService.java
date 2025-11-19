package com.zhongxin.sonicshelf.service;

import com.github.pagehelper.PageInfo;
import com.zhongxin.sonicshelf.dto.request.AlbumManageRequest;
import com.zhongxin.sonicshelf.dto.response.AlbumInfoResponse;
import com.zhongxin.sonicshelf.dto.response.AlbumManageResponse;

public interface AlbumService {
    AlbumInfoResponse findAlbumInfoById(Long id);

    PageInfo<AlbumManageResponse> findAlbumsAsPage(Integer pageNum, Integer pageSize, String keyword, Long singerId);

    AlbumManageResponse updateAlbum(AlbumManageRequest album);

    AlbumManageResponse addAlbum(AlbumManageRequest album);
}
