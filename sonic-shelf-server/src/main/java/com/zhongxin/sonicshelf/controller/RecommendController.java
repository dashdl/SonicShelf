package com.zhongxin.sonicshelf.controller;

import com.zhongxin.sonicshelf.dto.response.MusicInfoResponse;
import com.zhongxin.sonicshelf.service.CategoriesService;
import com.zhongxin.sonicshelf.service.FavoriteService;
import com.zhongxin.sonicshelf.service.MusicService;
import com.zhongxin.sonicshelf.util.CurrentUserUtil;
import com.zhongxin.sonicshelf.util.Result;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/v1/recommend")
public class RecommendController {

    @Resource
    MusicService musicService;
    @Resource
    CategoriesService categoriesService;
    @Resource
    FavoriteService favoriteService;


    @GetMapping("/music/likes")
    public Result findById(@RequestParam(required = false) Integer limit) {
        List<Long> ids = favoriteService.findByUserIdAndTargetType(CurrentUserUtil.getCurrentUserId(),"music");

//        List<Long> categoryIds = favoriteService.findByIds(ids);

        List<Long> musics = musicService.findByCategoryId(ids);

        List<MusicInfoResponse> musicInfoResponses = musicService.findByIds(musics);
//        List<MusicInfoResponse> musics = favoriteService.findMusicByLikes(limit);

        return Result.success();
    }
}
