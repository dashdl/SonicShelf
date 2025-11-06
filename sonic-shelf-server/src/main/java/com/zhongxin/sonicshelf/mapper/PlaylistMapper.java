package com.zhongxin.sonicshelf.mapper;

import com.zhongxin.sonicshelf.dto.response.PlaylistsResponse;
import com.zhongxin.sonicshelf.entity.Playlist;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

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
}
