package com.zhongxin.sonicshelf.service;

import com.zhongxin.sonicshelf.dto.response.CategoriesResponse;
import com.zhongxin.sonicshelf.dto.response.CategoryManageResponse;
import com.zhongxin.sonicshelf.entity.Categories;
import com.zhongxin.sonicshelf.util.Result;

import java.util.List;


public interface CategoriesService {
    CategoriesResponse findById(int id);

    List<CategoriesResponse> findByParentId(int parentId);

    List<CategoriesResponse> findParents();

    List<Long> findCategoryIdsByTargetIdFromMusicCategories(List<Long> ids);

    CategoryManageResponse findCategoryManageResponseById(Integer id);

    List<CategoryManageResponse> findCategoryManageResponseByParentId(Integer parentId);

    List<CategoryManageResponse> findParentCategoryManageResponse();

    void updateCategory(Categories category);

    void addCategory(Categories category);

    void deleteCategory(Integer id);
}
