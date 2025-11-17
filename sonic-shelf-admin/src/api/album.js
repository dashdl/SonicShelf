/**
 * 专辑管理API
 * 
 * 这个文件提供专辑管理相关的真实API接口调用
 */

import request from '@/utils/request'

/**
 * 获取专辑列表
 * @param {Object} params - 查询参数
 * @param {number} params.page - 页码
 * @param {number} params.pageSize - 每页条数
 * @param {string} params.keyword - 搜索关键词（专辑名）
 * @param {number} params.singerId - 歌手ID筛选
 * @returns {Promise} 返回专辑列表数据
 */
export const getAlbumList = (params) => {
  return request.get('/admin/albums', { params })
}

/**
 * 添加专辑
 * @param {Object} album - 专辑数据
 * @param {string} album.name - 专辑名
 * @param {number} album.singerId - 歌手ID
 * @param {string} album.description - 专辑描述
 * @param {string} album.releaseDate - 发行日期
 * @param {File} album.coverFile - 封面文件
 * @returns {Promise} 返回添加的专辑数据
 */
export const addAlbum = (album) => {
  const formData = new FormData()
  Object.keys(album).forEach(key => {
    formData.append(key, album[key])
  })
  
  return request.post('/admin/albums', formData, {
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

/**
 * 更新专辑
 * @param {Object} album - 专辑数据
 * @param {number} album.id - 专辑ID
 * @returns {Promise} 返回更新的专辑数据
 */
export const updateAlbum = (album) => {
  return request.put(`/admin/albums/${album.id}`, album)
}

/**
 * 删除专辑
 * @param {number} id - 专辑ID
 * @returns {Promise} 返回删除结果
 */
export const deleteAlbum = (id) => {
  return request.delete(`/admin/albums/${id}`)
}

/**
 * 获取专辑详情
 * @param {number} id - 专辑ID
 * @returns {Promise} 返回专辑详情数据
 */
export const getAlbumDetail = (id) => {
  return request.get(`/admin/albums/${id}`)
}

/**
 * 获取专辑下的音乐列表
 * @param {number} albumId - 专辑ID
 * @param {Object} params - 查询参数
 * @returns {Promise} 返回音乐列表数据
 */
export const getAlbumMusicList = (albumId, params = {}) => {
  return request.get(`/admin/albums/${albumId}/music`, { params })
}