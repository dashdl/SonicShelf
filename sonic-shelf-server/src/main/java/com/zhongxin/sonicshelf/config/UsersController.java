package com.zhongxin.sonicshelf.config;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
public class UsersController {

    @GetMapping("/profile")
    public String profile() {



        return "profile";
    }
}
