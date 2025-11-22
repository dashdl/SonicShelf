package com.zhongxin.sonicshelf.service;

import com.github.pagehelper.PageInfo;
import com.zhongxin.sonicshelf.dto.response.MusicResponse;
import com.zhongxin.sonicshelf.entity.PlayHistory;

public interface PlayHistoryService {
    PageInfo<PlayHistory> findByUserIdAsPage(Integer pageNum, Integer pageSize, Long userId);
    PlayHistory findByUserIdAndMusicId(Long userId, Long musicId);
    Long recordPlayHistory(Long userId, Long musicId, Integer playDuration);
    int deleteById(Long id, Long userId);
    int clearByUserId(Long userId);

    void updatePlayHistory(Long id, Integer playDuration);

    PageInfo<MusicResponse> findMusicByUserIdAsPage(Integer pageNum, Integer pageSize, Long currentUserId);
}