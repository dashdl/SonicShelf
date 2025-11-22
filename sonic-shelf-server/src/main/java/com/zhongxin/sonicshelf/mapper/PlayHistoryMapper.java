package com.zhongxin.sonicshelf.mapper;

import com.zhongxin.sonicshelf.entity.PlayHistory;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PlayHistoryMapper {
    List<PlayHistory> findByUserId(@Param("userId") Long userId);

    List<PlayHistory> findByUserIdAsPage(@Param("userId") Long userId,
                                         @Param("offset") Integer offset,
                                         @Param("pageSize") Integer pageSize);

    PlayHistory findByUserIdAndMusicId(@Param("userId") Long userId, @Param("musicId") Long musicId);

    @Insert("INSERT INTO play_histories(user_id, music_id, play_duration, play_time)" +
            "VALUES(#{userId}, #{musicId}, #{playDuration}, #{playTime})")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    int insert(PlayHistory playHistory);

    void updatePlayHistory(@Param("id") Long id,@Param("playDuration") Integer playDuration, @Param("userId") Long userId);

    int deleteById(@Param("id") Long id);

    int countByUserId(@Param("userId") Long userId);
}