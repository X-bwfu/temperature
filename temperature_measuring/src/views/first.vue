<template>
  <div class="main-container">
    <!-- 顶部导航栏 -->
    <header class="navbar">
      <div class="logo">工业测温平台</div>
      <nav class="nav-buttons">
        <el-button type="text" @click="goPage('dashboard')">大屏展示</el-button>
        <el-button type="text" @click="goPage('simulate')">工业模拟</el-button>
        <el-button type="text" @click="goPage('profile')">个人信息</el-button>
      </nav>
    </header>

    <!-- 轮播图 -->
    <section class="carousel-section">
      <el-carousel height="450px" indicator-position="outside">
        <el-carousel-item v-for="(item, index) in carouselItems" :key="index">
          <div class="carousel-image" :style="{ backgroundImage: `url(${item.img})` }" @click="goPage(item.link)">
            <div class="carousel-overlay">
              <h2>{{ item.title }}</h2>
              <p>{{ item.desc }}</p>
            </div>
          </div>
        </el-carousel-item>
      </el-carousel>
    </section>

    <!-- 核心数据概览 -->
    <section class="stats-section">
      <div class="stats-container">
        <div class="stat-card">
          <div class="stat-icon temperature">🌡️</div>
          <div class="stat-content">
            <div class="stat-value">98.7%</div>
            <div class="stat-label">测温精度</div>
            <div class="stat-desc">国产换芯方案 vs 进口芯片</div>
          </div>
        </div>
        <div class="stat-card">
          <div class="stat-icon device">📡</div>
          <div class="stat-content">
            <div class="stat-value">{{ deviceStats.onlineCount }}/{{ deviceStats.totalCount }}</div>
            <div class="stat-label">设备在线</div>
            <div class="stat-desc">边缘节点稳定运行</div>
          </div>
        </div>
        <div class="stat-card">
          <div class="stat-icon alert">⚠️</div>
          <div class="stat-content">
            <div class="stat-value">{{ alertStats.alarmCount }}</div>
            <div class="stat-label">异常预警</div>
            <div class="stat-desc">今日检测到异常温度</div>
          </div>
        </div>
        <div class="stat-card">
          <div class="stat-icon prediction">📊</div>
          <div class="stat-content">
            <div class="stat-value">95.2%</div>
            <div class="stat-label">预测准确率</div>
            <div class="stat-desc">趋势分析算法精度</div>
          </div>
        </div>
      </div>
    </section>

    <!-- AI智能分析模块 -->
    <section class="content-section">
      <h2 class="section-title">AI 智能分析中心</h2>
      <div class="card-container">
        <el-card class="ai-card" shadow="hover">
          <div class="card-header">
            <span class="card-icon">🔬</span>
            <h3>端侧换芯进展</h3>
          </div>
          <!-- 加载状态 -->
          <div v-if="aiAnalysisData.endpoint.loading" class="loading-state">
            <el-icon class="is-loading"><Loading /></el-icon>
            <span>分析中...</span>
          </div>
          <!-- 显示后端返回的分析内容 -->
          <div v-else class="analysis-content">
            <p>{{ aiAnalysisData.endpoint.content }}</p>
          </div>
          <div class="card-footer">
            <el-tag type="success">运行正常</el-tag>
            <el-button type="primary" size="small" @click="fetchAIAnalysis('endpoint')">刷新分析</el-button>
          </div>
        </el-card>

        <el-card class="ai-card" shadow="hover">
          <div class="card-header">
            <span class="card-icon">🖥️</span>
            <h3>边缘计算状态</h3>
          </div>
          <div v-if="aiAnalysisData.edge.loading" class="loading-state">
            <el-icon class="is-loading"><Loading /></el-icon>
            <span>分析中...</span>
          </div>
          <div v-else class="analysis-content">
            <p>{{ aiAnalysisData.edge.content }}</p>
          </div>
          <div class="card-footer">
            <el-tag type="success">运行正常</el-tag>
            <el-button type="primary" size="small" @click="fetchAIAnalysis('edge')">刷新分析</el-button>
          </div>
        </el-card>

        <el-card class="ai-card" shadow="hover">
          <div class="card-header">
            <span class="card-icon">☁️</span>
            <h3>云端监测概览</h3>
          </div>
          <div v-if="aiAnalysisData.cloud.loading" class="loading-state">
            <el-icon class="is-loading"><Loading /></el-icon>
            <span>分析中...</span>
          </div>
          <div v-else class="analysis-content">
            <p>{{ aiAnalysisData.cloud.content }}</p>
          </div>
          <div class="card-footer">
            <el-tag type="warning">注意观察</el-tag>
            <el-button type="primary" size="small" @click="fetchAIAnalysis('cloud')">刷新分析</el-button>
          </div>
        </el-card>

        <el-card class="ai-card" shadow="hover">
          <div class="card-header">
            <span class="card-icon">📈</span>
            <h3>温度趋势预测</h3>
          </div>
          <div v-if="aiAnalysisData.trend.loading" class="loading-state">
            <el-icon class="is-loading"><Loading /></el-icon>
            <span>分析中...</span>
          </div>
          <div v-else class="analysis-content">
            <p>{{ aiAnalysisData.trend.content }}</p>
          </div>
          <div class="card-footer">
            <el-tag type="success">状态良好</el-tag>
            <el-button type="primary" size="small" @click="fetchAIAnalysis('trend')">刷新分析</el-button>
          </div>
        </el-card>

        <el-card class="ai-card" shadow="hover">
          <div class="card-header">
            <span class="card-icon">🔔</span>
            <h3>预警与告警</h3>
          </div>
          <div v-if="aiAnalysisData.alert.loading" class="loading-state">
            <el-icon class="is-loading"><Loading /></el-icon>
            <span>分析中...</span>
          </div>
          <div v-else class="analysis-content">
            <p>{{ aiAnalysisData.alert.content }}</p>
          </div>
          <div class="card-footer">
            <el-button type="primary" size="small" @click="fetchAIAnalysis('alert')">刷新分析</el-button>
            <el-button type="primary" size="small" @click="handleAlerts">处理告警</el-button>
          </div>
        </el-card>
      </div>
    </section>
  </div>
</template>

<script setup lang="ts">
import { useRouter } from "vue-router";
import { ref, onMounted } from "vue";
import axios from 'axios';
import { ElMessageBox } from 'element-plus'
type PanelKey = 'endpoint' | 'edge' | 'cloud' | 'trend' | 'alert'

interface PanelItem {
  content: string
  loading: boolean
}

const aiAnalysisData = ref<Record<PanelKey, PanelItem>>({
  endpoint: { content: '加载中...', loading: false },
  edge: { content: '加载中...', loading: false },
  cloud: { content: '加载中...', loading: false },
  trend: { content: '加载中...', loading: false },
  alert: { content: '加载中...', loading: false }
})

const router = useRouter();

// 添加设备统计数据和告警数据的响应式变量
const deviceStats = ref({
  onlineCount: 0,
  offlineCount: 0,
  totalCount: 0
});

const alertStats = ref({
  alarmCount: 0,
  normalCount: 0,
  totalCount: 0
});





const loading = ref(false);
const localImages = [
  new URL('./1.png', import.meta.url).href,
  new URL('./2.jpg', import.meta.url).href,
  new URL('./3.jpg', import.meta.url).href,
  new URL('./4.jpg', import.meta.url).href,
  new URL('./5.jpg', import.meta.url).href,
];

interface CarouselItem {
  img: string;
  title: string;
  desc: string;
  link: string;
}

const carouselItems = ref<CarouselItem[]>([]);

// 获取设备状态统计
const fetchDeviceStatusSummary = async () => {
  try {
    const response = await axios.get('http://127.0.0.1:8080/api/device/summary');
    deviceStats.value = response.data;
    console.log('设备状态统计:', response.data);
  } catch (error) {
    console.error('获取设备状态统计失败:', error);
    // 使用默认值
    deviceStats.value = {
      onlineCount: 0,
      offlineCount: 0,
      totalCount: 0
    };
  }
};

// 获取传感器告警统计
const fetchSensorStatusSummary = async () => {
  try {
    const response = await axios.get('http://127.0.0.1:8080/api/sensor-status-summary');
    alertStats.value = {
      alarmCount: response.data.alarmCount,
      normalCount: response.data.normalCount,
      totalCount: response.data.alarmCount + response.data.normalCount
    };
    console.log('传感器告警统计:', response.data);
  } catch (error) {
    console.error('获取传感器告警统计失败:', error);
    // 使用默认值
    alertStats.value = {
      alarmCount: 0,
      normalCount: 0,
      totalCount: 0
    };
  }
};

// 获取设备最新数据的方法
const fetchLatestSensorData = async () => {
  try {
    const response = await axios.get('http://127.0.0.1:8080/api/sensor-latest');
    console.log('设备最新数据:', response.data);
    
    // 详细打印每个设备的信息
    response.data.forEach((device: any, index: number) => {
      console.log(`设备 ${index + 1}:`, {
        设备ID: device.deviceId,
        传感器ID: device.sensorId,
        传感器名称: device.sensorName,
        温度: device.temperature,
        湿度: device.humidity,
        压力: device.pressure,
        预测温度: device.predictedTemperature,
        是否告警: device.alarmTriggered,
        告警信息: device.alarmMessage,
        时间戳: device.timestamp
      });
    });
    
    // 统计信息
    console.log('数据统计:', {
      设备总数: response.data.length,
      告警设备数: response.data.filter((d: any) => d.alarmTriggered).length,
      正常设备数: response.data.filter((d: any) => !d.alarmTriggered).length
    });
    
  } catch (error) {
    console.error('获取设备最新数据失败:', error);
  }
};

// 获取所有统计数据的函数
const fetchAllStatistics = async () => {
  await Promise.all([
    fetchDeviceStatusSummary(),
    fetchSensorStatusSummary(),
    fetchLatestSensorData()
  ]);
};

// 获取AI分析数据的函数
const fetchAIAnalysis = async (module: PanelKey) => {
  aiAnalysisData.value[module].loading = true;
  try {
    const response = await axios.get(`http://127.0.0.1:8080/api/ai/analyzeTemperature/${module}`);
    aiAnalysisData.value[module].content = response.data;
  } catch (error) {
    console.error(`获取${module}模块分析失败:`, error);
    aiAnalysisData.value[module].content = '数据加载失败，请稍后重试';
  } finally {
    aiAnalysisData.value[module].loading = false;
  }
};

// 获取所有AI分析数据
const fetchAllAIAnalysis = async () => {
  const modules: PanelKey[] = ['endpoint', 'edge', 'cloud', 'trend', 'alert'];
  await Promise.all(modules.map(module => fetchAIAnalysis(module)));
};

// 处理告警按钮点击
const handleAlerts = () => {
  // 这里可以跳转到告警处理页面或显示详情
  ElMessageBox.alert("正在处理预警")
}

function goPage(path: string) {
  if (path.startsWith("http")) {
    window.open(path, "_blank");
  } else {
    router.push(`/${path}`);
  }
}

onMounted(async () => {
  try {
    const res = await fetch("http://127.0.0.1:5000/api/news");
    const data = await res.json();
    
    carouselItems.value = data.articles.slice(0, 5).map((a: any, index: number) => ({
      img: localImages[index] || localImages[0],
      title: a.title,
      desc: "点击查看详情",
      link: a.link,
    }));
   
  } catch (err) {
    console.error("获取新闻失败：", err);
    carouselItems.value = [
      {
        img: "https://images.unsplash.com/photo-1581090700227-1e37b190418e",
        title: "智慧工业测温技术",
        desc: "了解工业温度监控的最新进展。",
        link: "https://baike.baidu.com/item/工业测温",
      },
    ];
  }
  
  // 获取所有统计数据
  await fetchAllStatistics();
  
  // 获取AI分析数据
  await fetchAllAIAnalysis();
});
</script>

<style scoped>
/* 整体布局与背景 */
.main-container {
  font-family: "Microsoft YaHei", sans-serif;
  background: linear-gradient(135deg, #2D8CFF 0%, #090a0f 100%);
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  color: #ecf0f1;
}

/* 顶部导航栏 */
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

/* 轮播图 */
.carousel-section {
  width: 90%;
  margin: 30px auto;
  border-radius: 20px;
  overflow: hidden;
  box-shadow: 0 15px 50px rgba(0, 0, 0, 0.15);
  backdrop-filter: blur(8px);
  transition: transform 0.5s ease;
}

.carousel-section:hover {
  transform: scale(1.02);
}

.carousel-image {
  width: 100%;
  height: 450px;
  background-size: cover;
  background-position: center;
  position: relative;
  cursor: pointer;
}

.carousel-overlay {
  position: absolute;
  bottom: 0;
  width: 100%;
  background: linear-gradient(180deg, rgba(0,0,0,0) 0%, rgba(0,0,0,0.8) 100%);
  color: #f1f1f1;
  padding: 40px;
  transition: background 0.3s ease;
}

.carousel-overlay h2 {
  font-size: 28px;
  margin-bottom: 10px;
}

.carousel-overlay p {
  font-size: 16px;
  opacity: 0.9;
}

/* 数据统计区域 */
.stats-section {
  width: 90%;
  margin: 30px auto;
}

.stats-container {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(240px, 1fr));
  gap: 20px;
}

.stat-card {
  background: linear-gradient(135deg, rgba(52,152,219,0.15), rgba(155,89,182,0.15));
  border-radius: 20px;
  padding: 25px 30px;
  display: flex;
  align-items: center;
  box-shadow: 0 8px 25px rgba(0,0,0,0.3);
  border-left: 6px solid #3498db;
  transition: all 0.5s ease;
  position: relative;
  overflow: hidden;
}

.stat-card::after {
  content: '';
  position: absolute;
  width: 200%;
  height: 200%;
  top: -50%;
  left: -50%;
  background: radial-gradient(circle, rgba(255,255,255,0.1) 0%, transparent 70%);
  animation: rotate 6s linear infinite;
  z-index: 0;
}

@keyframes rotate {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.stat-card:hover {
  transform: translateY(-10px) scale(1.03) rotateZ(1deg);
  box-shadow: 0 15px 50px rgba(0,0,0,0.5);
}

.stat-icon {
  font-size: 48px;
  margin-right: 20px;
  animation: pulse 2s infinite;
  z-index: 1;
}

@keyframes pulse {
  0%, 100% { transform: scale(1); }
  50% { transform: scale(1.15); }
}

.stat-value {
  font-size: 32px;
  font-weight: bold;
  color: #f39c12;
  z-index: 1;
}

.stat-label {
  font-size: 16px;
  color: #ecf0f1;
  margin-bottom: 5px;
  z-index: 1;
}

.stat-desc {
  font-size: 12px;
  color: #bdc3c7;
  z-index: 1;
}

/* AI分析内容区 */
.content-section {
  width: 90%;
  margin: 40px auto 60px;
}

.section-title {
  font-size: 28px;
  font-weight: bold;
  margin-bottom: 30px;
  color: #ecf0f1;
  text-align: center;
  position: relative;
}

.section-title::after {
  content: '';
  position: absolute;
  bottom: -10px;
  left: 50%;
  transform: translateX(-50%);
  width: 60px;
  height: 3px;
  background: linear-gradient(90deg, #3498db, #9b59b6);
  border-radius: 2px;
}

.card-container {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(350px, 1fr));
  gap: 25px;
}

.ai-card {
  border-radius: 25px;
  transition: all 0.5s ease;
  border: none;
  background: linear-gradient(135deg, rgba(44,62,80,0.9), rgba(52,152,219,0.2));
  backdrop-filter: blur(6px);
  color: #ecf0f1;
  position: relative;
  overflow: hidden;
}

.ai-card::before {
  content: '';
  position: absolute;
  width: 120%;
  height: 120%;
  top: -10%;
  left: -10%;
  background: radial-gradient(circle, rgba(255,255,255,0.05) 0%, transparent 70%);
  animation: float 10s linear infinite;
  z-index: 0;
}

@keyframes float {
  0% { transform: translateY(0) rotate(0deg); }
  100% { transform: translateY(-10px) rotate(360deg); }
}

.ai-card:hover {
  transform: translateY(-12px) scale(1.02) rotateX(2deg);
  box-shadow: 0 20px 60px rgba(0,0,0,0.6);
}

.card-header {
  display: flex;
  align-items: center;
  margin-bottom: 15px;
  z-index: 1;
}

.card-icon {
  font-size: 28px;
  margin-right: 12px;
  animation: icon-bounce 2s infinite;
}

@keyframes icon-bounce {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-5px); }
}

.card-header h3 {
  color: #ecf0f1;
  font-size: 18px;
  margin: 0;
  z-index: 1;
}

.ai-card p {
  color: #dcdde1;
  font-size: 14px;
  line-height: 1.6;
  margin-bottom: 20px;
  z-index: 1;
}

.card-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 15px;
  z-index: 1;
}

/* 响应式设计 */
@media (max-width: 1024px) {
  .stats-container {
    grid-template-columns: repeat(2, 1fr);
  }
  .card-container {
    grid-template-columns: 1fr;
  }
  .carousel-section {
    width: 95%;
  }
}

@media (max-width: 480px) {
  .navbar {
    padding: 15px 20px;
  }
  .stat-card {
    flex-direction: column;
    align-items: flex-start;
  }
  .stat-icon {
    margin-bottom: 10px;
  }
}

/* 加载状态样式 */
.loading-state {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px;
  color: #bdc3c7;
}

.loading-state .el-icon {
  margin-right: 8px;
}

.analysis-content {
  min-height: 60px;
  line-height: 1.6;
}
</style>