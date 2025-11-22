package com.zhongxin.sonicshelf.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface DynamicMapper {

    @Update("update dynamics set like_count = #{likeCount} where id = #{targetId}")
    void updateLikeCount(Long targetId, Integer likeCount);
}
