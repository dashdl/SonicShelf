<template>
  <div class="admin-layout">
    <!-- 侧边栏 -->
    <el-aside :width="isCollapse ? '64px' : '220px'" class="admin-sidebar" :class="{ 'sidebar-collapse': isCollapse }">
      <div class="sidebar-logo">
        <h1 v-if="!isCollapse">SonicShelf</h1>
        <div v-else class="logo-collapsed">S</div>
      </div>
      <el-menu
        :default-active="activeMenu"
        class="el-menu-vertical-demo"
        router
        :collapse="isCollapse"
      >
        <el-menu-item index="dashboard">
          <el-icon><Monitor /></el-icon>
          <span>仪表盘</span>
        </el-menu-item>

        <el-menu-item index="user">
          <el-icon><User /></el-icon>
          <span>用户管理</span>
        </el-menu-item>

        <el-menu-item index="music">
          <el-icon><Headset /></el-icon>
          <span>音乐管理</span>
        </el-menu-item>

        <el-menu-item index="album">
          <el-icon><Collection /></el-icon>
          <span>专辑管理</span>
        </el-menu-item>

        <el-menu-item index="singer">
          <el-icon><User /></el-icon>
          <span>歌手管理</span>
        </el-menu-item>

        <el-menu-item index="playlist">
          <el-icon><Collection /></el-icon>
          <span>歌单管理</span>
        </el-menu-item>

        <el-menu-item index="category">
          <el-icon><Setting /></el-icon>
          <span>分类管理</span>
        </el-menu-item>

        <el-menu-item index="comment">
          <el-icon><ChatDotRound /></el-icon>
          <span>评论管理</span>
        </el-menu-item>
      </el-menu>
    </el-aside>

    <!-- 主内容区域 -->
    <div class="admin-main">
      <!-- 顶部导航栏 -->
      <el-header class="admin-header">
        <div class="header-left">
          <el-icon @click="toggleCollapse"><Menu /></el-icon>
        </div>
        <div class="header-right">
          <el-dropdown>
            <span class="el-dropdown-link">
              <el-avatar :size="36">
                <img :src="userStore.userInfo.avatar ? `http://localhost:8080${userStore.userInfo.avatar}` : '/user.svg'" alt="">
              </el-avatar>
              <span>{{ username || '管理员' }}</span>
              <el-icon class="el-icon--right"><ArrowDown /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item divided @click="logout">
                  <el-icon><SwitchButton /></el-icon>
                  退出登录
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>

      <!-- 内容区域 -->
      <el-main class="admin-content">
        <router-view />
      </el-main>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue';
import { useRouter } from 'vue-router';
import { useUserStore } from '@/store/userStore';
import { Monitor, User, Headset, Collection, Setting, Menu, ArrowDown, SwitchButton, ChatDotRound } from '@element-plus/icons-vue';

const router = useRouter();
const userStore = useUserStore();

const activeMenu = computed(() => {
  const path = router.currentRoute.value.path;
  return path.split('/').pop() || 'dashboard';
});

const username = computed(() => userStore.userInfo?.username);

const isCollapse = ref(false);

const toggleCollapse = () => {
  isCollapse.value = !isCollapse.value;
};

const logout = () => {
  userStore.logout();
  router.push('/login');
};
</script>

<style scoped lang="scss">
.admin-layout {
  display: flex;
  height: 100vh;
  overflow: hidden;

  .admin-sidebar {
      background: #ffffff;
      color: #303133;
      transition: all 0.3s ease;
      box-shadow: 2px 0 8px rgba(0, 0, 0, 0.1);
      border-right: 1px solid #e8e8e8;

      &.sidebar-collapse {
        box-shadow: none;
      }

    .sidebar-logo {
      display: flex;
      align-items: center;
      justify-content: center;
      height: 60px;
      font-size: 18px;
      font-weight: bold;
      border-bottom: 1px solid #e8e8e8;
      transition: all 0.3s ease;
      background-color: #ffffff;

      h1 {
        color: #1890ff;
        margin: 0;
        font-size: 20px;
        font-weight: 600;
        letter-spacing: 1px;
      }

      .logo-collapsed {
        width: 32px;
        height: 32px;
        background-color: #1890ff;
        border-radius: 6px;
        display: flex;
        align-items: center;
        justify-content: center;
        font-size: 18px;
        font-weight: bold;
        color: #fff;
      }
    }

    .el-menu {
      background-color: #ffffff;
      border-right: none;
      height: calc(100% - 60px);

      .el-menu-item {
        color: #606266;
        height: 56px;
        line-height: 56px;
        padding: 0 20px;
        transition: all 0.3s ease;
        font-size: 14px;

        &:hover {
          background-color: #f5f7fa;
          color: #1890ff;
        }
      }

      .el-menu-item.is-active {
        background-color: #ecf5ff;
        color: #1890ff;
        border-right: 3px solid #1890ff;
      }

      .el-icon {
        margin-right: 12px;
        font-size: 16px;
        width: 20px;
        text-align: center;
        color: #909399;
      }
    }
  }

  .admin-main {
    display: flex;
    flex-direction: column;
    flex: 1;
    overflow: hidden;
    transition: all 0.3s ease;

    .admin-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      background-color: #fff;
      padding: 0 20px;
      border-bottom: 1px solid #e8e8e8;
      height: 60px;
      box-shadow: 0 2px 4px rgba(0, 0, 0, 0.08);

      .header-left {
        .el-icon {
          height: 100%;
          width: 100%;
          cursor: pointer;
          color: #666;
          transition: all 0.3s ease;
          padding: 8px;
          border-radius: 4px;
          margin-right: 10px;

          &:hover {
            background-color: #f5f7fa;
            color: #1890ff;
          }
        }
      }

      .header-right {
        display: flex;
        align-items: center;

        .el-dropdown-link {
          display: flex;
          align-items: center;
          cursor: pointer;
          color: #666;

          .el-avatar {
            margin-right: 10px;
          }

          .el-icon {
            margin-left: 5px;
          }
        }
      }
    }

    .admin-content {
      flex: 1;
      padding: 20px;
      background-color: #f0f2f5;
      overflow-y: auto;
    }
  }
}
</style>