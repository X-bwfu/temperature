<template>
  <div class="main-container">
    <!-- 顶部导航栏 -->
    <header class="navbar">
      <div class="logo">工业测温平台</div>
      <nav class="nav-buttons">
        <el-button type="text" @click="goPage('dashboard')">大屏展示</el-button>
        <el-button type="text" 
        :class="{ active: $route.path === '/simulate' }"
        @click="goPage('simulate')">工业模拟</el-button>
        <el-button type="text" @click="goPage('profile')">个人信息</el-button>
        <el-button type="text" @click="goPage('first')">回到首页</el-button>
      </nav>
    </header>

    
  </div>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'

const router = useRouter()
function goPage(path: string) {
  if (path.startsWith("http")) {
    window.open(path, "_blank");
  } else {
    router.push(`/${path}`);
  }
}
</script>

<style scoped>
.main-container {
  min-height: 100vh;
  background: linear-gradient(135deg, #6CB8FF 0%, #090a0f 100%);
  font-family: "Microsoft YaHei", sans-serif;
}

.content-area {
  padding: 20px;
}

/* 激活状态的按钮样式 */
.nav-buttons .el-button.active {
  color: #ffd86f;
  font-weight: bold;
  position: relative;
}

.nav-buttons .el-button.active::after {
  content: '';
  position: absolute;
  bottom: -5px;
  left: 50%;
  transform: translateX(-50%);
  width: 30px;
  height: 2px;
  background: #ffd86f;
}

/* 导航栏样式保持你原来的 */
.navbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: linear-gradient(90deg, #2c3e50, #3498db);
  color: white;
  padding: 20px 50px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.3);
  position: relative;
  z-index: 10;
}

.logo {
  font-size: 24px;
  font-weight: bold;
}

.nav-buttons .el-button {
  color: #fff;
  font-weight: 500;
  margin-left: 20px;
  font-size: 15px;
  transition: 0.3s;
}

.nav-buttons .el-button:hover {
  color: #ffd86f;
  transform: scale(1.05);
}
.nav-buttons .el-button.active {
  color: #ffd86f;
  font-weight: bold;
  position: relative;
}

.nav-buttons .el-button.active::after {
  content: '';
  position: absolute;
  bottom: -5px;
  left: 50%;
  transform: translateX(-50%);
  width: 30px;
  height: 2px;
  background: #ffd86f;
}
</style>