package com.zhongxin.sonicshelf.mapper;

import com.github.pagehelper.PageInfo;
import com.zhongxin.sonicshelf.dto.response.MusicResponse;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface MusicMapper {

    List<MusicResponse> findAsPageByListId(@Param("id") Long id,@Param("userId") Long userId);

    MusicResponse findById(@Param("id") Long id,@Param("userId") Long userId);
}
