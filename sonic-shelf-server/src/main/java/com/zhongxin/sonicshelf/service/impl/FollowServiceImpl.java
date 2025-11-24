package com.zhongxin.sonicshelf.service.impl;

import com.zhongxin.sonicshelf.mapper.FollowMapper;
import com.zhongxin.sonicshelf.service.FollowService;
import com.zhongxin.sonicshelf.util.CurrentUserUtil;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class FollowServiceImpl implements FollowService {

    @Resource
    private FollowMapper followMapper;

    @Override
    public void followById(Long id) {
        followMapper.addFollowById(CurrentUserUtil.getCurrentUserId(),id);
    }

    @Override
    public void unFollowById(Long id) {
        followMapper.deleteFollowById(CurrentUserUtil.getCurrentUserId(),id);
    }
}
