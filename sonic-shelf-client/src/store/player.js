// stores/playerStore.js
import {defineStore} from 'pinia'
import request from "@/utils/request.js";

export const usePlayerStore = defineStore('player', {
    state: () => {
        return {
            audio: null,           // 音频实例
            currentPlaylist: [],   // 当前播放列表
            currentIndex: 0,       // 当前播放索引
            isPlaying: false,      // 播放状态
            currentTime: 0,        // 当前播放时间
            volume: 0.5,           // 当前音量（默认50%）
            duration: 0,           // 总时长
            currentTitle: '',
            currentName: '',
        }
    },

    actions: {
        // 初始化音频播放器
        initAudio() {
            if (!this.audio) {
                this.audio = new Audio()
                this.setupAudioEvents()
            }
            try {
                let playlistData = localStorage.getItem("playlist");
                if (playlistData) {
                    this.currentPlaylist = JSON.parse(playlistData);
                }
            } catch(e) {
                console.error('解析播放列表失败:', e);
                this.currentPlaylist = [];
            }

            try {
                let now = 0;
                let nowPlayingData = localStorage.getItem("nowPlaying");
                if (nowPlayingData) {
                    now = JSON.parse(nowPlayingData);
                }
                
                if (this.currentPlaylist.length > 0 && now >= 0 && now < this.currentPlaylist.length) {
                    this.audio.src = 'http://localhost:8080' + this.currentPlaylist[now].fileUrl;
                    this.currentIndex = now;
                    this.currentTitle = this.currentPlaylist[now].title || '未知歌曲';
                    this.currentName = this.currentPlaylist[now].artistName || '未知艺术家';
                }
            } catch(e) {
                console.error('设置当前播放歌曲失败:', e);
            }
            
            const savedVolume = localStorage.getItem('playerVolume');
            this.volume = savedVolume ? parseFloat(savedVolume) : 0.5;
            if (this.audio) {
                this.audio.volume = this.volume;
            }
            
            return this.audio;
        },

        setupAudioEvents() {
            if (!this.audio) return;
            
            this.audio.addEventListener('loadedmetadata', () => {
                this.duration = this.audio.duration;
            });
            
            this.audio.addEventListener('timeupdate', () => {
                this.currentTime = this.audio.currentTime;
            });
            
            this.audio.addEventListener('ended', () => {
                this.next();
            });
            
            this.audio.addEventListener('volumechange', () => {
                if (Math.abs(this.audio.volume - this.volume) > 0.01) {
                    this.volume = this.audio.volume;
                    localStorage.setItem('playerVolume', this.volume.toString());
                }
            });
            
            this.audio.addEventListener('error', (error) => {
                console.error('音频播放错误:', error);
                this.isPlaying = false;
            });
        },

        updatePlaylist(newPlaylist) {
            this.currentPlaylist = newPlaylist
            this.currentIndex = 0 // 重置到第一首

            if (newPlaylist.length > 0) {
                this.currentTitle = newPlaylist[0].title || '未知歌曲';
                this.currentName = newPlaylist[0].artistName || '未知艺术家';
            }
        },

        clearPlaylist() {
            this.currentPlaylist = [];
            this.currentIndex = 0;
            this.isPlaying = false;
            this.currentTime = 0;
            this.duration = 0;
            this.currentTitle = '';
            this.currentName = '';
            if (this.audio) {
                this.audio.pause();
                this.audio.src = '';
            }
            localStorage.setItem('playlist', JSON.stringify(this.currentPlaylist));
            localStorage.removeItem('nowPlaying');
        },


        playSong(index) {
            if (index >= 0 && index < this.currentPlaylist.length && this.audio) {
                try {
                    if (!this.audio.paused) {
                        this.audio.pause();
                    }

                    this.audio.currentTime = 0;

                    this.currentIndex = index;
                    const song = this.currentPlaylist[index];
                    this.currentTitle = song.title || '未知歌曲';
                    this.currentName = song.artistName || '未知艺术家';

                    this.audio.src = 'http://localhost:8080' + (song.fileUrl || '');
                    this.isPlaying = true;

                    this.audio.play().catch(error => {
                        console.warn('播放需要用户交互:', error);
                    });

                    localStorage.setItem("nowPlaying", JSON.stringify(this.currentIndex));
                } catch (error) {
                    console.error('播放歌曲失败:', error);
                    this.isPlaying = false;
                }
            }
        },

        togglePlay() {
            if (this.audio) {
                if (this.isPlaying) {
                    this.audio.pause();
                } else {
                    this.audio.play().catch(error => {
                        console.warn('播放需要用户交互:', error);
                    });
                }
                this.isPlaying = !this.isPlaying;
            }
        },

        next() {
            if (this.currentPlaylist.length > 0) {
                const nextIndex = (this.currentIndex + 1) % this.currentPlaylist.length;
                this.playSong(nextIndex);
            }
        },

        prev() {
            if (this.currentPlaylist.length > 0) {
                const prevIndex = this.currentIndex > 0 ? this.currentIndex - 1 : this.currentPlaylist.length - 1;
                this.playSong(prevIndex);
            }
        },

        setCurrentTime(time) {
            if (this.audio) {
                this.audio.currentTime = time;
            }
        },

        setVolume(volumeLevel) {
            if (this.audio) {
                const normalizedVolume = Math.max(0, Math.min(1, volumeLevel));
                this.volume = normalizedVolume;
                this.audio.volume = normalizedVolume;
                localStorage.setItem('playerVolume', normalizedVolume.toString());
            }
        },

        checkMusicId(id) {
            if (!this.audio) {
                this.initAudio();
            }

            let index = 0;
            for (let item of this.currentPlaylist) {
                if (item.id === id) {
                    this.playSong(index);
                    return;
                }
                index++;
            }

            request.get('/musics/' + id).then(res => {
                if (res.data) {
                    if (this.currentPlaylist.length === 0) {
                        this.currentPlaylist.unshift(res.data);
                        localStorage.setItem("playlist", JSON.stringify(this.currentPlaylist));
                        this.currentIndex = 0;
                        this.playSong(0);
                    } else {
                        this.currentPlaylist.splice(this.currentIndex + 1, 0, res.data);
                        localStorage.setItem("playlist", JSON.stringify(this.currentPlaylist));
                        this.playSong(this.currentIndex + 1);
                    }
                }
            }).catch(error => {
                console.error('获取音乐信息失败:', error);
            });
        }

    }
})