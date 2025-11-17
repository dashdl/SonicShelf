/**
 * 歌手管理API
 * 
 * 这个文件提供歌手管理相关的真实API接口调用
 */

import request from '@/utils/request'

/**
 * 获取歌手列表
 * @param {Object} params - 查询参数
 * @param {number} params.page - 页码
 * @param {number} params.pageSize - 每页条数
 * @param {string} params.keyword - 搜索关键词（歌手名）
 * @param {string} params.category - 分类筛选
 * @returns {Promise} 返回歌手列表数据
 */
export const getSingerList = (params) => {
  return request.get('/admin/singers', { params })
}

/**
 * 添加歌手
 * @param {Object} singer - 歌手数据
 * @param {string} singer.name - 歌手名
 * @param {string} singer.category - 分类
 * @param {string} singer.description - 简介
 * @param {File} singer.avatarFile - 头像文件
 * @returns {Promise} 返回添加的歌手数据
 */
export const addSinger = (singer) => {
  const formData = new FormData()
  Object.keys(singer).forEach(key => {
    formData.append(key, singer[key])
  })
  
  return request.post('/admin/singers', formData, {
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

/**
 * 更新歌手
 * @param {Object} singer - 歌手数据
 * @param {number} singer.id - 歌手ID
 * @returns {Promise} 返回更新的歌手数据
 */
export const updateSinger = (singer) => {
  return request.put(`/admin/singers/${singer.id}`, singer)
}

/**
 * 删除歌手
 * @param {number} id - 歌手ID
 * @returns {Promise} 返回删除结果
 */
export const deleteSinger = (id) => {
  return request.delete(`/admin/singers/${id}`)
}

/**
 * 获取歌手详情
 * @param {number} id - 歌手ID
 * @returns {Promise} 返回歌手详情数据
 */
export const getSingerDetail = (id) => {
  return request.get(`/admin/singers/${id}`)
}

/**
 * 获取歌手的音乐列表
 * @param {number} singerId - 歌手ID
 * @param {Object} params - 查询参数
 * @returns {Promise} 返回音乐列表数据
 */
export const getSingerMusicList = (singerId, params = {}) => {
  return request.get(`/admin/singers/${singerId}/music`, { params })
}

/**
 * 获取歌手的专辑列表
 * @param {number} singerId - 歌手ID
 * @param {Object} params - 查询参数
 * @returns {Promise} 返回专辑列表数据
 */
export const getSingerAlbumList = (singerId, params = {}) => {
  return request.get(`/admin/singers/${singerId}/albums`, { params })
}