// 用户管理模拟数据
const mockUsers = [
  {
    id: 1,
    username: 'admin',
    email: 'admin@example.com',
    nickname: '管理员',
    role: 'admin',
    status: '1',
    createTime: '2024-01-01 00:00:00',
    lastLoginTime: '2024-01-10 10:00:00'
  },
  {
    id: 2,
    username: 'user1',
    email: 'user1@example.com',
    nickname: '用户1',
    role: 'user',
    status: '1',
    createTime: '2024-01-02 10:00:00',
    lastLoginTime: '2024-01-09 15:30:00'
  },
  {
    id: 3,
    username: 'user2',
    email: 'user2@example.com',
    nickname: '用户2',
    role: 'user',
    status: '1',
    createTime: '2024-01-03 14:20:00',
    lastLoginTime: '2024-01-08 09:15:00'
  },
  {
    id: 4,
    username: 'user3',
    email: 'user3@example.com',
    nickname: '用户3',
    role: 'user',
    status: '0',
    createTime: '2024-01-04 09:00:00',
    lastLoginTime: '2024-01-07 18:45:00'
  },
  {
    id: 5,
    username: 'user4',
    email: 'user4@example.com',
    nickname: '用户4',
    role: 'user',
    status: '1',
    createTime: '2024-01-05 16:30:00',
    lastLoginTime: '2024-01-06 12:20:00'
  }
];

// 生成更多模拟数据
for (let i = 6; i <= 50; i++) {
  mockUsers.push({
    id: i,
    username: `user${i}`,
    email: `user${i}@example.com`,
    nickname: `用户${i}`,
    role: 'user',
    status: i % 5 === 0 ? '0' : '1',
    createTime: `2024-01-${(i % 10 + 1).toString().padStart(2, '0')} ${(i % 24).toString().padStart(2, '0')}:00:00`,
    lastLoginTime: `2024-01-${(i % 15 + 5).toString().padStart(2, '0')} ${(i % 24).toString().padStart(2, '0')}:30:00`
  });
}

export default mockUsers;