package com.zhongxin.sonicshelf.service;

import com.github.pagehelper.PageInfo;
import com.zhongxin.sonicshelf.dto.response.*;

import java.util.List;

public interface FavoriteService {

    FavoriteResponse addFavorite(String targetType, Long targetId);

    void removeFavorite(String targetType, Long playlistId);

    List<PlaylistsResponse> findPlaylists(Long id);

    List<CollectorResponse> findCollectorByTargetTypeAndTargetId(String targetType, Long targetId);

    PageInfo<PlaylistsResponse> findAsPage(Integer pageNum, Integer pageSize, Long currentUserId);

    List<Long> findByUserIdAndTargetType(Long currentUserId, String targetType);

    PageInfo<MusicResponse> findMusicsAsPage(Integer pageNum, Integer pageSize);

    List<MusicResponse> findMusics();

    List<AlbumInfoResponse> findAlbums();

    PageInfo<AlbumInfoResponse> findAlbumsAsPage(Integer pageNum, Integer pageSize);

//    List<Long> findByIds(List<Long> ids);

//    PageResult<FavoriteItemDto> getUserFavorites(Long userId, String targetType, int page, int limit, String sort);

//    FavoriteStatus checkFavoriteStatus(Long userId, String targetType, Long targetId);

    // 批量检查收藏状态（性能优化）
//    Map<Long, Boolean> batchCheckFavoriteStatus(Long userId, String targetType, List<Long> targetIds);
}
