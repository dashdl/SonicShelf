package com.zhongxin.sonicshelf.controller;

import com.github.pagehelper.PageInfo;
import com.zhongxin.sonicshelf.annotation.AdminAuth;
import com.zhongxin.sonicshelf.dto.request.PlaylistManageRequest;
import com.zhongxin.sonicshelf.dto.request.PlaylistRequest;
import com.zhongxin.sonicshelf.dto.response.MusicResponse;
import com.zhongxin.sonicshelf.dto.response.PlaylistManageResponse;
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

import java.util.Arrays;
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

        if (pageNum == null || pageNum < 1) return Result.success(playlistsService.findAllUserId(id));

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

    @GetMapping("/recommend")
    public Result recommend(@RequestParam(required = false) Integer limit) {
        return Result.success(playlistsService.findAll(limit));
    }

    @PostMapping("/{playlistId}/collect/{musicId}")
    public Result collect(@PathVariable Long playlistId,
                          @PathVariable Long musicId) {

        if (Objects.equals(playlistsService.findUserIdByPlaylistId(playlistId), CurrentUserUtil.getCurrentUserId())) {
            if (musicService.findById(musicId) != null && playlistsService.findByPlaylistId(playlistId) != null)
                playlistsService.collectMusicByPlaylistIdAndMusicId(playlistId, musicId);
            playlistsService.updateMusicCount(playlistId);
        } else {
            throw new CustomException("这不是您的歌单");
        }
        return Result.success();
    }

    @DeleteMapping("/{playlistId}/collect/{musicId}")
    public Result deleteMusic(@PathVariable Long playlistId,
                              @PathVariable Long musicId) {

        if (Objects.equals(playlistsService.findUserIdByPlaylistId(playlistId), CurrentUserUtil.getCurrentUserId())) {

            if (musicService.findById(musicId) != null && playlistsService.findByPlaylistId(playlistId) != null)
                playlistsService.deleteMusicByPlaylistIdAndMusicId(playlistId, musicId);
            playlistsService.updateMusicCount(playlistId);
        } else {
            throw new CustomException("这不是您的歌单");
        }
        return Result.success();
    }

    @AdminAuth
    @GetMapping("/getAll")
    public Result getAll(@RequestParam Integer pageNum,
                         @RequestParam Integer pageSize,
                         @RequestParam(required = false) String keyword,
                         @RequestParam(required = false) Integer[] categoryIds,
                         @RequestParam(required = false) Byte isPublic) {
        PageInfo<PlaylistManageResponse> pageInfo = playlistsService.findPlaylistAsPage(pageNum, pageSize, keyword, categoryIds, isPublic);
        return Result.success(pageInfo);
    }

    @AdminAuth
    @PostMapping("/add")
    public Result addPlaylist(@RequestBody PlaylistManageRequest playlist) {

        if (playlist.getIsPublic() != 2) {
            throw new CustomException("3003", "请勿添加用户歌单");
        }

        return Result.success("歌单添加成功", playlistsService.addPlaylist(playlist));
    }

    @AdminAuth
    @PutMapping("/update")
    public Result updatePlaylist(@RequestBody PlaylistManageRequest playlist) {

        if (playlist.getIsPublic() != 2) {
            throw new CustomException("3003", "请勿编辑用户歌单");
        }

        playlistsService.updateOfficialPlaylist(playlist);
        playlistsService.updatePlaylistTags(playlist.getId(), Arrays.stream(playlist.getCategoryIds())
                .map(Integer::longValue)
                .toArray(Long[]::new));

        return Result.success("歌单信息修改成功");
    }

    @AdminAuth
    @DeleteMapping("/delete/{id}")
    public Result deletePlaylist(@PathVariable Long id) {

        playlistsService.deleteOfficialPlaylist(id);

        return Result.success();
    }

    @AdminAuth
    @GetMapping("/{playlistId}/musics/admin")
    public Result getPlaylistMusics(@PathVariable Long playlistId) {
        if (!playlistsService.isPublic(playlistId)) {
            throw new CustomException("3004", "请勿试图查看非公开歌单");
        }

        return Result.success("获取成功", musicService.findPlaylistMusicResponseByPlaylistId(playlistId));
    }

    @AdminAuth
    @PostMapping("/{playlistId}/add-musics")
    public Result addPlaylistMusic(@PathVariable Long playlistId,
                                   @RequestBody Long[] musicIds) {
        if (!playlistsService.isOfficial(playlistId)) {
            throw new CustomException("3004", "请勿编辑用户歌单");
        }

        playlistsService.addMusic(playlistId, musicIds);
        playlistsService.updateMusicCount(playlistId);
        return Result.success("添加成功");
    }

    @AdminAuth
    @DeleteMapping("/{playlistId}/remove-music/{musicId}")
    public Result removePlaylistMusic(@PathVariable Long playlistId,
                                      @PathVariable Long musicId) {
        if (!playlistsService.isOfficial(playlistId)) {
            throw new CustomException("3004", "请勿编辑用户歌单");
        }
        playlistsService.removeMusic(playlistId, musicId);
        playlistsService.updateMusicCount(playlistId);
        return Result.success("添加成功");
    }
}