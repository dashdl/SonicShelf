package com.zhongxin.sonicshelf.mapper;

import com.zhongxin.sonicshelf.dto.request.MusicManageRequest;
import com.zhongxin.sonicshelf.dto.response.MusicInfoResponse;
import com.zhongxin.sonicshelf.dto.response.MusicManageResponse;
import com.zhongxin.sonicshelf.dto.response.MusicResponse;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface MusicMapper {

    List<MusicResponse> findAsPageByListId(@Param("id") Long id, @Param("userId") Long userId);

    MusicResponse findById(@Param("id") Long id, @Param("userId") Long userId);

    MusicResponse selectById(Long id);

    List<MusicInfoResponse> selectByIds(List<Long> ids);

    @Select("select lyrics from musics where id=#{id}")
    String selectLyricById(Long id);

    List<MusicResponse> selectMusicsByArtistId(Long id);


    List<MusicResponse> selectMusicsByAlbumId(Long id);

    List<MusicResponse> selectMusicsByIds(List<Long> ids);

    @Select("SELECT COUNT(*) FROM musics WHERE artist_id = #{artistId}")
    int countByArtistId(Long artistId);

    @Delete("delete from musics where artist_id = #{id}")
    void deleteByArtistId(Long id);

    List<MusicManageResponse> selectMusics(String keyword, Long artistId, Long albumId);

    @Update("update musics set title=#{music.title},artist_id=#{music.artistId},album_id=#{music.albumId},duration=#{music.duration} where id=#{music.id}")
    void updateMusic(@Param("music") MusicManageRequest music);

    MusicManageResponse selectMusicManageResponseById(Long id);

    @Insert("insert into musics set title=#{music.title},artist_id=#{music.artistId},album_id=#{music.albumId},duration=#{music.duration}")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    void addMusic(@Param("music") MusicManageRequest music);

    @Update("update musics set cover_image=#{path} where id=#{id}")
    void uploadMusicCover(String path, Long id);

    @Update("update musics set file_url=#{path} where id=#{id}")
    void uploadMusicFile(String path, Long id);

    @Select("select count(*) from musics where album_id=#{albumId}")
    int countByAlbumId(Long albumId);

    @Delete("delete from musics where id = #{id}")
    void deleteById(Long id);

    @Delete("delete from musics where album_id = #{id}")
    void deleteByAlbumId(Long id);
}