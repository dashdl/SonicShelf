package com.zhongxin.sonicshelf.dto.response;

import com.zhongxin.sonicshelf.entity.AdminInfo;
import lombok.Data;

@Data
public class AdminResponse {
    private String access_token;
    private String token_type="Bearer";
    private int expires_in=3600;
    private AdminInfo adminInfo;

    public AdminResponse() {}

    public AdminResponse(String access_token, String token_type, int expires_in, AdminInfo adminInfo) {
        this.access_token = access_token;
        this.token_type = token_type;
        this.expires_in = expires_in;
        this.adminInfo = adminInfo;
    }

    public AdminResponse(AdminInfo adminInfo){
        this.adminInfo = adminInfo;
    }
}
