package com.zhongxin.sonicshelf.service;

import com.github.pagehelper.PageInfo;
import com.zhongxin.sonicshelf.dto.response.CollectorResponse;
import com.zhongxin.sonicshelf.dto.response.FavoriteResponse;
import com.zhongxin.sonicshelf.dto.response.MusicInfoResponse;
import com.zhongxin.sonicshelf.dto.response.PlaylistsResponse;

import java.util.List;

public interface FavoriteService {

    FavoriteResponse addFavorite(String targetType, Long targetId);

    void removeFavorite(String targetType, Long playlistId);

    List<PlaylistsResponse> findPlaylists(Long id);

    List<CollectorResponse> findCollectorByTargetTypeAndTargetId(String targetType, Long targetId);

    PageInfo<PlaylistsResponse> findAsPage(Integer pageNum, Integer pageSize, Long currentUserId);

    List<Long> findByUserIdAndTargetType(Long currentUserId, String targetType);

//    List<Long> findByIds(List<Long> ids);

//    PageResult<FavoriteItemDto> getUserFavorites(Long userId, String targetType, int page, int limit, String sort);

//    FavoriteStatus checkFavoriteStatus(Long userId, String targetType, Long targetId);

    // 批量检查收藏状态（性能优化）
//    Map<Long, Boolean> batchCheckFavoriteStatus(Long userId, String targetType, List<Long> targetIds);
}
