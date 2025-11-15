package com.zhongxin.sonicshelf.controller;

import com.zhongxin.sonicshelf.dto.response.AlbumResponse;
import com.zhongxin.sonicshelf.dto.response.MusicResponse;
import com.zhongxin.sonicshelf.mapper.AlbumMapper;
import com.zhongxin.sonicshelf.mapper.MusicMapper;
import com.zhongxin.sonicshelf.service.AlbumService;
import com.zhongxin.sonicshelf.service.MusicService;
import com.zhongxin.sonicshelf.util.Result;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/album")
public class AlbumController {

    @Resource
    private AlbumService albumService;
    @Resource
    private MusicService musicService;

    @GetMapping("/{id}")
    public Result getAlbumInfoById(@PathVariable Long id) {
        return Result.success(albumService.findAlbumInfoById(id));
    }

    @GetMapping("/{id}/musics")
    public Result getMusicsById(@PathVariable Long id) {
        return Result.success(musicService.findMusicsByAlbumId(id));
    }
}
