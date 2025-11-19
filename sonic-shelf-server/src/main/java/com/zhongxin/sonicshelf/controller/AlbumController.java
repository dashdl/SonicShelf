package com.zhongxin.sonicshelf.controller;

import com.github.pagehelper.PageInfo;
import com.zhongxin.sonicshelf.annotation.AdminAuth;
import com.zhongxin.sonicshelf.dto.request.AlbumManageRequest;
import com.zhongxin.sonicshelf.dto.response.AlbumManageResponse;
import com.zhongxin.sonicshelf.service.AlbumService;
import com.zhongxin.sonicshelf.service.MusicService;
import com.zhongxin.sonicshelf.util.Result;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

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

    @AdminAuth
    @GetMapping("/getAll")
    public Result getAllAlbum(@RequestParam Integer pageNum,
                              @RequestParam Integer pageSize,
                              @RequestParam(required = false) String keyword,
                              @RequestParam(required = false) Long artistId) {

        PageInfo<AlbumManageResponse> pageInfo = albumService.findAlbumsAsPage(pageNum, pageSize, keyword, artistId);
        return Result.success(pageInfo);
    }

    @AdminAuth
    @PutMapping("/update")
    public Result updateAlbum(@RequestBody AlbumManageRequest album){
        return Result.success(albumService.updateAlbum(album));
    }

    @AdminAuth
    @PostMapping("/add")
    public Result addAlbum(@RequestBody AlbumManageRequest album){
        return Result.success(albumService.addAlbum(album));
    }

}
