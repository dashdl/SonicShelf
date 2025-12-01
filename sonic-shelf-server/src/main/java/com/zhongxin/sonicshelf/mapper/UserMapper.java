package com.zhongxin.sonicshelf.mapper;

import com.zhongxin.sonicshelf.dto.request.RegisterRequest;
import com.zhongxin.sonicshelf.dto.response.CollectorResponse;
import com.zhongxin.sonicshelf.dto.response.UserManageResponse;
import com.zhongxin.sonicshelf.entity.User;
import org.apache.ibatis.annotations.*;

import java.time.LocalDateTime;
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

    @Select("select u.id,u.bio,u.nickname,u.avatar,u.gender " +
            "from users u left join favorites f on u.id = f.user_id " +
            "where " +
            "f.target_type = #{targetType} and f.target_id = #{targetId}")
    List<CollectorResponse> selectByFavorite(String targetType, Long targetId);

    @Select("select * from users where id=#{id}")
    User selectById(Long id);

    List<UserManageResponse> selectUsersAsPage(String keyword, Integer status);

    @Update("update users set username=#{user.username},email=#{user.email},nickname=#{user.nickname},status=#{user.status} where id=#{user.id}")
    void updateUser(@Param("user") RegisterRequest user);

    @Insert("insert into users (username,password,email,nickname) values (#{user.username},#{user.password},#{user.email},#{user.nickname})")
    void insertUser(@Param("user") RegisterRequest user);

    @Insert("update users set status=#{status} where id = #{id}")
    void updateUserStatus(Long id, byte status);

    @Delete("delete from users where id=#{id}")
    void deleteUser(Long id);

    @Select("select count(*) from users")
    int countUserCount();

    List<CollectorResponse> selectUsersByKeyword(String keyword);

    @Update("update users set following_count = #{count} where id = #{id}")
    void updateFollowingCount(Integer count, Long id);

    @Update("update users set follower_count = #{count} where id = #{id}")
    void updateFollowerCount(Integer count, Long id);

    @Update("update users set last_login_at = #{now} where username = #{username}")
    void updateLoginTime(LocalDateTime now, String username);
}
