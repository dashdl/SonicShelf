package com.zhongxin.sonicshelf.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zhongxin.sonicshelf.dto.response.PlaylistsResponse;
import com.zhongxin.sonicshelf.entity.Playlist;
import com.zhongxin.sonicshelf.mapper.CategoriesMapper;
import com.zhongxin.sonicshelf.mapper.PlaylistMapper;
import com.zhongxin.sonicshelf.service.PlaylistService;
import com.zhongxin.sonicshelf.util.CurrentUserUtil;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PlaylistServiceImpl implements PlaylistService {

    @Resource
    private CategoriesMapper categoriesMapper;

    @Resource
    private PlaylistMapper playlistMapper;

    @Override
    public PageInfo<PlaylistsResponse> findAsPage(Integer pageNum, Integer pageSize, Long id) {

        PageHelper.startPage(pageNum, pageSize);

        return PageInfo.of(playlistMapper.selectByUserId(id));
    }

    @Override
    public PlaylistsResponse findByPlaylistId(Long id) {

        PlaylistsResponse playlistsResponse = playlistMapper.findByPlaylistId(id);

        playlistsResponse.setTags(categoriesMapper.findByPlaylistId(id).toArray(new Long[0]));

        return playlistsResponse;
    }

    @Override
    public List<PlaylistsResponse> findAll(Long id) {

        return playlistMapper.selectByUserId(id);
    }

    @Override
    public PlaylistsResponse updatePlaylist(Playlist playlist) {
        playlistMapper.updatePlaylist(playlist);
        return null;
    }

    @Override
    public Long[] updatePlaylistTags(Long id, Long[] tags) {
        List<Long> oldTags = categoriesMapper.findByPlaylistId(id);
        for (Long tag : tags) {
            if (oldTags.contains(tag)) {
                oldTags.remove(tag);
            } else {
                categoriesMapper.addPlaylistTags(id, tag);
            }
        }
        for (Long tag : oldTags) {
            categoriesMapper.removePlaylistTags(id, tag);
        }
        return categoriesMapper.findByPlaylistId(id).toArray(new Long[0]);
    }

    @Override
    public void updatePlaylistCover(String url,Long id) {
        playlistMapper.updateUserAvatar(url, id);
    }
}
