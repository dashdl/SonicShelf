package com.zhongxin.sonicshelf.service;

import com.zhongxin.sonicshelf.dto.response.FollowingResponse;

import java.util.List;

public interface FollowService {
    void followById(Long id);

    void unFollowById(Long id);

    List<FollowingResponse> findAll(Long currentUserId, String type);

    List<FollowingResponse> findAllFollower(Long id);
}
