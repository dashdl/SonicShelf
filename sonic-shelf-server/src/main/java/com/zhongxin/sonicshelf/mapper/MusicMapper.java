package com.zhongxin.sonicshelf.mapper;

import com.zhongxin.sonicshelf.dto.request.MusicManageRequest;
import com.zhongxin.sonicshelf.dto.response.*;
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

    List<MusicManageResponse> selectMusics(String keyword, Long artistId, Long albumId, Integer[] categoryIds);

    @Update("update musics set title=#{music.title},artist_id=#{music.artistId},album_id=#{music.albumId},duration=#{music.duration},lyrics=#{music.lyrics} where id=#{music.id}")
    void updateMusic(@Param("music") MusicManageRequest music);

    MusicManageResponse selectMusicManageResponseById(Long id);

    @Insert("insert into musics set title=#{music.title},artist_id=#{music.artistId},album_id=#{music.albumId},duration=#{music.duration},lyrics=#{music.lyrics}")
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

    @Select("select count(*) from musics")
    int countMusicCount();

    List<PlaylistMusicResponse> selectPlaylistMusicResponseByPlaylistId(Long playlistId);

    @Update("update musics set favorite_count = #{favoriteCount} where id = #{targetId}")
    void updateFavoriteCount(Long targetId, Integer favoriteCount);

    @Select("select artist_id from musics where id = #{id}")
    Long getArtistIdById(Long id);

    @Select("select album_id from musics where id = #{id}")
    Long getAlbumIdById(Long id);

    @Update("update musics set play_count = play_count + 1 where id = #{id}")
    void addPlayCount(Long id);

    List<MusicResponse> selectMusicsFromHistories(@Param("currentUserId") Long currentUserId);

    DynamicResponse selectMusicInfoById(Long targetId);

    List<MusicInfoResponse> selectMusicsForUser(String keyword);
}