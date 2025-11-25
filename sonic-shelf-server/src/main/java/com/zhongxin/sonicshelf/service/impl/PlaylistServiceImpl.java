package com.zhongxin.sonicshelf.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zhongxin.sonicshelf.dto.request.PlaylistManageRequest;
import com.zhongxin.sonicshelf.dto.response.*;
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
    public Long addPlaylist(PlaylistManageRequest playlist) {

        playlistMapper.addPlaylist(playlist);

        categoriesMapper.addOfficialPlaylistTags(playlist.getCategoryIds(), playlist.getId());

        return playlist.getId();
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

    @Override
    public boolean isPublic(Long playlistId) {
        Integer isPublic = playlistMapper.selectPlaylistIsPublic(playlistId);
        return 1 == isPublic || 2 == isPublic;
    }

    @Override
    public boolean isOfficial(Long playlistId) {
        return 2 == playlistMapper.selectPlaylistIsPublic(playlistId);
    }

    @Override
    public void addMusic(Long playlistId, Long[] musicIds) {
        playlistMapper.addMusic(playlistId, musicIds);
    }

    @Override
    public void removeMusic(Long playlistId, Long musicId) {
        playlistMapper.removeMusic(playlistId, musicId);
    }

    @Override
    public void updateMusicCount(Long playlistId) {
        playlistMapper.updateMusicCount(playlistId, playlistMapper.countMusicCount(playlistId));
    }

    @Override
    public void addPlayCount(Long id) {
        playlistMapper.addPlayCount(id);
    }

    @Override
    public PageInfo<PlaylistBaseResponse> findPlaylistsAsPageForUser(Integer pageNum, Integer pageSize, String keyword) {

        PageHelper.startPage(pageNum, pageSize);
        return PageInfo.of(playlistMapper.selectPlaylistsForUser(keyword));
    }

    @Override
    public PageInfo<PlaylistBaseResponse> searchPlaylistsByKeyword(Integer pageNum, Integer pageSize, String keyword) {

        PageHelper.startPage(pageNum, pageSize);
        return PageInfo.of(playlistMapper.selectPlaylistsForSearch(keyword));
    }

    @Override
    public void createPlaylist(boolean isPublic, String title) {

        if (isPublic) {
            playlistMapper.insertPlaylist(1,title,CurrentUserUtil.getCurrentUserId());
        }else {
            playlistMapper.insertPlaylist(0,title,CurrentUserUtil.getCurrentUserId());
        }


    }
}
