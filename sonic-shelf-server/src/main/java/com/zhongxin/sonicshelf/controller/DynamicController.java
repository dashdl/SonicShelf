package com.zhongxin.sonicshelf.controller;

import com.github.pagehelper.PageInfo;
import com.zhongxin.sonicshelf.dto.request.DynamicRequest;
import com.zhongxin.sonicshelf.dto.response.DynamicResponse;
import com.zhongxin.sonicshelf.service.DynamicService;
import com.zhongxin.sonicshelf.util.CurrentUserUtil;
import com.zhongxin.sonicshelf.util.Result;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/dynamic")
public class DynamicController {

    @Resource
    private DynamicService dynamicService;

    @GetMapping("/get-all")
    public Result getAll(@RequestParam Integer pageNum, @RequestParam Integer pageSize) {

        PageInfo<DynamicResponse> response = dynamicService.getAllDynamicByUserId(CurrentUserUtil.getCurrentUserId(), pageNum, pageSize);

        return Result.success(response);
    }

    @GetMapping("/get-self")
    public Result getSelf(@RequestParam Integer pageNum, @RequestParam Integer pageSize) {

        PageInfo<DynamicResponse> response = dynamicService.getDynamicByUserId(CurrentUserUtil.getCurrentUserId(), pageNum, pageSize);

        return Result.success(response);
    }

    @PostMapping("/add-dynamic")
    public Result addDynamic(@RequestBody DynamicRequest dynamic) {
        dynamicService.addDynamic(dynamic);
        return Result.success("动态发布成功", dynamic.getId());
    }

    @DeleteMapping("/delete-dynamic/{id}")
    public Result deleteDynamic(@PathVariable Long id) {
        dynamicService.deleteDynamic(id);
        return Result.success();
    }
}
