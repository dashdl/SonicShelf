package com.zhongxin.sonicshelf.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zhongxin.sonicshelf.dto.request.PlaylistManageRequest;
import com.zhongxin.sonicshelf.dto.response.PlaylistCardResponse;
import com.zhongxin.sonicshelf.dto.response.PlaylistManageResponse;
import com.zhongxin.sonicshelf.dto.response.PlaylistsResponse;
import com.zhongxin.sonicshelf.entity.Playlist;
import com.zhongxin.sonicshelf.exception.CustomException;
import com.zhongxin.sonicshelf.mapper.CategoriesMapper;
import com.zhongxin.sonicshelf.mapper.PlaylistMapper;
import com.zhongxin.sonicshelf.service.PlaylistService;
import com.zhongxin.sonicshelf.util.CurrentUserUtil;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

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
    public List<PlaylistsResponse> findAllUserId(Long id) {
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
    public void updatePlaylistCover(String url, Long id) {
        if (Objects.equals(playlistMapper.findCreatorByPlaylistId(id), CurrentUserUtil.getCurrentUserId())) {
            playlistMapper.updatePlaylistCover(url, id);
        } else {
            throw new CustomException("这不是您创建的歌单");
        }
    }

    @Override
    public List<PlaylistCardResponse> findAll(Integer limit) {

        List<PlaylistCardResponse> playlistCardResponses = playlistMapper.selectAll(limit);

        for (PlaylistCardResponse playlistCardResponse : playlistCardResponses) {
            playlistCardResponse.setSongs(playlistMapper.selectByPlaylistId(playlistCardResponse.getId()));
        }

        return playlistCardResponses;
    }

    @Override
    public void collectMusicByPlaylistIdAndMusicId(Long playlistId, Long musicId) {
        if (playlistMapper.findByPlaylistIdAndMusicId(playlistId, musicId) == null) {
            playlistMapper.insertPlaylistMusic(playlistId, musicId);
            playlistMapper.updateMusicCount(playlistId, playlistMapper.countMusicCount(playlistId));
        } else {
            throw new CustomException("歌单中已包含该音乐");
        }
    }

    @Override
    public void deleteMusicByPlaylistIdAndMusicId(Long playlistId, Long musicId) {
        if (playlistMapper.findByPlaylistIdAndMusicId(playlistId, musicId) != null) {
            playlistMapper.deletePlaylistMusic(playlistId, musicId);
            playlistMapper.updateMusicCount(playlistId, playlistMapper.countMusicCount(playlistId));
        } else {
            throw new CustomException("歌单中未包含该音乐");
        }
    }

    @Override
    public Long findUserIdByPlaylistId(Long playlistId) {
        return playlistMapper.selectUserIdByPlaylistId(playlistId);
    }

    @Override
    public PageInfo<PlaylistManageResponse> findPlaylistAsPage(Integer pageNum, Integer pageSize, String keyword, Integer[] categoryIds, Byte isPublic) {

        PageHelper.startPage(pageNum, pageSize);

        List<PlaylistManageResponse> playlists = playlistMapper.selectPlaylist(keyword, isPublic, categoryIds);

        if (!playlists.isEmpty()) {
            for (PlaylistManageResponse playlist : playlists) {
                playlist.setCategories(categoriesMapper.selectByPlaylistId(playlist.getId()));
            }
        }

        return PageInfo.of(playlists);
    }

    @Override
    public void addPlaylist(PlaylistManageRequest playlist) {

        playlistMapper.addPlaylist(playlist);

        categoriesMapper.addOfficialPlaylistTags(playlist.getCategoryIds(), playlist.getId());

    }

    @Override
    public void updateOfficialPlaylistCover(String url, Long id) {
        playlistMapper.updatePlaylistCover(url, id);
    }

    @Override
    public void updateOfficialPlaylist(PlaylistManageRequest playlist) {
        playlistMapper.updatePlaylist(playlist.toPlaylist());
    }

    @Override
    public void deleteOfficialPlaylist(Long playlistId) {
        playlistMapper.deleteOfficialPlaylist(playlistId);
    }
}
