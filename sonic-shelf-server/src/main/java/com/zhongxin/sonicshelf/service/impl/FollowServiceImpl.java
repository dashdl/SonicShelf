package com.zhongxin.sonicshelf.service.impl;

import com.zhongxin.sonicshelf.mapper.FollowMapper;
import com.zhongxin.sonicshelf.mapper.UserMapper;
import com.zhongxin.sonicshelf.service.FollowService;
import com.zhongxin.sonicshelf.util.CurrentUserUtil;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FollowServiceImpl implements FollowService {

    @Resource
    private FollowMapper followMapper;
    @Autowired
    private UserMapper userMapper;

    @Override
    public void followById(Long id) {
        followMapper.addFollowById(CurrentUserUtil.getCurrentUserId(),id);
        updateFollowCount(id);
        updateFollowCount(CurrentUserUtil.getCurrentUserId());
    }

    @Override
    public void unFollowById(Long id) {
        followMapper.deleteFollowById(CurrentUserUtil.getCurrentUserId(),id);
        updateFollowCount(id);
        updateFollowCount(CurrentUserUtil.getCurrentUserId());
    }

    private void updateFollowCount(Long id){
        userMapper.updateFollowingCount(followMapper.countFollowingCount(id),id);
        userMapper.updateFollowerCount(followMapper.countFollowerCount(id),id);
    }
}
