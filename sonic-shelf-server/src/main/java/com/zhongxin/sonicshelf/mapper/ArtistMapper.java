package com.zhongxin.sonicshelf.mapper;

import com.zhongxin.sonicshelf.dto.response.ArtistResponse;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface ArtistMapper {

    @Select("select name,description,cover_image,album_count,follower_count,translated_name from artists where id=#{id}")
    ArtistResponse selectArtistById(Long id);
}
