package com.zhongxin.sonicshelf.mapper;

import com.zhongxin.sonicshelf.dto.response.CollectorResponse;
import com.zhongxin.sonicshelf.dto.response.PlaylistsResponse;
import com.zhongxin.sonicshelf.dto.response.UserManageResponse;
import com.zhongxin.sonicshelf.dto.response.UserProfileResponse;
import com.zhongxin.sonicshelf.entity.User;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface UserMapper {

    @Select("select * from users where username=#{username}")
    User findByUsername(String username);

    @Select("select * from users where email=#{email}")
    User findByEmail(String email);

    @Insert("insert into users(username,nickname,password) values(#{username},#{nickname},#{password})")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    Long insert(User user);

    @Update("update users set nickname=#{user.nickname},bio=#{user.bio},gender=#{user.gender},birthday=#{user.birthday},location=#{user.location} where username=#{username}")
    void updateUserProfile(@Param("username") String username, @Param("user") User user);

    @Update("update users set avatar=#{avatar} where username=#{username}")
    void updateUserAvatar(@Param("avatar") String url, @Param("username") String username);

    @Select("select u.id,u.bio,u.nickname,u.avatar " +
            "from users u left join favorites f on u.id = f.user_id " +
            "where " +
            "f.target_type = #{targetType} and f.target_id = #{targetId}")
    List<CollectorResponse> selectByFavorite(String targetType, Long targetId);

    @Select("select * from users where id=#{id}")
    User selectById(Long id);

    List<UserManageResponse> selectUsersAsPage(String keyword, Integer status);
}
