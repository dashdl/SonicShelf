package com.zhongxin.sonicshelf.controller;

import com.zhongxin.sonicshelf.dto.response.MusicResponse;
import com.zhongxin.sonicshelf.dto.response.PlaylistsResponse;
import com.zhongxin.sonicshelf.entity.Music;
import com.zhongxin.sonicshelf.exception.CustomException;
import com.zhongxin.sonicshelf.service.FavoriteService;
import com.zhongxin.sonicshelf.util.FavoriteType;
import com.zhongxin.sonicshelf.util.Result;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;


@RestController
@RequestMapping("/api/v1/favorites")
public class FavoriteController {

    @Resource
    private FavoriteService favoriteService;

    @PostMapping("/music/{id}")
    public Result addFavoriteMusic(@PathVariable("id") Long playlistId) {
        return Result.success("收藏成功", favoriteService.addFavorite(FavoriteType.MUSIC.getValue(), playlistId));
    }

    @DeleteMapping("/music/{id}")
    public Result removeFavoriteMusic(@PathVariable("id") Long playlistId) {
        favoriteService.removeFavorite(FavoriteType.MUSIC.getValue(), playlistId);
        return Result.success("已取消收藏");
    }

    @PostMapping("/playlist/{id}")
    public Result addFavoritePlaylist(@PathVariable("id") Long playlistId) {
        return Result.success("收藏成功", favoriteService.addFavorite(FavoriteType.PLAYLIST.getValue(), playlistId));
    }

    @PostMapping("/playlist")//收藏全部
    public Result addFavoriteMusics(@RequestBody List<Music> musics) {
        List<String> successList = new ArrayList<>();
        for (Music music : musics) {
            try {
                favoriteService.addFavorite(FavoriteType.MUSIC.getValue(), music.getId());
                successList.add(music.getTitle());
            } catch (CustomException ignored) {}
        }
        return Result.success("收藏成功",successList);
    }

    @DeleteMapping("/playlist/{id}")
    public Result removeFavoritePlaylist(@PathVariable("id") Long playlistId) {
        favoriteService.removeFavorite(FavoriteType.PLAYLIST.getValue(), playlistId);
        return Result.success("已取消收藏");
    }

    @GetMapping("/playlists")
    public Result getFavoritePlaylists() {
        List<PlaylistsResponse> list = favoriteService.findPlaylists();
        return Result.success("获取成功", list);
    }
}
