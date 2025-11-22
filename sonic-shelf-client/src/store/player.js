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
            currentLyric: '',  // 原始歌词文本
            parsedLyrics: [],  // 解析后的歌词数组 [{time, text}]
            currentLyricIndex: -1,
            playTimer: null,
            playStartTime: 0,
            currentPlayDuration: 0,
            isRecorded: false,
            recordId: 0,
        }
    },

    actions: {
        // 初始化音频播放器
        async initAudio() {
            if (!this.audio) {
                this.audio = new Audio()
                this.setupAudioEvents()
            }
            try {
                let playlistData = localStorage.getItem("playlist");
                if (playlistData) {
                    this.currentPlaylist = JSON.parse(playlistData);
                }
            } catch (e) {
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
                    await this.fetchLyric(this.currentPlaylist[now].id);
                }
            } catch (e) {
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
                this.updateCurrentLyric();
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
                this.stopPlayTimer();
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

        updateCurrentLyric() {
            if (this.parsedLyrics.length === 0) return;
            for (let i = this.parsedLyrics.length - 1; i >= 0; i--) {
                if (this.currentTime >= this.parsedLyrics[i].time) {
                    this.currentLyricIndex = i
                    break
                }
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
            this.stopPlayTimer();
        },


        async playSong(index) {
            if (index >= 0 && index < this.currentPlaylist.length && this.audio) {
                try {
                    if (!this.audio.paused) {
                        this.audio.pause();
                    }

                    this.audio.currentTime = 0;
                    this.currentLyricIndex = -1;

                    this.currentIndex = index;
                    const song = this.currentPlaylist[index];
                    this.currentTitle = song.title || '未知歌曲';
                    this.currentName = song.artistName || '未知艺术家';

                    this.audio.src = 'http://localhost:8080' + (song.fileUrl || '');

                    await this.fetchLyric(song.id);

                    this.isPlaying = true;
                    this.audio.play().catch(error => {
                        console.warn('播放需要用户交互:', error);
                    });

                    this.resetPlayTimer();
                    this.startPlayTimer();

                    localStorage.setItem("nowPlaying", JSON.stringify(this.currentIndex));
                } catch (error) {
                    console.error('播放歌曲失败:', error);
                    this.isPlaying = false;
                    this.stopPlayTimer();
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
                if (this.isPlaying) {
                    this.startPlayTimer();
                } else {
                    this.stopPlayTimer();
                }
            }
        },

        next() {
            if (this.currentPlaylist.length > 0) {
                const nextIndex = (this.currentIndex + 1) % this.currentPlaylist.length;
                this.playSong(nextIndex);
            }
            this.resetPlayTimer()
        },

        prev() {
            if (this.currentPlaylist.length > 0) {
                const prevIndex = this.currentIndex > 0 ? this.currentIndex - 1 : this.currentPlaylist.length - 1;
                this.playSong(prevIndex);
            }
            this.resetPlayTimer()
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

        parseLyrics(lyricText) {
            if (!lyricText) return [];

            const lines = lyricText.split('\n');
            const result = [];

            // const timeRegex = /\[(\d{2}):(\d{2})\.(\d{2})\]/;
            const timeRegex = /\[(\d{2}):(\d{2})\.(\d{2,3})\]/;

            for (const line of lines) {
                if (!line.trim()) continue;

                const match = line.match(timeRegex);
                if (!match) continue;

                const [_, minutes, seconds, milliseconds] = match;

                // const time = parseInt(minutes) * 60 + parseInt(seconds) + parseInt(milliseconds) / 100;

                let time;
                if (milliseconds.length === 2) {
                    time = parseInt(minutes) * 60 + parseInt(seconds) + parseInt(milliseconds) / 100;
                } else {
                    time = parseInt(minutes) * 60 + parseInt(seconds) + parseInt(milliseconds) / 1000;
                }

                const text = line.replace(timeRegex, '').trim();

                if (text) {
                    result.push({
                        time: parseFloat(time.toFixed(3)),
                        text: text
                    });
                }
            }

            return result;
        },

        async fetchLyric(songId) {
            try {
                const res = await request.get(`musics/lyrics/` + songId);
                this.currentLyric = res.data || '';
                this.parsedLyrics = this.parseLyrics(res.data || '');
            } catch (error) {
                this.currentLyric = '';
                this.parsedLyrics = [];
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
        },

        addNext(id) {
            if (!this.audio) {
                this.initAudio();
            }

            let index = 0;
            for (let item of this.currentPlaylist) {
                if (item.id === id) {
                    const newArr = [...this.currentPlaylist];
                    const [movedElement] = newArr.splice(index, 1);
                    newArr.splice(this.currentIndex + 1, 0, movedElement);
                    this.currentPlaylist = newArr;
                    localStorage.setItem("playlist", JSON.stringify(this.currentPlaylist));
                    return
                }
                index++;
            }

            request.get('/musics/' + id).then(res => {
                if (res.data) {
                    if (this.currentPlaylist.length === 0) {
                        this.currentPlaylist.unshift(res.data);
                        localStorage.setItem("playlist", JSON.stringify(this.currentPlaylist));
                        this.currentIndex = 0;
                    } else {
                        this.currentPlaylist.splice(this.currentIndex + 1, 0, res.data);
                        localStorage.setItem("playlist", JSON.stringify(this.currentPlaylist));
                    }
                }
            }).catch(error => {
                console.error('获取音乐信息失败:', error);
            });
        },

        recordPlayHistoryWithThreshold() {
            if (this.currentPlayDuration < 60) {
                return;
            }

            const currentMusic = this.currentPlaylist[this.currentIndex];
            if (!currentMusic) return;

            // 调用后端API记录播放历史或更新历史记录
            if (!this.isRecorded) {
                this.recordPlayHistory(currentMusic.id, this.currentPlayDuration);
                this.isRecorded = true;
            } else {
                this.updatePlayHistory(this.currentPlayDuration);
            }

        },


        recordPlayHistory(id, currentPlayDuration) {
            request.post(`play-histories`, {
                musicId: id,
                playDuration: currentPlayDuration
            }).then(res => {
                if (res.code === '200') {
                    this.recordId = res.data;
                }
            })
        },

        updatePlayHistory(currentPlayDuration) {
            request.put('play-histories/update', {
                id: this.recordId,
                playDuration: currentPlayDuration
            })
        },

        startPlayTimer() {
            // 如果是暂停后重新播放，调整playStartTime以保持当前的播放时长
            if (this.currentPlayDuration > 0) {
                this.playStartTime = Date.now() - (this.currentPlayDuration * 1000);
            } else {
                this.playStartTime = Date.now();
            }
            this.playTimer = setInterval(() => {
                this.currentPlayDuration = Math.floor((Date.now() - this.playStartTime) / 1000);

                // 检查是否达到60秒阈值
                if (this.currentPlayDuration >= 60) {
                    this.recordPlayHistoryWithThreshold();
                }

            }, 1000);
        },

// 停止计时器
        // 停止计时器（暂停时使用，不重置时间）
        stopPlayTimer() {
            if (this.playTimer) {
                clearInterval(this.playTimer);
                this.playTimer = null;
            }
        },

// 重置计时器（切换歌曲或清空播放列表时使用）
        resetPlayTimer() {
            this.stopPlayTimer();
            this.currentPlayDuration = 0;
            this.playStartTime = 0;
            this.isRecorded = false;
            this.recordId = 0;
        }
    }
})