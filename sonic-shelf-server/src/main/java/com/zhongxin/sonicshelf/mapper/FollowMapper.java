package com.zhongxin.sonicshelf.mapper;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface FollowMapper {

    @Select("select following_id from follows where follower_id = #{currentUserId}")
    Long[] selectFollowingIdByUserId(Long currentUserId);

    @Insert("insert into follows (follower_id, following_id) VALUES (#{currentUserId},#{id})")
    void addFollowById(Long currentUserId, Long id);

    @Delete("delete from follows where follower_id = #{currentUserId} and following_id = #{id}")
    void deleteFollowById(Long currentUserId, Long id);
}
