package com.zhongxin.sonicshelf.controller;

import com.github.pagehelper.PageInfo;
import com.zhongxin.sonicshelf.dto.request.PlaylistRequest;
import com.zhongxin.sonicshelf.dto.response.MusicResponse;
import com.zhongxin.sonicshelf.dto.response.PlaylistsResponse;
import com.zhongxin.sonicshelf.entity.Playlist;
import com.zhongxin.sonicshelf.exception.CustomException;
import com.zhongxin.sonicshelf.mapper.PlaylistMapper;
import com.zhongxin.sonicshelf.service.MusicService;
import com.zhongxin.sonicshelf.service.PlaylistService;
import com.zhongxin.sonicshelf.util.CurrentUserUtil;
import com.zhongxin.sonicshelf.util.Result;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@RestController
@RequestMapping("/api/v1/playlists")
public class PlaylistsController {

    @Resource
    private PlaylistService playlistsService;
    @Resource
    private MusicService musicService;
    @Resource
    private PlaylistMapper playlistMapper;

    @GetMapping("")
    public Result playlists(@RequestParam(required = false) Integer pageNum,
                            @RequestParam(required = false) Integer pageSize,
                            Long id) {
        Long targetUserId = (id == null ? CurrentUserUtil.getCurrentUserId() : id);

        if (pageNum == null || pageNum < 1) return Result.success(playlistsService.findAll(id));

        PageInfo<PlaylistsResponse> pageInfo = playlistsService.findAsPage(pageNum, pageSize, targetUserId);
        return Result.success(pageInfo);
    }

    @GetMapping("/{id}")
    public Result playlists(@PathVariable Long id) {
        return Result.success(playlistsService.findByPlaylistId(id));
    }

    @PutMapping("/{id}")
    public Result updatePlaylist(@PathVariable Long id,
                                 @RequestBody PlaylistRequest playlistRequest) {
        Playlist playlist = new Playlist(playlistRequest);
        playlist.setId(id);
        if (Objects.equals(playlistMapper.findCreatorByPlaylistId(id), CurrentUserUtil.getCurrentUserId())) {
            playlistsService.updatePlaylistTags(id, playlistRequest.getTags());
            playlistsService.updatePlaylist(playlist);
        } else {
            throw new CustomException("这不是您的歌单");
        }
        return Result.success(playlistsService.findByPlaylistId(id));
    }

    @GetMapping("/{id}/musics")
    public Result playlists(@PathVariable Long id,
                            @RequestParam(required = false) Integer pageNum,
                            @RequestParam(required = false) Integer pageSize) {

        if (pageNum == null || pageNum < 1) {
            return Result.success(musicService.findByListId(id));
        }

        PageInfo<MusicResponse> pageInfo = musicService.findAsPageByListId(pageNum, pageSize, id);
        return Result.success(pageInfo);
    }
}