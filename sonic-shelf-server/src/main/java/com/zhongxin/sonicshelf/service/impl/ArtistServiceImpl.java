package com.zhongxin.sonicshelf.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zhongxin.sonicshelf.dto.request.ArtistManageRequest;
import com.zhongxin.sonicshelf.dto.response.AlbumResponse;
import com.zhongxin.sonicshelf.dto.response.ArtistManageResponse;
import com.zhongxin.sonicshelf.dto.response.ArtistResponse;
import com.zhongxin.sonicshelf.exception.CustomException;
import com.zhongxin.sonicshelf.mapper.AlbumMapper;
import com.zhongxin.sonicshelf.mapper.ArtistMapper;
import com.zhongxin.sonicshelf.mapper.MusicMapper;
import com.zhongxin.sonicshelf.service.ArtistService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ArtistServiceImpl implements ArtistService {

    @Resource
    private ArtistMapper artistMapper;
    @Resource
    private AlbumMapper albumMapper;
    @Resource
    private MusicMapper musicMapper;

    @Override
    public ArtistResponse findArtistById(Long id) {
        return artistMapper.selectArtistById(id);
    }

    @Override
    public List<AlbumResponse> findAlbumsByArtistId(Long id) {
        return albumMapper.selectAlbumsByArtistId(id);
    }

    @Override
    public PageInfo<ArtistManageResponse> findArtistsAsPage(Integer pageNum, Integer pageSize, String keyword, Byte gender) {
        PageHelper.startPage(pageNum, pageSize);

        return PageInfo.of(artistMapper.selectArtists(pageNum, pageSize, keyword, gender));
    }

    @Override
    public ArtistManageResponse updateArtist(ArtistManageRequest artist) {

        artistMapper.updateArtist(artist);
        artistMapper.updateMusicCountByArtistId(musicMapper.countByArtistId(artist.getId()), artist.getId());

        return artistMapper.selectArtistByIdReturn(artist.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteArtist(Long id) {
        try {
            musicMapper.deleteByArtistId(id);
            albumMapper.deleteByArtistId(id);
            artistMapper.deleteById(id);

        } catch (Exception e) {
            throw new CustomException("删除歌手失败");
        }
    }

    @Override
    public ArtistManageResponse addArtist(ArtistManageRequest artist) {
        artistMapper.addArtist(artist);
        return artistMapper.selectArtistByIdReturn(artist.getId());
    }
}
