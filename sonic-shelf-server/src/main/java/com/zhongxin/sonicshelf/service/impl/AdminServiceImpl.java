package com.zhongxin.sonicshelf.service.impl;

import com.zhongxin.sonicshelf.dto.response.AdminResponse;
import com.zhongxin.sonicshelf.dto.response.LoginResponse;
import com.zhongxin.sonicshelf.dto.response.MusicManageResponse;
import com.zhongxin.sonicshelf.entity.Admin;
import com.zhongxin.sonicshelf.entity.AdminInfo;
import com.zhongxin.sonicshelf.exception.CustomException;
import com.zhongxin.sonicshelf.mapper.AdminMapper;
import com.zhongxin.sonicshelf.service.AdminService;
import com.zhongxin.sonicshelf.util.JwtUtil;
import jakarta.annotation.Resource;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AdminServiceImpl implements AdminService {

    @Resource
    private AdminMapper adminMapper;

    @Resource
    private JwtUtil jwtUtil;


    @Override
    public AdminResponse login(Admin admin) {
        Admin tempAdmin = adminMapper.selectByRequest(admin);
        if (tempAdmin == null) {
            throw new CustomException("1002", "密码错误");
        } else {
            AdminResponse adminResponse = new AdminResponse(new AdminInfo(tempAdmin));
            adminResponse.setAccess_token(jwtUtil.generateToken(tempAdmin));
            return adminResponse;
        }
    }

    @Override
    public AdminInfo getInfoByUsername(String usernameFromToken) {
        return adminMapper.getInfoByUsername(usernameFromToken);
    }
}
