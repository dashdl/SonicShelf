package com.zhongxin.sonicshelf.controller;

import com.zhongxin.sonicshelf.annotation.AdminAuth;
import com.zhongxin.sonicshelf.service.*;
import com.zhongxin.sonicshelf.util.Result;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/dashboard")
public class DashboardController {

    @Resource
    private DashboardService dashboardService;

    @AdminAuth
    @GetMapping("/statistics")
    public Result statistics() {
        return Result.success(dashboardService.getCount());
    }

}
