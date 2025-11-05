package com.zhongxin.sonicshelf.service;

import com.zhongxin.sonicshelf.dto.response.CategoriesResponse;
import com.zhongxin.sonicshelf.util.Result;

import java.util.List;


public interface CategoriesService {
    CategoriesResponse findById(int id);

    List<CategoriesResponse> findByParentId(int parentId);

    List<CategoriesResponse> findParents();
}
