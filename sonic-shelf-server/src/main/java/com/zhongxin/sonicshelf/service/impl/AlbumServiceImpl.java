package com.zhongxin.sonicshelf.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zhongxin.sonicshelf.dto.request.AlbumManageRequest;
import com.zhongxin.sonicshelf.dto.response.AlbumInfoResponse;
import com.zhongxin.sonicshelf.dto.response.AlbumManageResponse;
import com.zhongxin.sonicshelf.mapper.AlbumMapper;
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

    @Override
    public PageInfo<AlbumManageResponse> findAlbumsAsPage(Integer pageNum, Integer pageSize, String keyword, Long artistId) {
        PageHelper.startPage(pageNum, pageSize);
        return PageInfo.of(albumMapper.selectAlbums(keyword,artistId));
    }

    @Override
    public AlbumManageResponse updateAlbum(AlbumManageRequest album) {

        albumMapper.updateAlbum(album);

        return albumMapper.selectAlbumManageResponseById(album.getId());
    }

    @Override
    public AlbumManageResponse addAlbum(AlbumManageRequest album) {

        albumMapper.insertAlbum(album);

        return albumMapper.selectAlbumManageResponseById(album.getId());
    }

}
