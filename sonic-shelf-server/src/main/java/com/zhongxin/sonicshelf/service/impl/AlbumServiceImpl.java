package com.zhongxin.sonicshelf.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zhongxin.sonicshelf.dto.response.AlbumInfoResponse;
import com.zhongxin.sonicshelf.dto.response.AlbumManageResponse;
import com.zhongxin.sonicshelf.mapper.AlbumMapper;
import com.zhongxin.sonicshelf.service.AlbumService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class AlbumServiceImpl implements AlbumService {
    @Resource
    private AlbumMapper albumMapper;

    @Override
    public AlbumInfoResponse findAlbumInfoById(Long id) {
        return albumMapper.selectAlbumsById(id);
    }

    @Override
    public PageInfo<AlbumManageResponse> findAlbumsAsPage(Integer pageNum, Integer pageSize, String keyword, Long singerId) {
        PageHelper.startPage(pageNum, pageSize);



        return null;
    }

}
