package com.zhongxin.sonicshelf.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zhongxin.sonicshelf.entity.PlayHistory;
import com.zhongxin.sonicshelf.mapper.PlayHistoryMapper;
import com.zhongxin.sonicshelf.service.PlayHistoryService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PlayHistoryServiceImpl implements PlayHistoryService {

    private final PlayHistoryMapper playHistoryMapper;

    public PlayHistoryServiceImpl(PlayHistoryMapper playHistoryMapper) {
        this.playHistoryMapper = playHistoryMapper;
    }

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
    public int recordPlayHistory(Long userId, Long musicId, Integer playDuration) {
        PlayHistory existing = playHistoryMapper.findByUserIdAndMusicId(userId, musicId);
        
        if (existing != null) {
            // 更新现有记录
            existing.setPlayDuration(playDuration);
            existing.setPlayTime(LocalDateTime.now());
            return playHistoryMapper.updatePlayHistory(existing);
        } else {
            // 创建新记录
            PlayHistory playHistory = new PlayHistory();
            playHistory.setUserId(userId);
            playHistory.setMusicId(musicId);
            playHistory.setPlayDuration(playDuration);
            playHistory.setPlayTime(LocalDateTime.now());
            return playHistoryMapper.insert(playHistory);
        }
    }

    @Override
    public int deleteById(Long id, Long userId) {
        return playHistoryMapper.deleteById(id);
    }

    @Override
    public int clearByUserId(Long userId) {
        // 这里需要根据userId删除，需要在Mapper中添加对应方法
        // 暂时返回0，后续在Mapper中实现
        return 0;
    }
}