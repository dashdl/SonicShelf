package com.zhongxin.sonicshelf.mapper;

import com.zhongxin.sonicshelf.entity.User;
import org.apache.ibatis.annotations.*;

@Mapper
public interface UserMapper {

    @Select("select * from users where username=#{username}")
    public User findByUsername(String username);

    @Select("select * from users where email=#{email}")
    public User findByEmail(String email);

    @Insert("insert into users(username,email,nickname,password) values(#{username},#{email},#{nickname},#{password})")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    public Long insert(User user);

    @Update("update users set nickname=#{user.nickname},bio=#{user.bio},gender=#{user.gender},location=#{user.location} where username=#{username}")
    public void updateUserProfile(@Param("username") String username, @Param("user") User user);
}
