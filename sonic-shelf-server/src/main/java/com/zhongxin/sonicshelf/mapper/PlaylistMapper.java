package com.zhongxin.sonicshelf.mapper;

import com.zhongxin.sonicshelf.dto.request.PlaylistManageRequest;
import com.zhongxin.sonicshelf.dto.response.*;
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

    List<PlaylistManageResponse> selectPlaylist(@Param("keyword") String keyword, @Param("isPublic") Byte isPublic, @Param("categoryIds") Integer[] categoryIds);

    @Insert("insert into playlists (user_id,title,description,is_public) values(1,#{playlist.title},#{playlist.description},#{playlist.isPublic})")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    void addPlaylist(@Param("playlist") PlaylistManageRequest playlist);

    @Delete("delete from playlists where id = #{playlistId}")
    void deleteOfficialPlaylist(Long playlistId);
    @Select("select count(*) from playlists")
    int countPlaylistCount();

    @Select("select is_public from playlists where id = #{playlistId} limit 1")
    Integer selectPlaylistIsPublic(Long playlistId);

    void addMusic(Long playlistId, Long[] musicIds);

    @Delete("delete from playlist_musics where playlist_id = #{playlistId} and music_id = #{musicId}")
    void removeMusic(Long playlistId, Long musicId);

    @Update("update playlists set favorite_count = #{favoriteCount} where id = #{targetId}")
    void updateFavoriteCount(Long targetId, Integer favoriteCount);

    @Update("update playlists set play_count = play_count + 1 where id = #{id}")
    void addPlayCount(Long id);

    DynamicResponse selectPlaylistInfoById(Long targetId);

    List<PlaylistBaseResponse> selectPlaylistsForUser(String keyword);

    List<PlaylistBaseResponse> selectPlaylistsForSearch(String keyword);

    @Insert("insert into playlists (is_public,title,user_id) values (#{isPublic},#{title},#{currentUserId})")
    void insertPlaylist(int isPublic, String title, Long currentUserId);

    List<PlaylistBaseResponse> selectAsCard();

    List<PlaylistCardResponse> selectGuest(Integer limit);
}
