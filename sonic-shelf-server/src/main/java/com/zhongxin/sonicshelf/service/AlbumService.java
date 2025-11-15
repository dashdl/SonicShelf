package com.zhongxin.sonicshelf.service;

import com.zhongxin.sonicshelf.dto.response.AlbumInfoResponse;
import com.zhongxin.sonicshelf.dto.response.AlbumResponse;

import java.util.List;

public interface AlbumService {
    AlbumInfoResponse findAlbumInfoById(Long id);
}
