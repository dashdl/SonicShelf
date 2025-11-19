/**
 * 模拟数据服务
 * 
 * 这个文件提供了所有管理后台的模拟数据接口
 * 要替换为真实API，请按照以下步骤操作：
 * 
 * 1. 创建对应的API文件（如：src/api/user.js, src/api/album.js等）
 * 2. 使用真实API替换这里的mockService调用
 * 3. 保持相同的返回格式：{ code: '200', data: {...} }
 * 
 * 示例真实API调用方式：
 * import request from '@/utils/request'
 * 
 * export const getUserList = (params) => {
 *   return request.get('/admin/users', { params })
 * }
 */

import mockUsers from './user';
import mockAlbums from './album';
import mockSingers from './singer';
import mockMusic from './music';
import mockCategories from './category';
import mockPlaylists from './playlist';

// 模拟延迟
const delay = (ms) => new Promise(resolve => setTimeout(resolve, ms));

// 分页工具函数
const paginate = (array, page, pageSize) => {
  const start = (page - 1) * pageSize;
  const end = start + pageSize;
  return array.slice(start, end);
};

// 搜索过滤工具函数
const filterByKeyword = (array, keyword, fields = ['name']) => {
  if (!keyword) return array;
  const lowerKeyword = keyword.toLowerCase();
  return array.filter(item => 
    fields.some(field => 
      item[field]?.toLowerCase().includes(lowerKeyword)
    )
  );
};

// 状态过滤工具函数
const filterByStatus = (array, status) => {
  if (!status) return array;
  return array.filter(item => item.status === status);
};

/**
 * 模拟API服务
 * 
 * 注意：所有API方法都返回Promise，保持异步调用的一致性
 * 真实API替换时，也要确保返回Promise对象
 * 
 * 返回格式约定：
 * {
 *   code: '200',     // 状态码，200表示成功
 *   data: {...},     // 响应数据
 *   message: ''      // 错误信息（可选）
 * }
 */
const mockService = {
  // 用户管理API
  user: {
    /**
     * 获取用户列表
     * @param {Object} params - 查询参数
     * @param {number} params.pageNum - 页码
     * @param {number} params.pageSize - 每页条数
     * @param {string} params.keyword - 搜索关键词
     * @param {string} params.status - 状态筛选
     * @returns {Promise} 返回用户列表数据
     * 
     * 真实API替换示例：
     * getList: (params) => request.get('/admin/users', { params })
     */
    async getList(params = {}) {
      await delay(300);
      let filteredUsers = [...mockUsers];
      
      // 搜索过滤
      if (params.keyword) {
        filteredUsers = filterByKeyword(filteredUsers, params.keyword, ['username', 'email', 'nickname']);
      }
      
      // 状态过滤
      if (params.status) {
        filteredUsers = filterByStatus(filteredUsers, params.status);
      }
      
      // 分页
      const page = params.pageNum || 1;
      const pageSize = params.pageSize || 20;
      const paginatedUsers = paginate(filteredUsers, page, pageSize);
      
      return {
        code: '200',
        data: {
          list: paginatedUsers,
          total: filteredUsers.length
        }
      };
    },
    
    /**
     * 添加用户
     * @param {Object} user - 用户数据
     * @param {string} user.username - 用户名
     * @param {string} user.password - 密码
     * @param {string} user.email - 邮箱
     * @param {string} user.nickname - 昵称
     * @param {string} user.status - 状态
     * @returns {Promise} 返回添加的用户数据
     * 
     * 真实API替换示例：
     * add: (user) => request.post('/admin/users', user)
     */
    async add(user) {
      await delay(300);
      const newUser = {
        ...user,
        id: mockUsers.length + 1,
        createTime: new Date().toISOString().replace('T', ' ').substring(0, 19),
        lastLoginTime: null
      };
      mockUsers.push(newUser);
      return {
        code: '200',
        data: newUser
      };
    },
    
    /**
     * 更新用户
     * @param {Object} user - 用户数据
     * @param {number} user.id - 用户ID
     * @returns {Promise} 返回更新的用户数据
     * 
     * 真实API替换示例：
     * update: (user) => request.put(`/admin/users/${user.id}`, user)
     */
    async update(user) {
      await delay(300);
      const index = mockUsers.findIndex(u => u.id === user.id);
      if (index !== -1) {
        mockUsers[index] = { ...mockUsers[index], ...user };
        return {
          code: '200',
          data: mockUsers[index]
        };
      }
      return {
        code: '500',
        message: '用户不存在'
      };
    },
    
    /**
     * 删除用户
     * @param {number} id - 用户ID
     * @returns {Promise} 返回删除结果
     * 
     * 真实API替换示例：
     * delete: (id) => request.delete(`/admin/users/${id}`)
     */
    async delete(id) {
      await delay(300);
      const index = mockUsers.findIndex(u => u.id === id);
      if (index !== -1) {
        mockUsers.splice(index, 1);
        return {
          code: '200'
        };
      }
      return {
        code: '500',
        message: '用户不存在'
      };
    }
  },
  
  // 专辑管理API
  album: {
    // 获取专辑列表
    async getList(params = {}) {
      await delay(300);
      let filteredAlbums = [...mockAlbums];
      
      // 搜索过滤
      if (params.keyword) {
        filteredAlbums = filterByKeyword(filteredAlbums, params.keyword, ['name']);
      }
      
      // 分页
      const page = params.pageNum || 1;
      const pageSize = params.pageSize || 20;
      const paginatedAlbums = paginate(filteredAlbums, page, pageSize);
      
      return {
        code: '200',
        data: {
          list: paginatedAlbums,
          total: filteredAlbums.length
        }
      };
    }
  },
  
  // 歌手管理API
  singer: {
    // 获取歌手列表
    async getList(params = {}) {
      await delay(300);
      let filteredSingers = [...mockSingers];
      
      // 搜索过滤
      if (params.keyword) {
        filteredSingers = filterByKeyword(filteredSingers, params.keyword, ['name']);
      }
      
      // 分页
      const page = params.pageNum || 1;
      const pageSize = params.pageSize || 20;
      const paginatedSingers = paginate(filteredSingers, page, pageSize);
      
      return {
        code: '200',
        data: {
          list: paginatedSingers,
          total: filteredSingers.length
        }
      };
    }
  },
  
  // 音乐管理API
  music: {
    // 获取音乐列表
    async getList(params = {}) {
      await delay(300);
      let filteredMusic = [...mockMusic];
      
      // 搜索过滤
      if (params.keyword) {
        filteredMusic = filterByKeyword(filteredMusic, params.keyword, ['title', 'artistName', 'albumTitle']);
      }
      
      // 专辑过滤
      if (params.albumId) {
        filteredMusic = filteredMusic.filter(item => item.albumId === parseInt(params.albumId));
      }
      
      // 歌手过滤
      if (params.artistId) {
        filteredMusic = filteredMusic.filter(item => item.artistId === parseInt(params.artistId));
      }
      
      // 分页
      const page = params.pageNum || 1;
      const pageSize = params.pageSize || 20;
      const paginatedMusic = paginate(filteredMusic, page, pageSize);
      
      return {
        code: '200',
        data: {
          list: paginatedMusic,
          total: filteredMusic.length
        }
      };
    },
    
    // 添加音乐
    async add(music) {
      await delay(300);
      const singer = mockSingers.find(s => s.id === music.artistId);
      const album = mockAlbums.find(a => a.id === music.albumId);
      
      const newMusic = {
        ...music,
        id: mockMusic.length + 1,
        artistName: singer?.name || '',
        albumTitle: album?.name || '',
        playCount: 0,
        createdAt: new Date().toISOString().replace('T', ' ').substring(0, 19)
      };
      
      mockMusic.push(newMusic);
      return {
        code: '200',
        data: newMusic
      };
    },
    
    // 更新音乐
    async update(music) {
      await delay(300);
      const index = mockMusic.findIndex(m => m.id === music.id);
      if (index !== -1) {
        const singer = mockSingers.find(s => s.id === music.artistId);
        const album = mockAlbums.find(a => a.id === music.albumId);
        
        mockMusic[index] = {
          ...mockMusic[index],
          ...music,
          artistName: singer?.name || mockMusic[index].artistName,
          albumTitle: album?.name || mockMusic[index].albumTitle
        };
        
        return {
          code: '200',
          data: mockMusic[index]
        };
      }
      return {
        code: '500',
        message: '音乐不存在'
      };
    },
    
    // 删除音乐
    async delete(id) {
      await delay(300);
      const index = mockMusic.findIndex(m => m.id === id);
      if (index !== -1) {
        mockMusic.splice(index, 1);
        return {
          code: '200'
        };
      }
      return {
        code: '500',
        message: '音乐不存在'
      };
    }
  },
  
  // 分类管理API
  category: {
    // 获取分类列表
    async getList(params = {}) {
      await delay(300);
      let filteredCategories = [...mockCategories];
      
      // 搜索过滤
      if (params.keyword) {
        filteredCategories = filterByKeyword(filteredCategories, params.keyword, ['name', 'description']);
      }
      
      // 分页
      const page = params.pageNum || 1;
      const pageSize = params.pageSize || 20;
      const paginatedCategories = paginate(filteredCategories, page, pageSize);
      
      return {
        code: '200',
        data: {
          list: paginatedCategories,
          total: filteredCategories.length
        }
      };
    },
    
    // 添加分类
    async add(data) {
      await delay(300);
      const newCategory = {
        id: Date.now().toString(),
        name: data.name,
        description: data.description || '',
        musicCount: 0,
        playlistCount: 0,
        createTime: new Date().toISOString().replace('T', ' ').substring(0, 19)
      };
      mockCategories.push(newCategory);
      return { code: '200', message: '分类添加成功' };
    },
    
    // 更新分类
    async update(data) {
      await delay(300);
      const index = mockCategories.findIndex(item => item.id === data.id);
      if (index !== -1) {
        mockCategories[index] = {
          ...mockCategories[index],
          name: data.name,
          description: data.description || ''
        };
        return { code: '200', message: '分类更新成功' };
      }
      return { code: '500', message: '分类不存在' };
    },
    
    // 删除分类
    async delete(id) {
      await delay(300);
      const index = mockCategories.findIndex(item => item.id === id);
      if (index !== -1) {
        mockCategories.splice(index, 1);
        return { code: '200', message: '分类删除成功' };
      }
      return { code: '500', message: '分类不存在' };
    }
  },
  
  // 歌单管理API
  playlist: {
    // 获取歌单列表
    async getList(params = {}) {
      await delay(300);
      let filteredPlaylists = [...mockPlaylists];
      
      // 搜索过滤
      if (params.keyword) {
        filteredPlaylists = filterByKeyword(filteredPlaylists, params.keyword, ['name', 'description']);
      }
      
      // 分类过滤
      if (params.categoryId) {
        filteredPlaylists = filteredPlaylists.filter(item => item.categoryId === params.categoryId);
      }
      
      // 状态过滤
      if (params.status) {
        filteredPlaylists = filteredPlaylists.filter(item => item.status === params.status);
      }
      
      // 分页
      const page = params.pageNum || 1;
      const pageSize = params.pageSize || 20;
      const paginatedPlaylists = paginate(filteredPlaylists, page, pageSize);
      
      return {
        code: '200',
        data: {
          list: paginatedPlaylists,
          total: filteredPlaylists.length
        }
      };
    },
    
    // 添加歌单
    async add(data) {
      await delay(300);
      // 获取分类名称
      const category = mockCategories.find(c => c.id === data.categoryId);
      const newPlaylist = {
        id: Date.now().toString(),
        name: data.name,
        categoryId: data.categoryId,
        categoryName: category ? category.name : '',
        creatorId: '1',
        creatorName: '管理员',
        description: data.description || '',
        musicCount: 0,
        playCount: 0,
        status: data.status || 'public',
        coverUrl: data.coverUrl || '',
        createTime: new Date().toISOString().replace('T', ' ').substring(0, 19)
      };
      mockPlaylists.push(newPlaylist);
      return { code: '200', message: '歌单添加成功' };
    },
    
    // 更新歌单
    async update(data) {
      await delay(300);
      const index = mockPlaylists.findIndex(item => item.id === data.id);
      if (index !== -1) {
        // 获取分类名称
        const category = mockCategories.find(c => c.id === data.categoryId);
        mockPlaylists[index] = {
          ...mockPlaylists[index],
          name: data.name,
          categoryId: data.categoryId,
          categoryName: category ? category.name : '',
          description: data.description || '',
          status: data.status || 'public',
          coverUrl: data.coverUrl || ''
        };
        return { code: '200', message: '歌单更新成功' };
      }
      return { code: '500', message: '歌单不存在' };
    },
    
    // 删除歌单
    async delete(id) {
      await delay(300);
      const index = mockPlaylists.findIndex(item => item.id === id);
      if (index !== -1) {
        mockPlaylists.splice(index, 1);
        return { code: '200', message: '歌单删除成功' };
      }
      return { code: '500', message: '歌单不存在' };
    }
  }
};

export default mockService;