package com.zhongxin.sonicshelf.mapper;

import com.zhongxin.sonicshelf.dto.response.FavoriteResponse;
import com.zhongxin.sonicshelf.dto.response.FollowingResponse;
import com.zhongxin.sonicshelf.entity.Favorite;
import org.apache.ibatis.annotations.*;

import java.util.Collection;
import java.util.List;

@Mapper
public interface FavoriteMapper {

    @Results({
            @Result(column = "id", property = "favoriteId"),
            @Result(column = "user_id", property = "userId"),
            @Result(column = "target_type", property = "targetType"),
            @Result(column = "target_id", property = "targetId"),
            @Result(column = "created_at", property = "createdAt")
    })
    @Options(useGeneratedKeys = true, keyProperty = "id")
    @Insert("insert into favorites ( user_id, target_type, target_id) values (#{userId},#{targetType},#{targetId})")
    void addFavorite(Favorite favorite);

    @Select("select * from favorites where user_id=#{userId} and target_type= #{targetType} and target_id=#{targetId}")
    Favorite findByUserAndTarget(Favorite favorite);

    @Select("select * from favorites where id=#{id}")
    FavoriteResponse selectFavoriteById(@Param("id") int id);

    @Delete("delete from favorites where user_id=#{userId} and target_type= #{targetType} and target_id=#{targetId}")
    void removeFavorite(Favorite favorite);

    @Select("select target_id from favorites where user_id=#{currentUserId} and target_type=#{targetType} limit 100")
    List<Long> selectByUserIdAndTargetType(Long currentUserId, String targetType);

    @Select("select target_id from favorites where target_type=#{targetType} and user_id = #{currentUserId}")
    List<Long> selectIdByUserIdAndTargetType(Long currentUserId, String targetType);

    @Select("select count(*) from favorites where target_type = #{targetType} and target_id = #{targetId}")
    Integer countFavoriteCount(String targetType, Long targetId);

    List<FollowingResponse> selectFollowingArtists(Long currentUserId);

    List<FollowingResponse> selectFollowingArtistsWithoutUser(Long currentUserId);


//    List<Long> selectByIds(@Param("ids") List<Long> ids);

//    @Select("select id from categories where id in #{ids}")
//    List<Long> selectByIds(List<Long> ids);
}
