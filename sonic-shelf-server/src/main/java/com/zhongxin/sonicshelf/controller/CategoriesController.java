package com.zhongxin.sonicshelf.controller;


import com.zhongxin.sonicshelf.dto.response.CategoriesResponse;
import com.zhongxin.sonicshelf.service.CategoriesService;
import com.zhongxin.sonicshelf.util.Result;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/v1/categories")
public class CategoriesController {

    private static final Logger log = LoggerFactory.getLogger(CategoriesController.class);
    @Resource
    CategoriesService categoriesService;

    @GetMapping("")
    public Result getCategories(@RequestParam(required = false) Integer id,
                                @RequestParam(required = false) Integer parentId,
                                @RequestParam(required = false) List<Integer> tags) {

        if (tags != null && !tags.isEmpty()) {
            List<CategoriesResponse> categoriesResponses = new ArrayList<>();
            for (Integer tag : tags) {
                try {
                    CategoriesResponse category = categoriesService.findById(tag);
                    if (category != null) {
                        categoriesResponses.add(category);
                    }
                }catch (RuntimeException e) {
                    log.error("不存在的标签");
                }
            }

            return Result.success(categoriesResponses);
        }

        if (id != null && id > 0) {
            return Result.success(categoriesService.findById(id));
        }
        if (parentId != null && parentId > 0) {
            categoriesService.findById(parentId);
            return Result.success(categoriesService.findByParentId(parentId));
        }

        if (id == null && parentId == null) {
            return Result.success(categoriesService.findParents());
        }

        return Result.error("未找到分类标签");
    }

}
