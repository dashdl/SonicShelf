package com.zhongxin.sonicshelf.controller;

import com.github.pagehelper.PageInfo;
import com.zhongxin.sonicshelf.dto.response.MusicResponse;
import com.zhongxin.sonicshelf.service.PlayHistoryService;
import com.zhongxin.sonicshelf.util.CurrentUserUtil;
import com.zhongxin.sonicshelf.util.Result;
import jakarta.annotation.Resource;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/play-histories")
public class PlayHistoryController {

    @Resource
    private PlayHistoryService playHistoryService;

    @GetMapping
    public Result getPlayHistories(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "20") Integer pageSize) {

        Long currentUserId = CurrentUserUtil.getCurrentUserId();
        PageInfo<MusicResponse> pageInfo = playHistoryService.findMusicByUserIdAsPage(pageNum, pageSize, currentUserId);
        return Result.success(pageInfo);
    }

    @PostMapping
    public Result recordPlayHistory(@RequestBody PlayHistoryRequest request) {
        Long currentUserId = CurrentUserUtil.getCurrentUserId();
        Long id = playHistoryService.recordPlayHistory(currentUserId, request.getMusicId(), request.getPlayDuration());

        if (id > 0) {
            return Result.success("播放历史记录成功", id);
        } else {
            return Result.error("播放历史记录失败");
        }
    }

    @PutMapping("/update")
    public Result updatePlayHistory(@RequestBody PlayHistoryRequest request) {
        try {
            playHistoryService.updatePlayHistory(request.getId(), request.getPlayDuration());
        } catch (Exception e) {
            return Result.error("播放历史记录失败");
        }
        return Result.success("播放历史更新成功");
    }

    @DeleteMapping("/delete/{id}")
    public Result deletePlayHistory(@PathVariable Long id) {
        Long currentUserId = CurrentUserUtil.getCurrentUserId();
        int result = playHistoryService.deleteById(id, currentUserId);

        if (result > 0) {
            return Result.success("播放历史删除成功");
        } else {
            return Result.error("播放历史删除失败");
        }
    }

    @DeleteMapping("/clear")
    public Result clearPlayHistory() {
        Long currentUserId = CurrentUserUtil.getCurrentUserId();
        int result = playHistoryService.clearByUserId(currentUserId);

        if (result > 0) {
            return Result.success("播放历史清空成功");
        } else {
            return Result.success("播放历史已清空");
        }
    }

    @Getter
    @Setter
    public static class PlayHistoryRequest {
        private Long id;
        private Long musicId;
        private Integer playDuration;
    }
}