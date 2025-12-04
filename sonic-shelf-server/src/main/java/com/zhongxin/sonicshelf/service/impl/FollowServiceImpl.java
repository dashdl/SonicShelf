package com.zhongxin.sonicshelf.service.impl;

import com.zhongxin.sonicshelf.dto.response.FollowingResponse;
import com.zhongxin.sonicshelf.mapper.FavoriteMapper;
import com.zhongxin.sonicshelf.mapper.FollowMapper;
import com.zhongxin.sonicshelf.mapper.UserMapper;
import com.zhongxin.sonicshelf.service.FollowService;
import com.zhongxin.sonicshelf.util.CurrentUserUtil;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class FollowServiceImpl implements FollowService {

    @Resource
    private FollowMapper followMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private FavoriteMapper favoriteMapper;

    @Override
    public void followById(Long id) {
        followMapper.addFollowById(CurrentUserUtil.getCurrentUserId(), id);
        updateFollowCount(id);
        updateFollowCount(CurrentUserUtil.getCurrentUserId());
    }

    @Override
    public void unFollowById(Long id) {
        followMapper.deleteFollowById(CurrentUserUtil.getCurrentUserId(), id);
        updateFollowCount(id);
        updateFollowCount(CurrentUserUtil.getCurrentUserId());
    }

    @Override
    public List<FollowingResponse> findAll(Long currentUserId, String type) {

        if (type.equals("user")) {
            return followMapper.selectFollowingByUserId(currentUserId);
        } else if (type.equals("artist")) {
            return favoriteMapper.selectFollowingArtists(currentUserId);
        }
        List<FollowingResponse> followingResponses;

        followingResponses = favoriteMapper.selectFollowingArtists(currentUserId);
        List<Long> ids = new ArrayList<>();
        for (FollowingResponse followingResponse : followingResponses) {
            if(followingResponse.getUserId()!=null) ids.add(followingResponse.getUserId());
        }
        followingResponses.addAll(followMapper.selectFollowingWithoutArtistByUserId(currentUserId,ids));
        return followingResponses;
    }

    @Override
    public List<FollowingResponse> findAllFollower(Long id) {

        return followMapper.selectFollowerByUserId(id);
    }

    private void updateFollowCount(Long id) {
        userMapper.updateFollowingCount(followMapper.countFollowingCount(id), id);
        userMapper.updateFollowerCount(followMapper.countFollowerCount(id), id);
    }
}
