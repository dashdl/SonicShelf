// 音乐管理模拟数据
const mockMusic = [
  {
    id: 1,
    name: '歌曲1',
    singerId: 1,
    singerName: '歌手1',
    albumId: 1,
    albumName: '专辑1',
    duration: 240,
    fileUrl: 'https://example.com/music1.mp3',
    coverUrl: 'https://via.placeholder.com/300x300?text=Music1',
    playCount: 1000,
    createTime: '2024-01-01 00:00:00'
  },
  {
    id: 2,
    name: '歌曲2',
    singerId: 1,
    singerName: '歌手1',
    albumId: 1,
    albumName: '专辑1',
    duration: 180,
    fileUrl: 'https://example.com/music2.mp3',
    coverUrl: 'https://via.placeholder.com/300x300?text=Music2',
    playCount: 800,
    createTime: '2024-01-02 00:00:00'
  },
  {
    id: 3,
    name: '歌曲3',
    singerId: 2,
    singerName: '歌手2',
    albumId: 2,
    albumName: '专辑2',
    duration: 300,
    fileUrl: 'https://example.com/music3.mp3',
    coverUrl: 'https://via.placeholder.com/300x300?text=Music3',
    playCount: 1500,
    createTime: '2024-01-03 00:00:00'
  },
  {
    id: 4,
    name: '歌曲4',
    singerId: 2,
    singerName: '歌手2',
    albumId: 2,
    albumName: '专辑2',
    duration: 210,
    fileUrl: 'https://example.com/music4.mp3',
    coverUrl: 'https://via.placeholder.com/300x300?text=Music4',
    playCount: 900,
    createTime: '2024-01-04 00:00:00'
  },
  {
    id: 5,
    name: '歌曲5',
    singerId: 3,
    singerName: '歌手3',
    albumId: 3,
    albumName: '专辑3',
    duration: 270,
    fileUrl: 'https://example.com/music5.mp3',
    coverUrl: 'https://via.placeholder.com/300x300?text=Music5',
    playCount: 1200,
    createTime: '2024-01-05 00:00:00'
  }
];

// 生成更多模拟数据
for (let i = 6; i <= 30; i++) {
  const singerId = (i % 5) + 1;
  const albumId = (i % 5) + 1;
  mockMusic.push({
    id: i,
    name: `歌曲${i}`,
    singerId: singerId,
    singerName: `歌手${singerId}`,
    albumId: albumId,
    albumName: `专辑${albumId}`,
    duration: 180 + (i % 120),
    fileUrl: `https://example.com/music${i}.mp3`,
    coverUrl: `https://via.placeholder.com/300x300?text=Music${i}`,
    playCount: 500 + (i * 100),
    createTime: `2024-01-${(i % 28 + 1).toString().padStart(2, '0')} ${(i % 24).toString().padStart(2, '0')}:00:00`
  });
}

export default mockMusic;