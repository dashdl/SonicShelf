package com.zhongxin.sonicshelf.service.impl;

import com.zhongxin.sonicshelf.dto.response.AlbumResponse;
import com.zhongxin.sonicshelf.dto.response.ArtistResponse;
import com.zhongxin.sonicshelf.mapper.AlbumMapper;
import com.zhongxin.sonicshelf.mapper.ArtistMapper;
import com.zhongxin.sonicshelf.service.ArtistService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ArtistServiceImpl implements ArtistService {

    @Resource
    private ArtistMapper artistMapper;
    @Resource
    private AlbumMapper albumMapper;

    @Override
    public ArtistResponse findArtistById(Long id) {
        return artistMapper.selectArtistById(id);
    }

    @Override
    public List<AlbumResponse> findAlbumsByArtistId(Long id) {
        return albumMapper.selectAlbumsByArtistId(id);
    }
}
