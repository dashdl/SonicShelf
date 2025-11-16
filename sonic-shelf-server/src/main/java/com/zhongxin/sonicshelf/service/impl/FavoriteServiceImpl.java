package com.zhongxin.sonicshelf.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zhongxin.sonicshelf.dto.response.*;
import com.zhongxin.sonicshelf.entity.Favorite;
import com.zhongxin.sonicshelf.exception.CustomException;
import com.zhongxin.sonicshelf.mapper.*;
import com.zhongxin.sonicshelf.service.FavoriteService;
import com.zhongxin.sonicshelf.util.CurrentUserUtil;
import com.zhongxin.sonicshelf.util.FavoriteType;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FavoriteServiceImpl implements FavoriteService {
    @Resource
    FavoriteMapper favoriteMapper;
    @Autowired
    private PlaylistMapper playlistMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private MusicMapper musicMapper;
    @Resource
    AlbumMapper albumMapper;


    @Override
    public FavoriteResponse addFavorite(String targetType, Long id) {
        Favorite favorite = new Favorite(targetType, id);

        if (isFavorite(favorite)) {
            throw new CustomException("400", "您已收藏该" + FavoriteType.toChinese(targetType));
        }
        favoriteMapper.addFavorite(favorite);
        return favoriteMapper.selectFavoriteById(favorite.getId().intValue());
    }

    @Override
    public void removeFavorite(String targetType, Long id) {
        Favorite favorite = new Favorite(targetType, id);

        if (!isFavorite(favorite)) throw new CustomException("400", "您未收藏该" + FavoriteType.toChinese(targetType));

        favoriteMapper.removeFavorite(favorite);
    }

    @Override
    public List<PlaylistsResponse> findPlaylists(Long id) {
        return playlistMapper.selectFavoriteByUserId(id);
    }

    @Override
    public List<CollectorResponse> findCollectorByTargetTypeAndTargetId(String targetType, Long targetId) {
        return userMapper.selectByFavorite(targetType, targetId);
    }

    @Override
    public PageInfo<PlaylistsResponse> findAsPage(Integer pageNum, Integer pageSize, Long id) {
        PageHelper.startPage(pageNum, pageSize);
        return PageInfo.of(playlistMapper.selectFavoriteByUserId(id));
    }

    private boolean isFavorite(Favorite favorite) {
        return favoriteMapper.findByUserAndTarget(favorite) != null;
    }

    @Override
    public List<Long> findByUserIdAndTargetType(Long currentUserId, String targetType) {
        return favoriteMapper.selectByUserIdAndTargetType(currentUserId, targetType);
    }

    @Override
    public PageInfo<MusicResponse> findMusicsAsPage(Integer pageNum, Integer pageSize) {

        List<Long> ids = favoriteMapper.selectIdByUserIdAndTargetType(CurrentUserUtil.getCurrentUserId(), "music");
        PageHelper.startPage(pageNum, pageSize);

        List<MusicResponse> musicResponses = musicMapper.selectMusicsByIds(ids);
        for (MusicResponse music : musicResponses) music.setFavorite(true);

        return PageInfo.of(musicResponses);
    }

    @Override
    public List<MusicResponse> findMusics() {

        List<Long> ids = favoriteMapper.selectIdByUserIdAndTargetType(CurrentUserUtil.getCurrentUserId(), "music");
        List<MusicResponse> musicResponses = musicMapper.selectMusicsByIds(ids);
        for (MusicResponse music : musicResponses) music.setFavorite(true);

        return musicResponses;
    }

    @Override
    public List<AlbumInfoResponse> findAlbums() {
        List<Long> ids = favoriteMapper.selectIdByUserIdAndTargetType(CurrentUserUtil.getCurrentUserId(), "album");
        List<AlbumInfoResponse> albumResponses = albumMapper.selectAlbumByIds(ids);
        for (AlbumInfoResponse music : albumResponses) music.setFavorite(true);
        return albumResponses;
    }

    @Override
    public PageInfo<AlbumInfoResponse> findAlbumsAsPage(Integer pageNum, Integer pageSize) {
        List<Long> ids = favoriteMapper.selectIdByUserIdAndTargetType(CurrentUserUtil.getCurrentUserId(), "album");
        PageHelper.startPage(pageNum, pageSize);
        List<AlbumInfoResponse> albumResponses = albumMapper.selectAlbumByIds(ids);
        for (AlbumInfoResponse music : albumResponses) music.setFavorite(true);
        return PageInfo.of(albumResponses);
    }
}
