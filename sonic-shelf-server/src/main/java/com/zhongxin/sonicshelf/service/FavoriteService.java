package com.zhongxin.sonicshelf.service;

import com.zhongxin.sonicshelf.dto.response.FavoriteResponse;
import com.zhongxin.sonicshelf.dto.response.PlaylistsResponse;

import java.util.List;
import java.util.Map;

public interface FavoriteService {

    FavoriteResponse addFavorite(String targetType, Long targetId);

    void removeFavoritePlaylist(String targetType, Long playlistId);

    List<PlaylistsResponse> findPlaylists();

//    PageResult<FavoriteItemDto> getUserFavorites(Long userId, String targetType, int page, int limit, String sort);

//    FavoriteStatus checkFavoriteStatus(Long userId, String targetType, Long targetId);

    // 批量检查收藏状态（性能优化）
//    Map<Long, Boolean> batchCheckFavoriteStatus(Long userId, String targetType, List<Long> targetIds);
}
