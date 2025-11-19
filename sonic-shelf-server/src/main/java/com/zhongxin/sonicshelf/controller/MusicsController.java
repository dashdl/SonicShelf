package com.zhongxin.sonicshelf.controller;

import com.github.pagehelper.PageInfo;
import com.zhongxin.sonicshelf.annotation.AdminAuth;
import com.zhongxin.sonicshelf.dto.request.MusicManageRequest;
import com.zhongxin.sonicshelf.dto.response.AlbumManageResponse;
import com.zhongxin.sonicshelf.dto.response.MusicManageResponse;
import com.zhongxin.sonicshelf.service.MusicService;
import com.zhongxin.sonicshelf.util.Result;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/musics")
public class MusicsController {

    @Resource
    MusicService musicService;

    @GetMapping("/{id}")
    public Result findById(@PathVariable Long id) {
        return Result.success(musicService.findById(id));
    }

    @GetMapping("/lyrics/{id}")
    public Result findLyrics(@PathVariable Long id) {
        return Result.success("获取成功", musicService.findLyricsById(id));
    }

    @AdminAuth
    @GetMapping("/getAll")
    public Result getAllAlbum(@RequestParam Integer pageNum,
                              @RequestParam Integer pageSize,
                              @RequestParam(required = false) String keyword,
                              @RequestParam(required = false) Long artistId,
                              @RequestParam(required = false) Long albumId) {

        PageInfo<MusicManageResponse> pageInfo = musicService.findAlbumsAsPage(pageNum, pageSize, keyword, artistId, albumId);
        return Result.success(pageInfo);
    }

    @AdminAuth
    @PutMapping("/update")
    public Result update(@RequestBody MusicManageRequest music) {

        return Result.success(musicService.updateMusic(music));
    }

    @AdminAuth
    @PostMapping("/add")
    public Result add(@RequestBody MusicManageRequest music) {
        return Result.success(musicService.addMusic(music));
    }
}
