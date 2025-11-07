package com.zhongxin.sonicshelf.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zhongxin.sonicshelf.dto.response.CollectorResponse;
import com.zhongxin.sonicshelf.dto.response.FavoriteResponse;
import com.zhongxin.sonicshelf.dto.response.PlaylistsResponse;
import com.zhongxin.sonicshelf.entity.Favorite;
import com.zhongxin.sonicshelf.exception.CustomException;
import com.zhongxin.sonicshelf.mapper.FavoriteMapper;
import com.zhongxin.sonicshelf.mapper.PlaylistMapper;
import com.zhongxin.sonicshelf.mapper.UserMapper;
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

    @Override
    public FavoriteResponse addFavorite(String targetType, Long targetId) {
        Favorite favorite = new Favorite(targetType, targetId);

        if (isFavorite(favorite)) {
            throw new CustomException("400", "您已收藏该" + FavoriteType.toChinese(targetType));
        }
        favoriteMapper.addFavorite(favorite);
        return favoriteMapper.selectFavoriteById(favorite.getId().intValue());
    }

    @Override
    public void removeFavorite(String targetType, Long playlistId) {
        Favorite favorite = new Favorite(targetType, playlistId);

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

}
