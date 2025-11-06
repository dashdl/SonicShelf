package com.zhongxin.sonicshelf.mapper;

import org.apache.ibatis.annotations.*;

@Mapper
public interface LikeMapper {

    @Insert("insert into likes (target_type,target_id,user_id) values (#{targetType},#{targetId},#{userId})")
    void like(String targetType, Long targetId, Long userId);

    @Delete("delete from likes where target_type=#{targetType} and target_id=#{targetId} and user_id=#{userId}")
    void unLike(@Param("targetType") String targetType, @Param("targetId") Long targetId, @Param("userId") Long userId);

    @Select("select id from likes where target_type=#{targetType} and target_id=#{targetId} and user_id=#{userId}")
    Long selectLike(@Param("targetType") String targetType, @Param("targetId") Long targetId, @Param("userId") Long userId);
}
