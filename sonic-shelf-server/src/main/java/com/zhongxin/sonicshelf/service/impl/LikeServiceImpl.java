package com.zhongxin.sonicshelf.service.impl;

import com.zhongxin.sonicshelf.mapper.LikeMapper;
import com.zhongxin.sonicshelf.service.LikeService;
import com.zhongxin.sonicshelf.util.CurrentUserUtil;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class LikeServiceImpl implements LikeService {

    @Resource
    private LikeMapper likeMapper;

    @Override
    public void like(String targetType, Long targetId) {
        likeMapper.like(targetType,targetId, CurrentUserUtil.getCurrentUserId());
    }

    @Override
    public void unLike(String targetType, Long targetId) {
        likeMapper.unLike(targetType,targetId, CurrentUserUtil.getCurrentUserId());
    }

    @Override
    public Long findByTargetIdAndTargetType(String targetType, Long targetId) {//targetId playlistId
        return likeMapper.selectLike(targetType,targetId,CurrentUserUtil.getCurrentUserId());
    }
}
