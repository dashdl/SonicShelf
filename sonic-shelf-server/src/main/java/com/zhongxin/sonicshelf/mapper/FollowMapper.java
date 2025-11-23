package com.zhongxin.sonicshelf.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface FollowMapper {

    @Select("select following_id from follows where follower_id = #{currentUserId}")
    Long[] selectFollowingIdByUserId(Long currentUserId);
}
