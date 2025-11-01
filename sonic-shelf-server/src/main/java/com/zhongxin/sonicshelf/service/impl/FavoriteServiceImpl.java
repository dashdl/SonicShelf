package com.zhongxin.sonicshelf.service.impl;

import com.zhongxin.sonicshelf.dto.response.FavoriteResponse;
import com.zhongxin.sonicshelf.dto.response.PlaylistsResponse;
import com.zhongxin.sonicshelf.entity.Favorite;
import com.zhongxin.sonicshelf.exception.CustomException;
import com.zhongxin.sonicshelf.mapper.FavoriteMapper;
import com.zhongxin.sonicshelf.mapper.PlaylistMapper;
import com.zhongxin.sonicshelf.service.FavoriteService;
import com.zhongxin.sonicshelf.util.CurrentUserUtil;
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

    @Override
    public FavoriteResponse addFavorite(String targetType, Long targetId) {
        Favorite favorite = new Favorite(targetType, targetId);

        if (isFavorite(favorite))
            throw new CustomException("400", "您已收藏该歌单");
        favoriteMapper.addFavorite(favorite);
        return favoriteMapper.selectFavoriteById(favorite.getId().intValue());
    }

    @Override
    public void removeFavoritePlaylist(String targetType, Long playlistId) {
        Favorite favorite = new Favorite(targetType, playlistId);

        if (!isFavorite(favorite)) throw new CustomException("400", "您未收藏该歌单");

        favoriteMapper.removeFavorite(favorite);
    }

    @Override
    public List<PlaylistsResponse> findPlaylists() {

        return playlistMapper.selectFavoriteByUserId(CurrentUserUtil.getCurrentUserId());
    }


    private boolean isFavorite(Favorite favorite) {
        return favoriteMapper.findByUserAndTarget(favorite) != null;
    }

}
