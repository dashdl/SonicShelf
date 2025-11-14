package com.zhongxin.sonicshelf.service;

import com.zhongxin.sonicshelf.dto.response.ArtistResponse;

public interface ArtistService {
    ArtistResponse findArtistById(Long id);
}
