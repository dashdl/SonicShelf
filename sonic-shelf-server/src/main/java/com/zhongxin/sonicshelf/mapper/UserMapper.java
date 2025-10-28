package com.zhongxin.sonicshelf.mapper;

import com.zhongxin.sonicshelf.entity.User;
import org.apache.ibatis.annotations.*;

@Mapper
public interface UserMapper {

    @Select("select * from users where username=#{username}")
    public User findByUsername(String username);

    @Select("select * from users where email=#{email}")
    public User findByEmail(String email);

    @Insert("insert into users(username,nickname,password) values(#{username},#{nickname},#{password})")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    public Long insert(User user);

    @Update("update users set nickname=#{user.nickname},bio=#{user.bio},gender=#{user.gender},birthday=#{user.birthday},location=#{user.location} where username=#{username}")
    public void updateUserProfile(@Param("username") String username, @Param("user") User user);

    @Update("update users set avatar=#{avatar} where username=#{username}")
    void updateUserAvatar(@Param("avatar") String url, @Param("username") String username);
}
