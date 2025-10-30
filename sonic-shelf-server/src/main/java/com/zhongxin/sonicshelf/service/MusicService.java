package com.zhongxin.sonicshelf.service;

import com.github.pagehelper.PageInfo;
import com.zhongxin.sonicshelf.dto.response.MusicResponse;

public interface MusicService {
    PageInfo<MusicResponse> findAsPageByListId(Integer pageNum, Integer pageSize, Long id);
}
