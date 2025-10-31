package com.zhongxin.sonicshelf.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zhongxin.sonicshelf.dto.response.PlaylistsResponse;
import com.zhongxin.sonicshelf.mapper.PlaylistMapper;
import com.zhongxin.sonicshelf.service.PlaylistService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class PlaylistServiceImpl implements PlaylistService {

    @Resource
    private PlaylistMapper playlistMapper;

    @Override
    public PageInfo<PlaylistsResponse> findAsPage(Integer pageNum, Integer pageSize, Long id) {

        PageHelper.startPage(pageNum, pageSize);

        return PageInfo.of(playlistMapper.selectByUserId(id));
    }

    @Override
    public PlaylistsResponse findByPlaylistId(Long id) {

        return playlistMapper.findByPlaylistId(id);
    }
}
