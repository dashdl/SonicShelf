package com.zhongxin.sonicshelf.service;

import com.github.pagehelper.PageInfo;
import com.zhongxin.sonicshelf.dto.request.DynamicRequest;
import com.zhongxin.sonicshelf.dto.response.DynamicResponse;

public interface DynamicService {
    PageInfo<DynamicResponse> getAllDynamicByUserId(Long currentUserId, Integer pageNum, Integer pageSize);

    void addDynamic(DynamicRequest dynamic);

    PageInfo<DynamicResponse> getDynamicByUserId(Long currentUserId, Integer pageNum, Integer pageSize);

    void deleteDynamic(Long id);
}
