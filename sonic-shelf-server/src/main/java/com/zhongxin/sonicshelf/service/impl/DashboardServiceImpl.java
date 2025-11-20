package com.zhongxin.sonicshelf.service.impl;

import com.zhongxin.sonicshelf.mapper.*;
import com.zhongxin.sonicshelf.service.DashboardService;
import com.zhongxin.sonicshelf.service.MusicService;
import com.zhongxin.sonicshelf.service.UserService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class DashboardServiceImpl implements DashboardService {

    @Resource
    private UserMapper userMapper;
    @Resource
    private MusicMapper musicMapper;
    @Resource
    private AlbumMapper albumMapper;
    @Resource
    private ArtistMapper artistMapper;
    @Resource
    private PlaylistMapper playlistMapper;
    @Resource
    private CategoriesMapper categoriesMapper;

    @Override
    public Map<String, Object> getCount() {

        Map<String, Object> responseMap = new LinkedHashMap<>();
        responseMap.put("userCount", userMapper.countUserCount());
        responseMap.put("musicCount", musicMapper.countMusicCount());
        responseMap.put("albumCount", albumMapper.countAlbumCount());
        responseMap.put("artistCount", artistMapper.countArtistCount());
        responseMap.put("playlistCount", playlistMapper.countPlaylistCount());
        responseMap.put("categoryCount", categoriesMapper.countCategoryCount());

        return responseMap;
    }
}
