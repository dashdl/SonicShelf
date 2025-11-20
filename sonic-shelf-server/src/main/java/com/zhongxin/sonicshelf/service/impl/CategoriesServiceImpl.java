package com.zhongxin.sonicshelf.service.impl;

import com.zhongxin.sonicshelf.dto.response.CategoriesResponse;
import com.zhongxin.sonicshelf.dto.response.CategoryManageResponse;
import com.zhongxin.sonicshelf.entity.Categories;
import com.zhongxin.sonicshelf.exception.CustomException;
import com.zhongxin.sonicshelf.mapper.CategoriesMapper;
import com.zhongxin.sonicshelf.service.CategoriesService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoriesServiceImpl implements CategoriesService {
    @Resource
    CategoriesMapper categoriesMapper;

    @Override
    public CategoriesResponse findById(int id) {
        try {
            return categoriesMapper.selectById(id);
        } catch (RuntimeException e) {
            throw new CustomException("标签获取失败");
        }
    }

    @Override
    public List<CategoriesResponse> findByParentId(int parentId) {
        try {
            return categoriesMapper.selectByParentId(parentId);
        } catch (RuntimeException e) {
            throw new CustomException("标签获取失败");
        }
    }

    @Override
    public List<CategoriesResponse> findParents() {
        return categoriesMapper.selectParents();
    }

    @Override
    public List<Long> findCategoryIdsByTargetIdFromMusicCategories(List<Long> ids) {
        return categoriesMapper.selectCategoryIdsFromMusicCategories(ids);
    }

    @Override
    public CategoryManageResponse findCategoryManageResponseById(Integer id) {
        try {
            CategoryManageResponse categoryManageResponse = categoriesMapper.selectCategoryManageResponseById(id);
            categoryManageResponse.setMusicCount(categoriesMapper.countMusicCountByCategoryId(categoryManageResponse.getId()));
            categoryManageResponse.setPlaylistCount(categoriesMapper.countPlaylistCountByCategoryId(categoryManageResponse.getId()));
            return categoryManageResponse;
        } catch (RuntimeException e) {
            throw new CustomException("标签获取失败");
        }
    }

    @Override
    public List<CategoryManageResponse> findCategoryManageResponseByParentId(Integer parentId) {
        List<CategoryManageResponse> categories = categoriesMapper.selectCategoryManageResponseByParentId(parentId);
        for (CategoryManageResponse category : categories) {
            category.setMusicCount(categoriesMapper.countMusicCountByCategoryId(category.getId()));
            category.setPlaylistCount(categoriesMapper.countPlaylistCountByCategoryId(category.getId()));
        }
        return categories;
    }

    @Override
    public List<CategoryManageResponse> findParentCategoryManageResponse() {
        return categoriesMapper.selectParentCategoryManageResponse();
    }

    @Override
    public void updateCategory(Categories category) {

        categoriesMapper.updateCategory(category);

    }

    @Override
    public void addCategory(Categories category) {
        categoriesMapper.addCategory(category);
    }

    @Override
    public void deleteCategory(Integer id) {

        CategoriesResponse category = categoriesMapper.selectById(id);
        if (category.getParentId() != null) {
            categoriesMapper.deleteCategory(id);
        }else {
            categoriesMapper.deleteByParentId(id);
            categoriesMapper.deleteCategory(id);
        }
    }
}
