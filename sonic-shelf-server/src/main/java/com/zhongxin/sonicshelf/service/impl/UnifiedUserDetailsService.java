package com.zhongxin.sonicshelf.service.impl;

import com.zhongxin.sonicshelf.entity.Admin;
import com.zhongxin.sonicshelf.entity.User;
import com.zhongxin.sonicshelf.mapper.AdminMapper;
import com.zhongxin.sonicshelf.mapper.UserMapper;
import jakarta.annotation.Resource;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * 统一的用户详情服务
 * 根据用户名自动判断是用户还是管理员
 */
@Service
public class UnifiedUserDetailsService implements UserDetailsService {

    @Resource
    private UserMapper userMapper;

    @Resource
    private AdminMapper adminMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 先在用户表中查找
        User user = userMapper.findByUsername(username);
        if (user != null) {
            return user;
        }

        // 如果用户表中没有，再在管理员表中查找
        Admin admin = adminMapper.findByUsername(username);
        if (admin != null) {
            return admin;
        }

        // 都没找到，抛出异常
        throw new UsernameNotFoundException("用户或管理员不存在: " + username);
    }
}