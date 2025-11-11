package com.zhongxin.sonicshelf.controller;

import com.zhongxin.sonicshelf.dto.response.MusicInfoResponse;
import com.zhongxin.sonicshelf.service.CategoriesService;
import com.zhongxin.sonicshelf.service.FavoriteService;
import com.zhongxin.sonicshelf.service.MusicService;
import com.zhongxin.sonicshelf.util.CurrentUserUtil;
import com.zhongxin.sonicshelf.util.RandomArraySelector;
import com.zhongxin.sonicshelf.util.Result;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
        List<Long> musicIds = favoriteService.findByUserIdAndTargetType(CurrentUserUtil.getCurrentUserId(), "music");
        List<Long> recommendedMusicIds = new ArrayList<>();
        if (!musicIds.isEmpty()) {
            List<Long> categoriesIds = categoriesService.findCategoryIdsByTargetIdFromMusicCategories(musicIds);
            if (!categoriesIds.isEmpty()) {
                recommendedMusicIds = musicService.findByCategoryId(categoriesIds);
                recommendedMusicIds = RandomArraySelector.selectWithoutDuplicate(recommendedMusicIds.toArray(new Long[0]), limit);
            }
            if (recommendedMusicIds.size() < limit) {
                Set<Long> recommendedSet = new HashSet<>(recommendedMusicIds);
                musicIds.stream()
                        .filter(id -> !recommendedSet.contains(id))
                        .limit(limit - recommendedMusicIds.size())
                        .forEach(recommendedMusicIds::add);
            }
        }
        List<MusicInfoResponse> musicInfoResponses = null;

        musicInfoResponses = musicService.findByIds(recommendedMusicIds);

        return Result.success(musicInfoResponses);
    }
}
