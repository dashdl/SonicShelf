package com.zhongxin.sonicshelf.mapper;

import com.zhongxin.sonicshelf.entity.PlayHistory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface PlayHistoryMapper {
    List<PlayHistory> findByUserId(@Param("userId") Long userId);
    List<PlayHistory> findByUserIdAsPage(@Param("userId") Long userId, 
                                        @Param("offset") Integer offset, 
                                        @Param("pageSize") Integer pageSize);
    PlayHistory findByUserIdAndMusicId(@Param("userId") Long userId, @Param("musicId") Long musicId);
    int insert(PlayHistory playHistory);
    int updatePlayHistory(PlayHistory playHistory);
    int deleteById(@Param("id") Long id);
    int countByUserId(@Param("userId") Long userId);
}