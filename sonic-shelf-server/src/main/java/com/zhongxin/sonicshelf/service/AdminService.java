package com.zhongxin.sonicshelf.service;

import com.zhongxin.sonicshelf.dto.response.AdminResponse;
import com.zhongxin.sonicshelf.dto.response.MusicInfoResponse;
import com.zhongxin.sonicshelf.entity.Admin;
import com.zhongxin.sonicshelf.entity.AdminInfo;

public interface AdminService {
    AdminResponse login(Admin admin);

    AdminInfo getInfoByUsername(String usernameFromToken);
}
