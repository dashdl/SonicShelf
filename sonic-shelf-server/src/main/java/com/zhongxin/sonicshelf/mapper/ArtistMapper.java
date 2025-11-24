package com.zhongxin.sonicshelf.mapper;

import com.zhongxin.sonicshelf.dto.request.ArtistManageRequest;
import com.zhongxin.sonicshelf.dto.response.ArtistManageResponse;
import com.zhongxin.sonicshelf.dto.response.ArtistResponse;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ArtistMapper {

    @Select("select name,description,cover_image,album_count,follower_count,translated_name,user_id from artists where id=#{id}")
    ArtistResponse selectArtistById(Long id);

    List<ArtistManageResponse> selectArtists(Integer pageNum, Integer pageSize, String keyword, Byte gender);

    @Delete("delete from artists where id=#{id}")
    void deleteUser(Long id);

    @Update("update artists set name=#{artist.name},gender=#{artist.gender},country=#{artist.country},description=#{artist.description} where id=#{artist.id}")
    void updateArtist(@Param("artist") ArtistManageRequest artist);

    @Update("update artists set music_count=#{count} where id=#{id}")
    void updateMusicCountByArtistId(int count, Long id);

    @Select("select id,name,gender,country,description,cover_image,music_count,created_at from artists where id=#{id}")
    ArtistManageResponse selectArtistByIdReturn(Long id);

    @Update("update artists set cover_image=#{path} where id=#{id}")
    void uploadArtistCover(String path, Long id);

    @Delete("delete from artists where id = #{id}")
    void deleteById(Long id);

    @Insert("insert into artists (name,gender,country,description) values (#{artist.name},#{artist.gender},#{artist.country},#{artist.description})")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    void addArtist(@Param("artist") ArtistManageRequest artist);

    @Select("select count(*) from albums where artist_id = #{artistId}")
    int countAlbumCountById(Long artistId);

    @Update("update artists set album_count = #{count} where id = #{artistId}")
    void updateAlbumCountByArtistId(int count, Long artistId);

    @Select("select count(*) from artists")
    int countArtistCount();

    @Update("update artists set follower_count = #{favoriteCount} where id = #{targetId}")
    void updateFollowerCount(Long targetId, Integer favoriteCount);

    List<ArtistResponse> selectArtistsByKeyword(String keyword);
}
