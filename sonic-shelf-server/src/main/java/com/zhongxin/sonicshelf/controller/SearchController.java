package com.zhongxin.sonicshelf.controller;

import com.github.pagehelper.PageInfo;
import com.zhongxin.sonicshelf.dto.response.*;
import com.zhongxin.sonicshelf.service.*;
import com.zhongxin.sonicshelf.util.Result;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/search")
public class SearchController {

    @Resource
    private MusicService musicService;
    @Autowired
    private PlaylistService playlistService;
    @Autowired
    private AlbumService albumService;
    @Resource
    private ArtistService artistService;
    @Resource
    private UserService userService;

    @GetMapping("/musics")
    public Result searchMusics(@RequestParam String keyword,
                               @RequestParam Integer pageNum,
                               @RequestParam Integer pageSize) {
        PageInfo<MusicResponse> pageInfo = musicService.searchMusicsByKeyword(pageNum,pageSize,keyword);
        return Result.success(pageInfo);
    }

    @GetMapping("/playlists")
    public Result searchPlaylists(@RequestParam String keyword,
                               @RequestParam Integer pageNum,
                               @RequestParam Integer pageSize) {
        PageInfo<PlaylistBaseResponse> pageInfo = playlistService.searchPlaylistsByKeyword(pageNum,pageSize,keyword);
        return Result.success(pageInfo);
    }

    @GetMapping("/albums")
    public Result searchAlbums(@RequestParam String keyword,
                                  @RequestParam Integer pageNum,
                                  @RequestParam Integer pageSize) {
        PageInfo<AlbumInfoResponse> pageInfo = albumService.searchAlbumsByKeyword(pageNum,pageSize,keyword);
        return Result.success(pageInfo);
    }

    @GetMapping("/artists")
    public Result searchArtists(@RequestParam String keyword,
                               @RequestParam Integer pageNum,
                               @RequestParam Integer pageSize) {
        PageInfo<ArtistResponse> pageInfo = artistService.searchArtistsByKeyword(pageNum,pageSize,keyword);
        return Result.success(pageInfo);
    }

    @GetMapping("/users")
    public Result searchUsers(@RequestParam String keyword,
                              @RequestParam Integer pageNum,
                              @RequestParam Integer pageSize) {
        PageInfo<CollectorResponse> pageInfo = userService.searchUsersByKeyword(pageNum,pageSize,keyword);
        return Result.success(pageInfo);
    }
}
