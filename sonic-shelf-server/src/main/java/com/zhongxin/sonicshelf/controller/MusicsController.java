package com.zhongxin.sonicshelf.controller;

import com.zhongxin.sonicshelf.service.MusicService;
import com.zhongxin.sonicshelf.util.Result;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/musics")
public class MusicsController {

    @Resource
    MusicService musicService;

    @GetMapping("/{id}")
    public Result findById(@PathVariable Long id) {
        return Result.success(musicService.findById(id));
    }

}
