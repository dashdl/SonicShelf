package com.zhongxin.sonicshelf.mapper;

import com.zhongxin.sonicshelf.dto.response.AlbumResponse;
import com.zhongxin.sonicshelf.dto.response.MusicInfoResponse;
import com.zhongxin.sonicshelf.dto.response.MusicResponse;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface MusicMapper {

    List<MusicResponse> findAsPageByListId(@Param("id") Long id,@Param("userId") Long userId);

    MusicResponse findById(@Param("id") Long id,@Param("userId") Long userId);

    MusicResponse selectById(Long id);

    List<MusicInfoResponse> selectByIds(List<Long> ids);

    @Select("select lyrics from musics where id=#{id}")
    String selectLyricById(Long id);

    List<MusicResponse> selectMusicsByArtistId(Long id);


    List<MusicResponse> selectMusicsByAlbumId(Long id);
}