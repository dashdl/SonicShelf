package com.zhongxin.sonicshelf.mapper;

import com.zhongxin.sonicshelf.dto.response.PlaylistsResponse;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface PlaylistMapper {

    List<PlaylistsResponse> selectByUserId(Long id);


    PlaylistsResponse findByPlaylistId(Long id);
}
