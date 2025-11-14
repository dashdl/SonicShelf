package com.zhongxin.sonicshelf.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zhongxin.sonicshelf.dto.response.ArtistResponse;
import com.zhongxin.sonicshelf.dto.response.MusicResponse;
import com.zhongxin.sonicshelf.service.ArtistService;
import com.zhongxin.sonicshelf.service.MusicService;
import com.zhongxin.sonicshelf.util.Result;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/artist")
public class ArtistController {

    @Resource
    private ArtistService artistService;
    @Resource
    private MusicService musicService;

    @GetMapping("/{id}")
    public Result getArtistById(@PathVariable("id") Long id) {

        ArtistResponse artistResponse = artistService.findArtistById(id);
        artistResponse.setId(id);
        return Result.success(artistResponse);
    }


    @GetMapping("/musics/{id}")
    public Result getMusicsByArtistId(@PathVariable("id") Long id,
                                      @RequestParam(required = false) Integer pageNum,
                                      @RequestParam(required = false) Integer pageSize) {

        if (pageNum == null || pageSize == null) {
            return Result.success(musicService.findMusicsByArtistId(id));
        } else {
            PageHelper.startPage(pageNum, pageSize);
            return Result.success(PageInfo.of(musicService.findMusicsByArtistId(id)));
        }

    }
}
