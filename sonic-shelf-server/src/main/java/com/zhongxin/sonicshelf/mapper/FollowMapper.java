package com.zhongxin.sonicshelf.mapper;

import com.zhongxin.sonicshelf.dto.response.FollowingResponse;
import org.apache.ibatis.annotations.*;

import java.util.Collection;
import java.util.List;

@Mapper
public interface FollowMapper {

    @Select("select following_id from follows where follower_id = #{currentUserId}")
    Long[] selectFollowingIdByUserId(Long currentUserId);

    @Insert("insert into follows (follower_id, following_id) VALUES (#{currentUserId},#{id})")
    void addFollowById(Long currentUserId, Long id);

    @Delete("delete from follows where follower_id = #{currentUserId} and following_id = #{id}")
    void deleteFollowById(Long currentUserId, Long id);

    @Select("select count(*) > 0 from follows where follower_id = #{currentUserId} and following_id = #{id}")
    boolean selectFollow(@Param("currentUserId") Long currentUserId, @Param("id") Long id);

    @Select("select count(*) from follows where follower_id = #{id}")
    Integer countFollowingCount(Long id);

    @Select("select count(*) from follows where following_id = #{id}")
    Integer countFollowerCount(Long id);

    List<FollowingResponse> selectFollowingByUserId(Long currentUserId);

    Collection<? extends FollowingResponse> selectFollowingWithoutArtistByUserId(@Param("currentUserId") Long currentUserId,@Param("ids") List<Long> ids);

    List<FollowingResponse> selectFollowerByUserId(Long id);
}
