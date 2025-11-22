package com.zhongxin.sonicshelf.service.impl;

import com.fasterxml.jackson.databind.ser.std.StdKeySerializers;
import com.zhongxin.sonicshelf.mapper.CommentMapper;
import com.zhongxin.sonicshelf.mapper.DynamicMapper;
import com.zhongxin.sonicshelf.mapper.LikeMapper;
import com.zhongxin.sonicshelf.mapper.UserMapper;
import com.zhongxin.sonicshelf.service.LikeService;
import com.zhongxin.sonicshelf.util.CurrentUserUtil;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class LikeServiceImpl implements LikeService {

    @Resource
    private LikeMapper likeMapper;
    @Autowired
    private CommentMapper commentMapper;
    @Resource
    private DynamicMapper dynamicMapper;

    @Override
    public void like(String targetType, Long targetId) {
        likeMapper.like(targetType, targetId, CurrentUserUtil.getCurrentUserId());
        updateLikeCount(targetType, targetId);
    }

    @Override
    public void unLike(String targetType, Long targetId) {
        likeMapper.unLike(targetType, targetId, CurrentUserUtil.getCurrentUserId());
        updateLikeCount(targetType, targetId);
    }

    @Override
    public Long findByTargetIdAndTargetType(String targetType, Long targetId) {//targetId playlistId
        return likeMapper.selectLike(targetType, targetId, CurrentUserUtil.getCurrentUserId());
    }

    private void updateLikeCount(String targetType, Long targetId) {
        if (Objects.equals(targetType, "comment")) {
            commentMapper.updateLikeCount(targetId, likeMapper.countLikeCount(targetType, targetId));
        }
        if (Objects.equals(targetType, "dynamic")) {
            dynamicMapper.updateLikeCount(targetId, likeMapper.countLikeCount(targetType, targetId));
        }
    }
}
