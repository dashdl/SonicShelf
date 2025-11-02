package com.zhongxin.sonicshelf.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zhongxin.sonicshelf.dto.response.MusicResponse;
import com.zhongxin.sonicshelf.exception.CustomException;
import com.zhongxin.sonicshelf.mapper.MusicMapper;
import com.zhongxin.sonicshelf.service.MusicService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MusicServiceImpl implements MusicService {
    @Resource
    private MusicMapper musicMapper;

    @Override
    public PageInfo<MusicResponse> findAsPageByListId(Integer pageNum, Integer pageSize, Long id) {

        PageHelper.startPage(pageNum, pageSize);

        return PageInfo.of(musicMapper.findAsPageByListId(id));
    }

    @Override
    public List<MusicResponse> findByListId(Long id) {
        return musicMapper.findAsPageByListId(id);
    }

    @Override
    public MusicResponse findById(Long id) {
        try {
            return musicMapper.findById(id);
        }catch (RuntimeException e){
            throw new CustomException("404","音乐不存在或已下架");
        }
    }

}
