package com.zhongxin.sonicshelf.controller;

import com.github.pagehelper.PageInfo;
import com.zhongxin.sonicshelf.dto.response.PlaylistsResponse;
import com.zhongxin.sonicshelf.entity.Favorite;
import com.zhongxin.sonicshelf.entity.Music;
import com.zhongxin.sonicshelf.exception.CustomException;
import com.zhongxin.sonicshelf.mapper.FavoriteMapper;
import com.zhongxin.sonicshelf.service.FavoriteService;
import com.zhongxin.sonicshelf.util.CurrentUserUtil;
import com.zhongxin.sonicshelf.util.FavoriteType;
import com.zhongxin.sonicshelf.util.Result;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;


@RestController
@RequestMapping("/api/v1/favorites")
public class FavoriteController {

    @Resource
    private FavoriteService favoriteService;
    @Autowired
    private FavoriteMapper favoriteMapper;

    @PostMapping("/album/{id}")
    public Result addFavoriteAlbum(@PathVariable("id") Long musicId) {
        return Result.success("收藏成功", favoriteService.addFavorite(FavoriteType.ALBUM.getValue(), musicId));
    }

    @DeleteMapping("/album/{id}")
    public Result removeFavoriteAlbum(@PathVariable("id") Long musicId) {
        favoriteService.removeFavorite(FavoriteType.ALBUM.getValue(), musicId);
        return Result.success("已取消收藏");
    }

    @GetMapping("/albums")
    public Result getFavoriteAlbums(@RequestParam(required = false) Integer pageNum,
                                    @RequestParam(required = false) Integer pageSize) {
        if (pageNum == null || pageSize == null) {
            return Result.success(favoriteService.findAlbums());
        } else {
            return Result.success(favoriteService.findAlbumsAsPage(pageNum, pageSize));
        }
    }

    @PostMapping("/artist/{id}")
    public Result addFavoriteArtist(@PathVariable("id") Long musicId) {
        return Result.success("关注成功", favoriteService.addFavorite(FavoriteType.ARTIST.getValue(), musicId));
    }

    @DeleteMapping("/artist/{id}")
    public Result removeFavoriteArtist(@PathVariable("id") Long musicId) {
        favoriteService.removeFavorite(FavoriteType.ARTIST.getValue(), musicId);
        return Result.success("已取消关注");
    }

    @PostMapping("/music/{id}")
    public Result addFavoriteMusic(@PathVariable("id") Long playlistId) {
        return Result.success("收藏成功", favoriteService.addFavorite(FavoriteType.MUSIC.getValue(), playlistId));
    }

    @DeleteMapping("/music/{id}")
    public Result removeFavoriteMusic(@PathVariable("id") Long playlistId) {
        favoriteService.removeFavorite(FavoriteType.MUSIC.getValue(), playlistId);
        return Result.success("已取消收藏");
    }

    @GetMapping("/musics")
    public Result getFavoriteMusics(@RequestParam(required = false) Integer pageNum,
                                    @RequestParam(required = false) Integer pageSize) {
        if (pageNum == null || pageSize == null) {
            return Result.success(favoriteService.findMusics());
        } else {
            return Result.success(favoriteService.findMusicsAsPage(pageNum, pageSize));
        }
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
            } catch (CustomException ignored) {
            }
        }
        return Result.success("收藏成功", successList);
    }

    @DeleteMapping("/playlist/{id}")
    public Result removeFavoritePlaylist(@PathVariable("id") Long playlistId) {
        favoriteService.removeFavorite(FavoriteType.PLAYLIST.getValue(), playlistId);
        return Result.success("已取消收藏");
    }

    @GetMapping("/playlists")
    public Result getFavoritePlaylists(@RequestParam(required = false) Integer pageNum,
                                       @RequestParam(required = false) Integer pageSize,
                                       @RequestParam(required = false) Long id) {
        Long targetUserId = (id == null) ? CurrentUserUtil.getCurrentUserId() : id;

        if (pageNum == null || pageNum < 1) {
            List<PlaylistsResponse> list = favoriteService.findPlaylists(targetUserId);
            return Result.success("获取成功", list);
        } else {
            PageInfo<PlaylistsResponse> pageInfo = favoriteService.findAsPage(pageNum, pageSize, targetUserId);
            return Result.success("获取成功", pageInfo);
        }
    }

    @GetMapping("/collectors")
    public Result getFavoriteCollectors(@RequestParam(required = false) String targetType,
                                        @RequestParam(required = false) Long targetId) {
        try {
            return Result.success(favoriteService.findCollectorByTargetTypeAndTargetId(targetType, targetId));
        } catch (Exception e) {
            throw new CustomException("请求失败");
        }
    }

    @GetMapping("/isFavorite")
    public Result isFavorite(@RequestParam(required = false) String targetType,
                             @RequestParam(required = false) Long targetId) {
        Favorite favorite = new Favorite(targetType, targetId);

        return Result.success(favoriteMapper.findByUserAndTarget(favorite) != null);
    }
}
