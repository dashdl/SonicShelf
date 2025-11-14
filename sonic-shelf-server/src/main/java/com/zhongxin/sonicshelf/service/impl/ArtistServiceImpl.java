package com.zhongxin.sonicshelf.service.impl;

import com.zhongxin.sonicshelf.dto.response.ArtistResponse;
import com.zhongxin.sonicshelf.mapper.ArtistMapper;
import com.zhongxin.sonicshelf.service.ArtistService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class ArtistServiceImpl implements ArtistService {

    @Resource
    private ArtistMapper artistMapper;

    @Override
    public ArtistResponse findArtistById(Long id) {

        return artistMapper.selectArtistById(id);
    }
}
