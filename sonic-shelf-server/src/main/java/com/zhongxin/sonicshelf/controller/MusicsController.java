package com.zhongxin.sonicshelf.controller;

import com.zhongxin.sonicshelf.dto.response.MusicResponse;
import com.zhongxin.sonicshelf.service.MusicService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/musics")
public class MusicsController {

    @Resource
    MusicService musicService;

    @GetMapping("/{id}")
    public MusicResponse findById(@PathVariable Long id) {
        return musicService.findById(id);
    }
}
