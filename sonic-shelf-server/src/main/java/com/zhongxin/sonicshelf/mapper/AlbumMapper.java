package com.zhongxin.sonicshelf.mapper;

import com.zhongxin.sonicshelf.dto.request.AlbumManageRequest;
import com.zhongxin.sonicshelf.dto.response.AlbumInfoResponse;
import com.zhongxin.sonicshelf.dto.response.AlbumManageResponse;
import com.zhongxin.sonicshelf.dto.response.AlbumResponse;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface AlbumMapper {


    @Select("select id,title,cover_image,music_count,release_date from albums where artist_id = #{id}")
    List<AlbumResponse> selectAlbumsByArtistId(Long id);


    AlbumInfoResponse selectAlbumsById(Long id);


    List<AlbumInfoResponse> selectAlbumByIds(List<Long> ids);

    @Delete("delete from albums where artist_id = #{id}")
    void deleteByArtistId(Long id);

    List<AlbumManageResponse> selectAlbums(String keyword, Long artistId);

    @Update("update albums set title = #{album.title}, albums.artist_id=#{album.artistId},release_date = #{album.releaseDate},description = #{album.description} where id = #{album.id}")
    void updateAlbum(@Param("album") AlbumManageRequest album);

    AlbumManageResponse selectAlbumManageResponseById(Long id);

    @Insert("insert into albums (title, artist_id, description, release_date) values (#{album.title},#{album.artistId},#{album.description},#{album.releaseDate})")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    void insertAlbum(@Param("album") AlbumManageRequest album);

    @Update("update albums set cover_image = #{coverImage} where id = #{id}")
    void uploadArtistCover(String coverImage, Long id);

    @Select("select count(*) from musics where album_id=#{id}")
    int countMusicCountById(Long id);

    @Update("update albums set music_count = #{count} where id = #{albumId}")
    void updateMusicCountByAlbumId(int count, Long albumId);

    @Select("select count(*) from albums where artist_id = #{id}")
    int countByArtistId(Long id);

    @Delete("delete from albums where id = #{id}")
    void deleteById(Long id);
}
