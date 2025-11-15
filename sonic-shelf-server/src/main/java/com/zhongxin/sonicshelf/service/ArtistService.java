package com.zhongxin.sonicshelf.service;

import com.zhongxin.sonicshelf.dto.response.AlbumResponse;
import com.zhongxin.sonicshelf.dto.response.ArtistResponse;

import java.util.List;

public interface ArtistService {
    ArtistResponse findArtistById(Long id);

    List<AlbumResponse> findAlbumsByArtistId(Long id);
}
