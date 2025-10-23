package com.zhongxin.sonicshelf.mapper;

import com.zhongxin.sonicshelf.entity.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper {

    @Select("select * from users where username=#{username}")
    public User findByUsername(String username);

    @Select("select * from users where email=#{email}")
    public User findByEmail(String email);

    @Insert("insert into users(username,email,nickname,password) values(#{username},#{email},#{nickname},#{password})")
    public void insert(User user);
}
