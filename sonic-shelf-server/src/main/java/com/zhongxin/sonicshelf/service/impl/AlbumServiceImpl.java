package com.zhongxin.sonicshelf.service.impl;

import com.zhongxin.sonicshelf.dto.response.AlbumInfoResponse;
import com.zhongxin.sonicshelf.dto.response.AlbumResponse;
import com.zhongxin.sonicshelf.mapper.AlbumMapper;
import com.zhongxin.sonicshelf.mapper.MusicMapper;
import com.zhongxin.sonicshelf.service.AlbumService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AlbumServiceImpl implements AlbumService {
    @Resource
    private AlbumMapper albumMapper;

    @Override
    public AlbumInfoResponse findAlbumInfoById(Long id) {
        return albumMapper.selectAlbumsById(id);
    }

}
