package com.zhongxin.sonicshelf.mapper;

import com.github.pagehelper.PageInfo;
import com.zhongxin.sonicshelf.dto.response.MusicResponse;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MusicMapper {

    List<MusicResponse> findAsPageByListId(Long id);
}
