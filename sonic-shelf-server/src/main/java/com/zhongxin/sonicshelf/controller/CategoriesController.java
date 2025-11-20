package com.zhongxin.sonicshelf.controller;


import com.zhongxin.sonicshelf.annotation.AdminAuth;
import com.zhongxin.sonicshelf.dto.response.CategoriesResponse;
import com.zhongxin.sonicshelf.entity.Categories;
import com.zhongxin.sonicshelf.service.CategoriesService;
import com.zhongxin.sonicshelf.util.Result;
import jakarta.annotation.Resource;
import org.apache.ibatis.annotations.Delete;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

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
            return Result.success(categoriesService.findByParentId(parentId));
        }

        if (id == null && parentId == null) {
            return Result.success(categoriesService.findParents());
        }

        return Result.error("未找到分类标签");
    }

    @AdminAuth
    @GetMapping("/getAll")
    public Result getAllCategories(@RequestParam(required = false) Integer id,
                                   @RequestParam(required = false) Integer parentId) {

        if (id != null && id > 0) {
            return Result.success(categoriesService.findCategoryManageResponseById(id));
        }
        if (parentId != null && parentId > 0) {
            return Result.success(categoriesService.findCategoryManageResponseByParentId(parentId));
        }
        if (id == null && parentId == null) {
            return Result.success(categoriesService.findParentCategoryManageResponse());
        }
        return Result.error("未找到分类标签");
    }

    @AdminAuth
    @PutMapping("/update")
    public Result update(@RequestBody Categories category) {
        categoriesService.updateCategory(category);
        return Result.success("更新成功");
    }

    @AdminAuth
    @PostMapping("/add")
    public Result add(@RequestBody Categories category) {
        categoriesService.addCategory(category);
        return Result.success("添加成功");
    }

    @AdminAuth
    @DeleteMapping("/delete/{id}")
    public Result delete(@PathVariable Integer id) {
        categoriesService.deleteCategory(id);
        return Result.success("删除成功");
    }
}
