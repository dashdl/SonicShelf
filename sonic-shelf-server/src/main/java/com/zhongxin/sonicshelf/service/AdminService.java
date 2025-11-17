package com.zhongxin.sonicshelf.service;

import com.zhongxin.sonicshelf.dto.response.AdminResponse;
import com.zhongxin.sonicshelf.entity.Admin;

public interface AdminService {
    AdminResponse login(Admin admin);
}
