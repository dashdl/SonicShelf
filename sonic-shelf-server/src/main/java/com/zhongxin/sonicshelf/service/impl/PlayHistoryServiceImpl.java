package com.zhongxin.sonicshelf.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zhongxin.sonicshelf.dto.response.MusicResponse;
import com.zhongxin.sonicshelf.entity.Favorite;
import com.zhongxin.sonicshelf.entity.PlayHistory;
import com.zhongxin.sonicshelf.mapper.FavoriteMapper;
import com.zhongxin.sonicshelf.mapper.MusicMapper;
import com.zhongxin.sonicshelf.mapper.PlayHistoryMapper;
import com.zhongxin.sonicshelf.service.PlayHistoryService;
import com.zhongxin.sonicshelf.util.CurrentUserUtil;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PlayHistoryServiceImpl implements PlayHistoryService {

    @Resource
    private PlayHistoryMapper playHistoryMapper;
    @Resource
    private  MusicMapper musicMapper;

    @Resource
    private FavoriteMapper favoriteMapper;

    @Override
    public PageInfo<PlayHistory> findByUserIdAsPage(Integer pageNum, Integer pageSize, Long userId) {
        PageHelper.startPage(pageNum, pageSize);
        List<PlayHistory> list = playHistoryMapper.findByUserIdAsPage(userId, (pageNum - 1) * pageSize, pageSize);
        return new PageInfo<>(list);
    }

    @Override
    public PlayHistory findByUserIdAndMusicId(Long userId, Long musicId) {
        return playHistoryMapper.findByUserIdAndMusicId(userId, musicId);
    }

    @Override
    public Long recordPlayHistory(Long userId, Long musicId, Integer playDuration) {
//        PlayHistory existing = playHistoryMapper.findByUserIdAndMusicId(userId, musicId);
//
//        if (existing != null) {
//            // 更新现有记录
//            existing.setPlayDuration(playDuration);
//            existing.setPlayTime(LocalDateTime.now());
//            return playHistoryMapper.updatePlayHistory(existing);
//        } else {
        // 创建新记录
        PlayHistory playHistory = new PlayHistory();
        playHistory.setUserId(userId);
        playHistory.setMusicId(musicId);
        playHistory.setPlayDuration(playDuration);
        playHistory.setPlayTime(LocalDateTime.now());
        musicMapper.addPlayCount(musicId);
        playHistoryMapper.insert(playHistory);
        return playHistory.getId();
//        }
    }

    @Override
    public int deleteById(Long id, Long userId) {
        return playHistoryMapper.deleteById(id);
    }

    @Override
    public int clearByUserId(Long userId) {
        return 0;
    }

    @Override
    public void updatePlayHistory(Long id, Integer playDuration) {
        playHistoryMapper.updatePlayHistory(id, playDuration, CurrentUserUtil.getCurrentUserId());
    }

    @Override
    public PageInfo<MusicResponse> findMusicByUserIdAsPage(Integer pageNum, Integer pageSize, Long currentUserId) {

        PageHelper.startPage(pageNum, pageSize);

        List<MusicResponse> musics = musicMapper.selectMusicsFromHistories(currentUserId);

        for(MusicResponse music : musics) {
            music.setFavorite(isFavorite(music.getId()));
        }

        return PageInfo.of(musics);
    }

    private boolean isFavorite(Long musicId) {
        Favorite favorite = new Favorite("music", musicId);
        return favoriteMapper.findByUserAndTarget(favorite) != null;
    }
}