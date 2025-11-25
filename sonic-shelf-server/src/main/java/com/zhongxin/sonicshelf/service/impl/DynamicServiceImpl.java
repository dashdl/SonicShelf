package com.zhongxin.sonicshelf.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zhongxin.sonicshelf.dto.request.DynamicRequest;
import com.zhongxin.sonicshelf.dto.response.DynamicResponse;
import com.zhongxin.sonicshelf.exception.CustomException;
import com.zhongxin.sonicshelf.mapper.*;
import com.zhongxin.sonicshelf.service.DynamicService;
import com.zhongxin.sonicshelf.service.LikeService;
import com.zhongxin.sonicshelf.util.CurrentUserUtil;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DynamicServiceImpl implements DynamicService {

    @Resource
    private DynamicMapper dynamicMapper;
    @Resource
    private FollowMapper followMapper;
    @Resource
    private MusicMapper musicMapper;
    @Resource
    private PlaylistMapper playlistMapper;
    @Resource
    private LikeService likeService;

    @Override
    public PageInfo<DynamicResponse> getAllDynamicByUserId(Long currentUserId, Integer pageNum, Integer pageSize) {

        Long[] ids = followMapper.selectFollowingIdByUserId(currentUserId);

        PageHelper.startPage(pageNum, pageSize);
        return PageInfo.of(getDynamicInfo(dynamicMapper.selectAllByUserId(ids, currentUserId)));

    }

    @Override
    public void addDynamic(DynamicRequest dynamic) {
        dynamicMapper.insertDynamic(dynamic, CurrentUserUtil.getCurrentUserId());
    }

    @Override
    public PageInfo<DynamicResponse> getDynamicByUserId(Long userId, Integer pageNum, Integer pageSize) {

        PageHelper.startPage(pageNum, pageSize);

        return PageInfo.of(getDynamicInfo(dynamicMapper.selectByUserId(userId)));
    }

    @Override
    public void deleteDynamic(Long id) {

        if (CurrentUserUtil.getCurrentUserId().equals(dynamicMapper.getUserIdById(id))) {
            dynamicMapper.deleteDynamicById(id);
            dynamicMapper.deleteDynamicImageById(id);
        }else {
            throw new CustomException("1003","请勿试图删除他人动态");
        }
    }

    private List<DynamicResponse> getDynamicInfo(List<DynamicResponse> dynamicResponseList) {

        if (dynamicResponseList != null && !dynamicResponseList.isEmpty()) {
            DynamicResponse temp;
            for (DynamicResponse dynamicResponse : dynamicResponseList) {
                dynamicResponse.setImages(dynamicMapper.selectImagesByDynamicId(dynamicResponse.getId()));
                if (likeService.findByTargetIdAndTargetType("dynamic", dynamicResponse.getId()) != null)
                    dynamicResponse.setLike(true);
                if (dynamicResponse.getType().equals("music")) {
                    temp = musicMapper.selectMusicInfoById(dynamicResponse.getTargetId());
                    dynamicResponse.setCoverImage(temp.getCoverImage());
                    dynamicResponse.setTitle(temp.getTitle());
                    dynamicResponse.setArtistName(temp.getArtistName());
                    continue;
                }
                if (dynamicResponse.getType().equals("playlist")) {
                    temp = playlistMapper.selectPlaylistInfoById(dynamicResponse.getTargetId());
                    dynamicResponse.setCoverImage(temp.getCoverImage());
                    dynamicResponse.setTitle(temp.getTitle());
                    dynamicResponse.setCreatorName(temp.getCreatorName());
                }
            }
        }

        return dynamicResponseList;
    }
}
