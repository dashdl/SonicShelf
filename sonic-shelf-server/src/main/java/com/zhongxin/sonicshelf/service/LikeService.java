package com.zhongxin.sonicshelf.service;

public interface LikeService {
    void like(String targetType, Long targetId);

    void unLike(String targetType, Long targetId);

    Long findByTargetIdAndTargetType(String comment, Long targetId);
}
