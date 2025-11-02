package com.zhongxin.sonicshelf.controller;

import com.github.pagehelper.PageInfo;
import com.zhongxin.sonicshelf.dto.response.MusicResponse;
import com.zhongxin.sonicshelf.dto.response.PlaylistsResponse;
import com.zhongxin.sonicshelf.service.MusicService;
import com.zhongxin.sonicshelf.service.PlaylistService;
import com.zhongxin.sonicshelf.util.Result;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/playlists")
public class PlaylistsController {

    @Resource
    private PlaylistService playlistsService;
    @Resource
    private MusicService musicService;
    @GetMapping("")
    public Result playlists(@RequestParam(required = false) Integer pageNum,
                            @RequestParam(required = false) Integer pageSize,
                            Long id) {

        if (pageNum == null || pageNum < 1) {
            return  Result.success(playlistsService.findAll(id));
        }
        PageInfo<PlaylistsResponse> pageInfo = playlistsService.findAsPage(pageNum, pageSize, id);
        return Result.success(pageInfo);
    }

    @GetMapping("/{id}")
    public Result playlists(@PathVariable Long id) {
        return Result.success(playlistsService.findByPlaylistId(id));
    }

    @GetMapping("/{id}/musics")
    public Result playlists(@PathVariable Long id,
                            @RequestParam(required = false) Integer pageNum,
                            @RequestParam(required = false) Integer pageSize) {

        if (pageNum == null || pageNum < 1) {
            return  Result.success(musicService.findByListId(id));
        }

        PageInfo<MusicResponse> pageInfo = musicService.findAsPageByListId(pageNum, pageSize, id);
        return Result.success(pageInfo);
    }
}