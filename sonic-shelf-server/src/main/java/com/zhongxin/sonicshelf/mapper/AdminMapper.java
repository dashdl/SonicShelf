package com.zhongxin.sonicshelf.mapper;

import com.zhongxin.sonicshelf.entity.Admin;
import com.zhongxin.sonicshelf.entity.AdminInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface AdminMapper {

    @Select("select * from admins where username = #{username}")
    Admin findByUsername(String username);

    @Select("select * from admins where username = #{admin.username} and password = #{admin.password} ")
    Admin selectByRequest(@Param("admin") Admin admin);

    @Select("select * from admins where username = #{usernameFromToken}")
    AdminInfo getInfoByUsername(String usernameFromToken);
}
