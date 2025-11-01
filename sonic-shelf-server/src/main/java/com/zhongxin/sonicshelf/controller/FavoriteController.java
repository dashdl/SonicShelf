package com.zhongxin.sonicshelf.controller;

import com.zhongxin.sonicshelf.dto.response.PlaylistsResponse;
import com.zhongxin.sonicshelf.service.FavoriteService;
import com.zhongxin.sonicshelf.util.FavoriteType;
import com.zhongxin.sonicshelf.util.Result;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/v1/favorites")
public class FavoriteController {

    @Resource
    private FavoriteService favoriteService;

    @PostMapping("/playlist/{id}")
    public Result addFavoritePlaylist(@PathVariable("id") Long playlistId) {

        return Result.success("收藏成功",favoriteService.addFavorite(FavoriteType.PLAYLIST.getValue(), playlistId));
    }

    @DeleteMapping("/playlist/{id}")
    public Result removeFavoritePlaylist(@PathVariable("id") Long playlistId) {
        favoriteService.removeFavoritePlaylist(FavoriteType.PLAYLIST.getValue(), playlistId);
        return Result.success("已取消收藏");
    }

    @GetMapping("/playlists")
    public Result getFavoritePlaylists() {

        List<PlaylistsResponse> list = favoriteService.findPlaylists();
        return Result.success("获取成功",list);
    }
}
