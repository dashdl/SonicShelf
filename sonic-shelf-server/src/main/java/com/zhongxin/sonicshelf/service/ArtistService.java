package com.zhongxin.sonicshelf.service;

import com.github.pagehelper.PageInfo;
import com.zhongxin.sonicshelf.dto.request.ArtistManageRequest;
import com.zhongxin.sonicshelf.dto.response.AlbumResponse;
import com.zhongxin.sonicshelf.dto.response.ArtistManageResponse;
import com.zhongxin.sonicshelf.dto.response.ArtistResponse;

import java.util.List;

public interface ArtistService {
    ArtistResponse findArtistById(Long id);

    List<AlbumResponse> findAlbumsByArtistId(Long id);

    PageInfo<ArtistManageResponse> findArtistsAsPage(Integer pageNum, Integer pageSize, String keyword, Byte gender);

    ArtistManageResponse updateArtist(ArtistManageRequest artist);

    void deleteArtist(Long artist);

    ArtistManageResponse addArtist(ArtistManageRequest artist);
}
