/**
 * 音乐管理API
 * 
 * 这个文件提供音乐管理相关的真实API接口调用
 * 用于替换 src/mock/mockService.js 中的模拟数据
 */

import request from '@/utils/request'

/**
 * 获取音乐列表
 * @param {Object} params - 查询参数
 * @param {number} params.page - 页码
 * @param {number} params.pageSize - 每页条数
 * @param {string} params.keyword - 搜索关键词（歌曲名、歌手名、专辑名）
 * @param {number} params.albumId - 专辑ID筛选
 * @param {number} params.singerId - 歌手ID筛选
 * @param {string} params.category - 分类筛选
 * @returns {Promise} 返回音乐列表数据
 */
export const getMusicList = (params) => {
  return request.get('/admin/music', { params })
}

/**
 * 添加音乐
 * @param {Object} music - 音乐数据
 * @param {string} music.name - 歌曲名
 * @param {number} music.singerId - 歌手ID
 * @param {number} music.albumId - 专辑ID
 * @param {string} music.category - 分类
 * @param {string} music.lyrics - 歌词
 * @param {File} music.audioFile - 音频文件
 * @param {File} music.coverFile - 封面文件
 * @returns {Promise} 返回添加的音乐数据
 */
export const addMusic = (music) => {
  // 使用 FormData 处理文件上传
  const formData = new FormData()
  Object.keys(music).forEach(key => {
    formData.append(key, music[key])
  })
  
  return request.post('/admin/music', formData, {
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

/**
 * 更新音乐
 * @param {Object} music - 音乐数据
 * @param {number} music.id - 音乐ID
 * @returns {Promise} 返回更新的音乐数据
 */
export const updateMusic = (music) => {
  return request.put(`/admin/music/${music.id}`, music)
}

/**
 * 删除音乐
 * @param {number} id - 音乐ID
 * @returns {Promise} 返回删除结果
 */
export const deleteMusic = (id) => {
  return request.delete(`/admin/music/${id}`)
}

/**
 * 批量删除音乐
 * @param {Array} ids - 音乐ID数组
 * @returns {Promise} 返回批量删除结果
 */
export const batchDeleteMusic = (ids) => {
  return request.delete('/admin/music/batch', { data: { ids } })
}

/**
 * 上传音频文件
 * @param {File} file - 音频文件
 * @returns {Promise} 返回文件上传结果
 */
export const uploadAudioFile = (file) => {
  const formData = new FormData()
  formData.append('file', file)
  
  return request.post('/admin/music/upload-audio', formData, {
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

/**
 * 上传封面图片
 * @param {File} file - 图片文件
 * @returns {Promise} 返回图片上传结果
 */
export const uploadCoverImage = (file) => {
  const formData = new FormData()
  formData.append('file', file)
  
  return request.post('/admin/music/upload-cover', formData, {
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

/**
 * 获取音乐详情
 * @param {number} id - 音乐ID
 * @returns {Promise} 返回音乐详情数据
 */
export const getMusicDetail = (id) => {
  return request.get(`/admin/music/${id}`)
}

/**
 * 更新音乐状态
 * @param {number} id - 音乐ID
 * @param {string} status - 状态（enable/disable）
 * @returns {Promise} 返回更新结果
 */
export const updateMusicStatus = (id, status) => {
  return request.put(`/admin/music/${id}/status`, { status })
}