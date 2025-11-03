// stores/playerStore.js
import {defineStore} from 'pinia'

export const usePlayerStore = defineStore('player', {
    state: () => ({
        audio: null,           // Audio 实例
        currentPlaylist: [
            {
                id: "",
                title: "",
                coverImage: "",
                artistName: "",
                albumTitle: "",
                fileUrl: ""
            }
        ],   // 当前播放列表
        currentIndex: 0,       // 当前播放索引
        isPlaying: false,      // 播放状态
        currentTime: 0,        // 当前播放时间
        duration: 0,           // 总时长
        currentTitle: '',
        currentName: '',
    }),

    actions: {
        // 初始化音频播放器
        initAudio() {
            if (!this.audio) {
                this.audio = new Audio()
                this.setupAudioEvents()
            }
            this.currentPlaylist = JSON.parse(localStorage.getItem("playlist"))
            let now = JSON.parse(localStorage.getItem("nowPlaying"))
            this.audio.src = 'http://localhost:8080' + this.currentPlaylist[now].fileUrl
            this.currentIndex = now;
            this.currentTitle = this.currentPlaylist[now].title
            this.currentName = this.currentPlaylist[now].artistName
            return this.audio
        },

        // 设置音频事件监听
        setupAudioEvents() {
            this.audio.addEventListener('loadedmetadata', () => {
                this.duration = this.audio.duration
            })

            this.audio.addEventListener('timeupdate', () => {
                this.currentTime = this.audio.currentTime

            })

            this.audio.addEventListener('ended', () => {
                this.next()
            })
        },

        // 更新播放列表
        updatePlaylist(newPlaylist) {
            this.currentPlaylist = newPlaylist
            this.currentIndex = 0 // 重置到第一首

            // 如果正在播放，立即切换到新歌单的第一首
            if (this.isPlaying && newPlaylist.length > 0) {
                this.playSong(0)
            }

            this.currentTitle = this.currentPlaylist[0].title
            this.currentName = this.currentPlaylist[0].artistName

        },

        // 播放指定索引的歌曲
        playSong(index) {
            if (index >= 0 && index < this.currentPlaylist.length) {
                this.currentIndex = index
                const song = this.currentPlaylist[index]
                this.audio.src = 'http://localhost:8080' + song.fileUrl
                this.audio.play().then(() => {
                    this.isPlaying = true
                    localStorage.setItem("nowPlaying", JSON.stringify(this.currentIndex))
                    this.currentTitle = this.currentPlaylist[this.currentIndex].title
                    this.currentName = this.currentPlaylist[this.currentIndex].artistName
                }).catch(error => {
                    console.error('播放失败:', error)
                })
            }
        },

        // 播放/暂停
        togglePlay() {
            if (this.audio) {
                if (this.isPlaying) {
                    this.audio.pause()
                } else {
                    this.audio.play()
                }
                this.isPlaying = !this.isPlaying
            }
        },

        // 下一首
        next() {
            const nextIndex = (this.currentIndex + 1) % this.currentPlaylist.length
            this.playSong(nextIndex)
            this.currentTitle = this.currentPlaylist[this.currentIndex].title
            this.currentName = this.currentPlaylist[this.currentIndex].artistName
        },

        // 上一首
        prev() {
            const prevIndex = this.currentIndex > 0 ? this.currentIndex - 1 : this.currentPlaylist.length - 1
            this.playSong(prevIndex)
            this.currentTitle = this.currentPlaylist[this.currentIndex].title
            this.currentName = this.currentPlaylist[this.currentIndex].artistName
        },

        // 设置播放时间
        setCurrentTime(time) {
            if (this.audio) {
                this.audio.currentTime = time
            }
        }
    }
})