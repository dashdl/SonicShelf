package com.zhongxin.sonicshelf.mapper;

import com.zhongxin.sonicshelf.dto.response.PlaylistCardResponse;
import com.zhongxin.sonicshelf.dto.response.PlaylistsResponse;
import com.zhongxin.sonicshelf.entity.Playlist;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface PlaylistMapper {

    List<PlaylistsResponse> selectByUserId(Long id);


    PlaylistsResponse findByPlaylistId(Long id);

    List<PlaylistsResponse> selectFavoriteByUserId(Long currentUserId);

    @Update("update playlists set title=#{title},description=#{description},is_public=#{isPublic} where id=#{id}")
    void updatePlaylist(Playlist playlist);

    @Update("update playlists set cover_image=#{url} where id=#{id}")
    void updatePlaylistCover(@Param("url") String url, @Param("id") Long id);

    @Select("select user_id from playlists where id=#{id}")
    Long findCreatorByPlaylistId(Long id);


    List<PlaylistCardResponse> selectAll(Integer limit);

    List<String> selectByPlaylistId(Long i);

    @Update("insert into playlist_musics (playlist_id, music_id) VALUES (#{playlistId},#{musicId})")
    void insertPlaylistMusic(Long playlistId, Long musicId);

    @Select("SELECT COUNT(*) FROM playlist_musics WHERE playlist_id=#{playlistId}")
    Long countMusicCount(Long playlistId);

    @Update("update playlists set music_count = #{musicCount} where id = #{playlistId}")
    void updateMusicCount(Long playlistId, Long musicCount);

    @Select("select id from playlist_musics where playlist_id=#{playlistId} and music_id = #{musicId}")
    Long findByPlaylistIdAndMusicId(Long playlistId, Long musicId);

    @Delete("delete from playlist_musics where playlist_id=#{playlistId} and music_id = #{musicId}")
    void deletePlaylistMusic(Long playlistId, Long musicId);

    @Select("select user_id from playlists where id=#{playlistId}")
    Long selectUserIdByPlaylistId(Long playlistId);
}
