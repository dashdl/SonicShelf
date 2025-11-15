package com.zhongxin.sonicshelf.service.impl;

import com.github.pagehelper.PageInfo;
import com.zhongxin.sonicshelf.dto.response.MusicInfoResponse;
import com.zhongxin.sonicshelf.dto.response.MusicResponse;
import com.zhongxin.sonicshelf.entity.Favorite;
import com.zhongxin.sonicshelf.exception.CustomException;
import com.zhongxin.sonicshelf.mapper.CategoriesMapper;
import com.zhongxin.sonicshelf.mapper.FavoriteMapper;
import com.zhongxin.sonicshelf.mapper.MusicMapper;
import com.zhongxin.sonicshelf.service.MusicService;
import com.zhongxin.sonicshelf.util.CurrentUserUtil;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.List;

@Service
public class MusicServiceImpl implements MusicService {
    @Resource
    private MusicMapper musicMapper;
    @Autowired
    private CategoriesMapper categoriesMapper;
    @Resource
    private FavoriteMapper favoriteMapper;

    @Override
    public PageInfo<MusicResponse> findAsPageByListId(Integer pageNum, Integer pageSize, Long id) {

        List<MusicResponse> musicResponses = musicMapper.findAsPageByListId(id, CurrentUserUtil.getCurrentUserId());

        for (MusicResponse music : musicResponses) {
            music.setFavorite(isFavorite(music.getId()));
        }

        return PageInfo.of(musicResponses);
    }

    @Override
    public List<MusicResponse> findByListId(Long id) {

        List<MusicResponse> musicResponses = musicMapper.findAsPageByListId(id, CurrentUserUtil.getCurrentUserId());

        for (MusicResponse music : musicResponses) {
            music.setFavorite(isFavorite(music.getId()));
        }

        return musicResponses;
    }

    @Override
    public MusicResponse findById(Long id) {
        try {
            return musicMapper.selectById(id).setFavorite(isFavorite(id));
        } catch (RuntimeException e) {
            throw new CustomException("404", "音乐不存在或已下架");
        }
    }

    @Override
    public List<Long> findByCategoryId(List<Long> ids) {
        return categoriesMapper.selectMusicIdByCategoryId(ids);
    }

    @Override
    public List<MusicInfoResponse> findByIds(List<Long> musics) {

        List<MusicInfoResponse> musicInfoResponseList = musicMapper.selectByIds(musics);

        for (MusicInfoResponse music : musicInfoResponseList) music.setFavorite(isFavorite(music.getId()));

        return musicInfoResponseList;
    }

    @Override
    public String findLyricsById(Long id) {
        return musicMapper.selectLyricById(id);
    }

    @Override
    public List<MusicResponse> findMusicsByArtistId(Long id) {

        List<MusicResponse> musicResponseList = musicMapper.selectMusicsByArtistId(id);

        for (MusicResponse music : musicResponseList) music.setFavorite(isFavorite(music.getId()));

        return musicResponseList;
    }

    @Override
    public List<MusicResponse> findMusicsByAlbumId(Long id) {
        List<MusicResponse> musicResponseList = musicMapper.selectMusicsByAlbumId(id);

        for (MusicResponse music : musicResponseList) music.setFavorite(isFavorite(music.getId()));

        return musicResponseList;
    }

    private boolean isFavorite(Long musicId) {
        Favorite favorite = new Favorite("music", musicId);
        return favoriteMapper.findByUserAndTarget(favorite) != null;
    }
}
