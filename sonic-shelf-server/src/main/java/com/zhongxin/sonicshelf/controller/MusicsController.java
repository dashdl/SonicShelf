package com.zhongxin.sonicshelf.controller;

import com.github.pagehelper.PageInfo;
import com.zhongxin.sonicshelf.annotation.AdminAuth;
import com.zhongxin.sonicshelf.dto.request.MusicManageRequest;
import com.zhongxin.sonicshelf.dto.response.MusicInfoResponse;
import com.zhongxin.sonicshelf.dto.response.MusicManageResponse;
import com.zhongxin.sonicshelf.dto.response.MusicResponse;
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

    @GetMapping("/search")
    public Result search(@RequestParam Integer pageNum,
                         @RequestParam Integer pageSize,
                         @RequestParam String keyword) {
        PageInfo<MusicInfoResponse> pageInfo = musicService.findMusicsAsPageForUser(pageNum, pageSize, keyword);
        return Result.success(pageInfo);
    }

    @AdminAuth
    @GetMapping("/getAll")
    public Result getAllMusic(@RequestParam Integer pageNum,
                              @RequestParam Integer pageSize,
                              @RequestParam(required = false) String keyword,
                              @RequestParam(required = false) Long artistId,
                              @RequestParam(required = false) Long albumId,
                              @RequestParam(required = false) Integer[] categoryIds) {

        PageInfo<MusicManageResponse> pageInfo = musicService.findMusicsAsPage(pageNum, pageSize, keyword, artistId, albumId, categoryIds);
        return Result.success(pageInfo);
    }

    @AdminAuth
    @PutMapping("/update")
    public Result update(@RequestBody MusicManageRequest music) {
        musicService.updateMusicTags(music.getCategoryIds(), music.getId());
        return Result.success(musicService.updateMusic(music));
    }

    @AdminAuth
    @PostMapping("/add")
    public Result add(@RequestBody MusicManageRequest music) {
        MusicManageResponse musicManageResponse = musicService.addMusic(music);
        musicService.updateMusicTags(music.getCategoryIds(), music.getId());
        return Result.success(musicManageResponse);
    }

    @AdminAuth
    @DeleteMapping("/delete/{id}")
    public Result delete(@PathVariable Long id) {

        musicService.deleteMusicById(id);

        return Result.success("删除成功");
    }
}
