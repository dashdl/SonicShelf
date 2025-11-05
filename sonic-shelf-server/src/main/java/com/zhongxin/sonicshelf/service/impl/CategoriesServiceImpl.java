package com.zhongxin.sonicshelf.service.impl;

import com.zhongxin.sonicshelf.dto.response.CategoriesResponse;
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
}
