package com.zhongxin.sonicshelf.service;

import com.github.pagehelper.PageInfo;
import com.zhongxin.sonicshelf.dto.response.MusicResponse;

import java.util.List;

public interface MusicService {
    PageInfo<MusicResponse> findAsPageByListId(Integer pageNum, Integer pageSize, Long id);

    List<MusicResponse> findByListId(Long id);

    MusicResponse findById(Long id);
}
