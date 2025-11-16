package com.zhongxin.sonicshelf.mapper;

import com.zhongxin.sonicshelf.dto.response.AlbumInfoResponse;
import com.zhongxin.sonicshelf.dto.response.AlbumResponse;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface AlbumMapper {


    @Select("select id,title,cover_image,music_count,release_date from albums where artist_id = #{id}")
    List<AlbumResponse> selectAlbumsByArtistId(Long id);


    AlbumInfoResponse selectAlbumsById(Long id);


    List<AlbumInfoResponse> selectAlbumByIds(List<Long> ids);
}
