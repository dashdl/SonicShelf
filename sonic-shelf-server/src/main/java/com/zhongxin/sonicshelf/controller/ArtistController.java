package com.zhongxin.sonicshelf.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zhongxin.sonicshelf.dto.request.ArtistManageRequest;
import com.zhongxin.sonicshelf.dto.request.RegisterRequest;
import com.zhongxin.sonicshelf.dto.response.ArtistManageResponse;
import com.zhongxin.sonicshelf.dto.response.ArtistResponse;
import com.zhongxin.sonicshelf.dto.response.UserManageResponse;
import com.zhongxin.sonicshelf.service.ArtistService;
import com.zhongxin.sonicshelf.service.MusicService;
import com.zhongxin.sonicshelf.util.JwtUtil;
import com.zhongxin.sonicshelf.util.Result;
import com.zhongxin.sonicshelf.util.TokenExtractor;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Update;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/artist")
public class ArtistController {

    @Resource
    private ArtistService artistService;
    @Resource
    private MusicService musicService;
    @Autowired
    JwtUtil jwtUtil;

    @GetMapping("/{id}")
    public Result getArtistById(@PathVariable("id") Long id) {

        ArtistResponse artistResponse = artistService.findArtistById(id);
        artistResponse.setId(id);
        return Result.success(artistResponse);
    }


    @GetMapping("/{id}/musics")
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

    @GetMapping("/{id}/albums")
    public Result getAlbumsByArtistId(@PathVariable("id") Long id,
                                      @RequestParam(required = false) Integer pageNum,
                                      @RequestParam(required = false) Integer pageSize) {

        if (pageNum == null || pageSize == null) {
            return Result.success(artistService.findAlbumsByArtistId(id));
        } else {
            PageHelper.startPage(pageNum, pageSize);
            return Result.success(PageInfo.of(artistService.findAlbumsByArtistId(id)));
        }
    }

    @GetMapping("/getAll")
    public Result getAll(@RequestParam Integer pageNum,
                         @RequestParam Integer pageSize,
                         @RequestParam(required = false) String keyword,
                         @RequestParam(required = false) Byte gender,
                         HttpServletRequest request) {
        String token = TokenExtractor.extractToken(request);
        if (!jwtUtil.isAdmin(token)) {
            return Result.error("需要管理员权限");
        }

        PageInfo<ArtistManageResponse> pageInfo = artistService.findArtistsAsPage(pageNum, pageSize, keyword, gender);
        return Result.success(pageInfo);
    }

    @PostMapping("/add")
    public Result add(@RequestBody ArtistManageRequest artist, HttpServletRequest request) {
        String token = TokenExtractor.extractToken(request);
        if (!jwtUtil.isAdmin(token)) {
            return Result.error("需要管理员权限");
        }

        return Result.success(artistService.addArtist(artist));

    }

    @PutMapping("/update")
    public Result updateArtist(@RequestBody ArtistManageRequest artist, HttpServletRequest request) {
        String token = TokenExtractor.extractToken(request);
        if (!jwtUtil.isAdmin(token)) {
            return Result.error("需要管理员权限");
        }

        return Result.success(artistService.updateArtist(artist));
    }

    @DeleteMapping("/delete/{id}")
    public Result deleteArtist(@PathVariable Long id, HttpServletRequest request) {
        String token = TokenExtractor.extractToken(request);
        if (!jwtUtil.isAdmin(token)) {
            return Result.error("需要管理员权限");
        }

        artistService.deleteArtist(id);

        return Result.success("删除成功");
    }

//    @PostMapping("/add")
//    public Result addArtist(@RequestBody ArtistManageRequest artist, HttpServletRequest request) {
//        String token = TokenExtractor.extractToken(request);
//        if (!jwtUtil.isAdmin(token)) {
//            return Result.error("需要管理员权限");
//        }
//
//        return Result.success(artistService.updateArtist(artist));
//    }
}
