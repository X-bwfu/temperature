<template>
  <div class="dashboard">
    <!-- 网格背景 -->
    <div
      class="grid-line horizontal"
      v-for="i in 20"
      :key="'h'+i"
      :style="{top: (i*5) + '%'}"
    ></div>
    <div
      class="grid-line vertical"
      v-for="i in 20"
      :key="'v'+i"
      :style="{left: (i*5) + '%'}"
    ></div>
    
    <!-- 数据流效果 -->
    <div
      class="data-stream"
      v-for="i in 10"
      :key="'stream'+i"
      :style="{
        left: Math.random() * 100 + '%',
        animationDelay: Math.random() * 5 + 's',
        height: Math.random() * 100 + 50 + 'px'
      }"
    ></div>
    
    <div class="header">
      <div class="header-left">
        <div class="update-info">
          <div class="update-time">数据更新: {{ lastUpdate }}</div>
          <div class="device-count">
            {{ isUsingMockData ? '使用虚拟数据' : '实时数据获取中' }}
          </div>
        </div>
      </div>
      <div class="title">工业测温监控平台</div>
      <div class="header-right">
        <div class="time">{{ currentTime }}</div>
      </div>
    </div>
    
    <div class="main-content">
      <!-- 左侧面板 -->
      <div class="left-panel">
        <div class="panel">
          <div class="panel-title">
            <i>📊</i> 设备状态分布
          </div>
          <div class="ring-chart-container">
            <div class="ring-gauge-container">
              <div class="ring-gauge-item">
                <div class="ring-gauge-title">正常设备</div>
                <div class="ring-gauge-chart" ref="gaugeChartNormalRef"></div>
                <div class="ring-gauge-label">{{ normalDevices }}台设备</div>
              </div>
              <div class="ring-gauge-item">
                <div class="ring-gauge-title">预警设备</div>
                <div class="ring-gauge-chart" ref="gaugeChartWarningRef"></div>
                <div class="ring-gauge-label">{{ warningDevices }}台设备</div>
              </div>
              <div class="ring-gauge-item">
                <div class="ring-gauge-title">异常设备</div>
                <div class="ring-gauge-chart" ref="gaugeChartDangerRef"></div>
                <div class="ring-gauge-label">{{ dangerDevices }}台设备</div>
              </div>
            </div>
          </div>
        </div>
        
        <!-- 设备状态概览模块 -->
        <div class="panel">
          <div class="panel-title">
            <i>🌡️</i> 设备状态概览
          </div>
          <div class="status-overview-container">
            <div class="status-scroll-wrapper">
              <div 
                v-for="device in devices" 
                :key="device.deviceId" 
                class="status-item"
                :class="{ active: device.deviceId === selectedDevice?.deviceId }"
                :style="{ transform: `translateY(-${deviceScrollOffset}px)` }"
              >
                <div class="status-device">{{ device.deviceId }}</div>
                <div class="status-temp" :class="getTempClass(device.temperature)">
                  {{ device.temperature }}°C
                </div>
                <div class="status-location">{{ device.location }}</div>
              </div>
            </div>
          </div>
        </div>
        
        <!-- 最新告警模块 -->
        <div class="panel">
          <div class="panel-title">
            <i>🔔</i> 最新告警
            <div class="alert-stats" v-if="recentAlerts.length > 0">
              <span class="stat-danger">{{ getAlertStats().dangerAlerts }} 紧急</span>
              <span class="stat-warning">{{ getAlertStats().warningAlerts }} 警告</span>
            </div>
          </div>
          <div class="alerts-container">
            <div v-if="recentAlerts.length === 0" class="no-alerts">
              <div class="no-alerts-icon">✅</div>
              <div class="no-alerts-text">暂无告警</div>
            </div>
            <div v-else class="alerts-scroll-wrapper">
              <div 
                v-for="alert in recentAlerts" 
                :key="alert.id" 
                class="alert-item"
                :class="{
                  active: alert.id === currentAlert?.id,
                  'alert-danger': alert.level === 'danger',
                  'alert-warning': alert.level === 'warning',
                  'alert-info': alert.level === 'info',
                  'alert-acknowledged': alert.acknowledged
                }"
                :style="{ transform: `translateY(-${alertScrollOffset}px)` }"
                @click="acknowledgeAlert(alert.id)"
              >
                <div class="alert-header">
                  <div class="alert-time">{{ alert.time }}</div>
                  <div class="alert-level" :class="alert.level">
                    {{ alert.level === 'danger' ? '紧急' : alert.level === 'warning' ? '警告' : '信息' }}
                  </div>
                </div>
                <div class="alert-content">
                  <div class="alert-device">{{ alert.deviceId }}</div>
                  <div class="alert-message">{{ alert.message }}</div>
                </div>
                <div v-if="alert.acknowledged" class="alert-acknowledged-badge">已确认</div>
              </div>
            </div>
          </div>
        </div>

        <!-- 系统运行状态模块 -->
        <div class="panel">
          <div class="panel-title">
            <i>⚙️</i> 系统运行状态
          </div>
          <div class="system-status">
            <div class="status-item">
              <div class="status-label">数据接收</div>
              <div class="status-value normal">正常</div>
            </div>
            <div class="status-item">
              <div class="status-label">预测算法</div>
              <div class="status-value normal">运行中</div>
            </div>
            <div class="status-item">
              <div class="status-label">网络延迟</div>
              <div class="status-value normal">{{ networkDelay }}ms</div>
            </div>
          </div>
        </div>

      </div>
      
      <!-- 中间面板 - 3D可视化 -->
      <div class="center-panel">
        <!-- 3D工厂管道可视化 -->
        <div class="panel" style="flex: 1; position: relative;">
          <div class="panel-title">
            <i>🏭</i> 工厂管道3D可视化
          </div>
          
      <!-- 天气预报模块 -->
      <div class="weather-widget">
        <div class="weather-header">
          <i class="weather-icon">{{ weatherIcon }}</i>
          <span class="weather-title">天气信息 - {{ weatherData.city }}</span>
          <button class="refresh-btn" @click="fetchWeatherData" :disabled="weatherLoading">
            {{ weatherLoading ? '刷新中...' : '🔄' }}
          </button>
        </div>
        <div class="weather-content">
          <div class="weather-item">
            <div class="weather-label">天气状况</div>
            <div class="weather-value">{{ weatherData.condition }}</div>
          </div>
          <div class="weather-item">
            <div class="weather-label">温度</div>
            <div class="weather-value">{{ weatherData.temperature }}°C</div>
          </div>
          <div class="weather-item">
            <div class="weather-label">湿度</div>
            <div class="weather-value">{{ weatherData.humidity }}%</div>
          </div>
          <div class="weather-item">
            <div class="weather-label">气压</div>
            <div class="weather-value">{{ weatherData.pressure }} hPa</div>
          </div>
          <div class="weather-item">
            <div class="weather-label">更新时间</div>
            <div class="weather-value">{{ weatherData.updateTime }}</div>
          </div>
        </div>
        <div v-if="weatherError" class="weather-error">
          {{ weatherError }}
        </div>
      </div>
          
          <div class="factory-pipeline-3d">
            <div class="scene-container" ref="sceneContainer">
              <img src="./工厂.jpg" class="factory-map" alt="工厂地图">
            </div>
            
            <!-- 加载状态 -->
            <div v-if="loading3D" class="loading-overlay">
              <div class="loading-spinner"></div>
              <div class="loading-text">加载3D场景中...</div>
            </div>
          </div>
        </div>
		
		<!-- 设备状态总览面板 -->
		<div class="panel overview-panel">
		  <div class="panel-title">
		    <i>📋</i> 设备状态总览
		  </div>
		  <div class="stats-container">
		    <div class="stat-item">
		      <div class="stat-label">总设备数</div>
		      <div class="stat-value">{{ deviceStats.totalCount }}</div>
		    </div>
		    <div class="stat-item">
		      <div class="stat-label">在线设备</div>
		      <div class="stat-value stat-normal">{{ deviceStats.onlineCount }}</div>
		    </div>
		    <div class="stat-item">
		      <div class="stat-label">离线设备</div>
		      <div class="stat-value">{{ deviceStats.offlineCount }}</div>
		    </div>
		  </div>
		</div>
        

      </div>
      
      <!-- 右侧面板 -->
      <div class="right-panel">
        <!-- 设备分布3D曲面图 -->
        <div class="panel" style="flex: 1.5; position: relative;">
          <div class="panel-title">
            <i>📍</i> 设备分布3D曲面图
          </div>
          <div class="device-map-container">
            <div class="surface-3d-chart" ref="surface3dChartRef"></div>
          </div>
        </div>
        
        <!-- 设备详情小框 -->
        <div class="panel device-detail-panel">
          <div class="panel-title">
            <i>📱</i> 设备详情 - {{ selectedDevice ? selectedDevice.deviceId : '' }}
          </div>
          <div class="device-detail-content" v-if="selectedDevice">
            <div class="detail-info">
              <div class="info-item">
                <div class="info-label">设备ID</div>
                <div class="info-value">{{ selectedDevice.deviceId }}</div>
              </div>
              <div class="info-item">
                <div class="info-label">当前位置</div>
                <div class="info-value">{{ selectedDevice.location }}</div>
              </div>
              <div class="info-item">
                <div class="info-label">当前温度</div>
                <div class="info-value" :class="getTempClass(selectedDevice.temperature)">
                  {{ selectedDevice.temperature }}°C
                </div>
              </div>
              <div class="info-item">
                <div class="info-label">温度状态</div>
                <div class="info-value">{{ getTempStatus(selectedDevice.temperature) }}</div>
              </div>
              <div class="info-item">
                <div class="info-label">预测温度</div>
                <div class="info-value">{{ selectedDevice.predictedTemp }}°C</div>
              </div>
            </div>
          </div>
        </div>
        
		<!-- 温度趋势预测模块 -->
		<div class="panel">
		  <div class="panel-title">
			<i>📈</i> 温度趋势预测 - {{ selectedDevice ? selectedDevice.deviceId : '' }}
			<div v-if="historyLoading" class="loading-indicator">
			  <span class="loading-text">加载中...</span>
			</div>
		  </div>
		  <div class="chart-container">
			<div class="prediction-chart" ref="predictionChartRef"></div>
			<div v-if="!selectedDevice" class="no-data-prompt">
			  <div class="no-data-icon">📊</div>
			  <div class="no-data-text">请选择设备查看温度趋势</div>
			</div>
			<div v-else-if="temperatureHistory.timestamps.length === 0 && !historyLoading" class="no-data-prompt">
			  <div class="no-data-icon">📈</div>
			  <div class="no-data-text">暂无历史温度数据</div>
			</div>
		  </div>
		</div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, reactive, computed, onMounted, onBeforeUnmount, nextTick } from 'vue'
import * as echarts from 'echarts'
import 'echarts-gl'

export default {
  name: 'IndustrialTemperatureDashboard',
  setup() {
    // API 配置
    const API_CONFIG = {
      BASE_URL: 'http://localhost:8080/api',
      ENDPOINTS: {
        SENSOR_LATEST: '/sensor-latest',
        SENSOR_STATUS_SUMMARY: '/sensor-status-summary',
        DEVICE_SUMMARY: '/device/summary'
      }
    }

    // 响应式数据
    const currentTime = ref('')
    const devices = ref([])
    const selectedDevice = ref(null)
    const predictionChart = ref(null)
    const gaugeChartNormal = ref(null)
    const gaugeChartWarning = ref(null)
    const gaugeChartDanger = ref(null)
    const surface3dChart = ref(null)
    const recentAlerts = ref([])
    const networkDelay = ref(42)
    const lastUpdate = ref('')
    const onlineDevices = ref(0)
    const totalDevices = ref(0)
    const deviceCarouselInterval = ref(null)
    const alertCarouselInterval = ref(null)
    const currentAlertIndex = ref(0)
    const deviceScrollOffset = ref(0)
    const alertScrollOffset = ref(0)
	// 响应式数据 - 在现有数据后面添加
	const deviceStats = ref({
	  onlineCount: 0,
	  offlineCount: 0,
	  totalCount: 0
	})

    // 告警级别常量
    const ALERT_LEVELS = {
      DANGER: 'danger',
      WARNING: 'warning',
      INFO: 'info'
    }

    // 天气预报相关数据
    const weatherData = reactive({
      city: '北京',
      condition: '加载中...',
      temperature: '--',
      feelsLike: '--',
      humidity: '--',
      windSpeed: '--',
      pressure: '--',
      visibility: '--',
      updateTime: '--'
    })
    const weatherLoading = ref(false)
    const weatherError = ref('')

    // 和风天气API配置 - 请替换为您的实际API Key
    const WEATHER_CONFIG = {
      API_KEY: 'd95d87207f554b7a9439c367a3955f31',
      DEFAULT_CITY: '沈阳',
      BASE_URL: 'https://devapi.qweather.com/v7'
    }

    

    // 3D可视化相关数据
    const sceneContainer = ref(null)
    const loading3D = ref(true)
    const autoRotate = ref(true)
    let animationId = null
    let sceneObjects = []
    let cameraAngle = 0
    let cameraDistance = 300
    let cameraHeight = 200

    // DOM 引用
    const gaugeChartNormalRef = ref(null)
    const gaugeChartWarningRef = ref(null)
    const gaugeChartDangerRef = ref(null)
    const predictionChartRef = ref(null)
    const surface3dChartRef = ref(null)

    // 计算属性 - 根据后端数据计算设备状态分布
    const normalDevices = computed(() => {
      return devices.value.filter(d => !d.alarmTriggered && d.isUploaded).length
    })
    
    const warningDevices = computed(() => {
      return devices.value.filter(d => d.alarmTriggered && d.isUploaded).length
    })
    
    const dangerDevices = computed(() => {
      return devices.value.filter(d => !d.isUploaded).length
    })
    
    const normalPercentage = computed(() => {
      return totalDevices.value > 0 ? Math.round((normalDevices.value / totalDevices.value) * 100) : 0
    })
    
    const warningPercentage = computed(() => {
      return totalDevices.value > 0 ? Math.round((warningDevices.value / totalDevices.value) * 100) : 0
    })
    
    const dangerPercentage = computed(() => {
      return totalDevices.value > 0 ? Math.round((dangerDevices.value / totalDevices.value) * 100) : 0
    })
    
    const currentAlert = computed(() => {
      return recentAlerts.value[currentAlertIndex.value]
    })

    const deviceCount = computed(() => {
      return devices.value.length
    })
	
	// 获取设备状态统计
	const fetchDeviceStatusSummary = async () => {
	  try {
	    const response = await fetch(`${API_CONFIG.BASE_URL}/device/summary`)
	    if (response.ok) {
	      const summaryData = await response.json()
	      deviceStats.value = summaryData
	      console.log('设备状态统计:', summaryData)
	    } else {
	      console.error('获取设备状态统计失败:', response.status)
	      // 使用默认值
	      deviceStats.value = {
	        onlineCount: 0,
	        offlineCount: 0,
	        totalCount: 0
	      }
	    }
	  } catch (error) {
	    console.error('获取设备状态统计错误:', error)
		isUsingMockData.value = true
	    // 使用默认值
	    deviceStats.value = {
	      onlineCount: 0,
	      offlineCount: 0,
	      totalCount: 0
	    }
	  }
	}
	
	// 在现有的响应式数据后面添加
	const temperatureHistory = ref({
	  timestamps: [],
	  actual: [],
	  predicted: []
	})
	const historyLoading = ref(false)
	
	// 获取设备温度历史数据
	const fetchTemperatureHistory = async (deviceId) => {
	  if (!deviceId) return
	  
	  historyLoading.value = true
	  try {
	    const response = await fetch(`${API_CONFIG.BASE_URL}/sensor-curve/${deviceId}?limit=20`)
	    if (response.ok) {
	      const historyData = await response.json()
	      temperatureHistory.value = historyData
	      console.log('温度历史数据:', historyData)
	      updatePredictionChartWithHistory()
	    } else {
	      console.error('获取温度历史数据失败:', response.status)
	      // 使用模拟数据作为备用
	      useMockTemperatureHistory(deviceId)
	    }
	  } catch (error) {
	    console.error('获取温度历史数据错误:', error)
	    useMockTemperatureHistory(deviceId)
	  } finally {
	    historyLoading.value = false
	  }
	}
	
	// 模拟温度历史数据（备用）
	const useMockTemperatureHistory = (deviceId) => {
	  const now = new Date()
	  const timestamps = []
	  const actual = []
	  const predicted = []
	  
	  // 生成过去10分钟的数据点
	  for (let i = 9; i >= 0; i--) {
	    const time = new Date(now.getTime() - i * 60000)
	    timestamps.push(time.toLocaleTimeString())
	    
	    const baseTemp = selectedDevice.value?.temperature || 65
	    const actualTemp = baseTemp + Math.random() * 6 - 3
	    const predictedTemp = actualTemp + Math.random() * 4 - 2
	    
	    actual.push(Number(actualTemp.toFixed(1)))
	    predicted.push(Number(predictedTemp.toFixed(1)))
	  }
	  
	  temperatureHistory.value = { timestamps, actual, predicted }
	  updatePredictionChartWithHistory()
	}
	
	// 使用历史数据更新预测图表
	const updatePredictionChartWithHistory = () => {
	  if (predictionChart.value && temperatureHistory.value.timestamps.length > 0) {
	    const { timestamps, actual, predicted } = temperatureHistory.value
	    
	    const option = {
	      tooltip: {
	        trigger: 'axis',
	        textStyle: {
	          color: '#fff'
	        },
	        backgroundColor: 'rgba(16, 35, 70, 0.9)',
	        borderColor: '#4facfe',
	        formatter: function(params) {
	          let result = `<div style="font-weight:bold;margin-bottom:5px;">${params[0].axisValue}</div>`
	          params.forEach(param => {
	            const icon = param.seriesName === '实际温度' ? '🌡️' : '🔮'
	            const color = param.seriesName === '实际温度' ? '#00f2fe' : '#ffcc00'
	            result += `
	              <div style="display:flex;align-items:center;margin:2px 0;">
	                <span style="display:inline-block;width:10px;height:10px;border-radius:50%;background:${color};margin-right:5px;"></span>
	                ${icon} ${param.seriesName}: <span style="color:${color};font-weight:bold;margin-left:5px;">${param.value}°C</span>
	              </div>
	            `
	          })
	          return result
	        }
	      },
	      legend: {
	        data: ['实际温度', '预测温度'],
	        textStyle: {
	          color: '#fff'
	        },
	        top: '2%',
	      },
	      grid: {
	        left: '3%',
	        right: '4%',
	        bottom: '3%',
	        top: '15%',
	        containLabel: true
	      },
		xAxis: {
		  type: 'category',
		  boundaryGap: false,
		  data: timestamps,
		  axisLine: {
			lineStyle: {
			  color: '#4facfe'
			}
		  },
		  axisLabel: {
			color: '#a0c8ff',
			fontSize: 10,
			rotate: 45,
			formatter: function(value) {
			  // 只显示时间部分，精确到秒
			  if (value.includes(' ')) {
				// 如果包含日期和时间，只取时间部分
				return value.split(' ')[1] || value;
			  }
			  // 如果已经是纯时间格式，直接返回
			  return value;
			}
		  }
		},
	      yAxis: {
	        type: 'value',
	        name: '温度 (°C)',
	        nameTextStyle: {
	          color: '#a0c8ff'
	        },
	        min: function(value) {
	          return Math.max(0, Math.floor(value.min - 5))
	        },
	        max: function(value) {
	          return Math.ceil(value.max + 5)
	        },
	        axisLine: {
	          lineStyle: {
	            color: '#4facfe'
	          }
	        },
	        axisLabel: {
	          color: '#a0c8ff',
	          formatter: '{value}°C'
	        },
	        splitLine: {
	          lineStyle: {
	            color: 'rgba(79, 172, 254, 0.2)'
	          }
	        }
	      },
	      series: [
	        {
	          name: '实际温度',
	          type: 'line',
	          data: actual,
	          smooth: true,
	          lineStyle: {
	            color: '#00f2fe',
	            width: 3
	          },
	          symbol: 'circle',
	          symbolSize: 6,
	          itemStyle: {
	            color: '#00f2fe'
	          },
	          areaStyle: {
	            color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
	              { offset: 0, color: 'rgba(0, 242, 254, 0.3)' },
	              { offset: 1, color: 'rgba(0, 242, 254, 0.1)' }
	            ])
	          }
	        },
	        {
	          name: '预测温度',
	          type: 'line',
	          data: predicted,
	          smooth: true,
	          lineStyle: {
	            color: '#ffcc00',
	            width: 3,
	            type: 'dashed'
	          },
	          symbol: 'circle',
	          symbolSize: 6,
	          itemStyle: {
	            color: '#ffcc00'
	          }
	        }
	      ]
	    }
	    
	    predictionChart.value.setOption(option, true)
	  }
	}


   // 在 setup() 函数中添加获取设备摘要的方法
   const fetchDeviceSummary = async () => {
     try {
       const response = await fetch(`${API_CONFIG.BASE_URL}/device/summary`)
       if (response.ok) {
         const summaryData = await response.json()
         onlineDevices.value = summaryData.onlineCount || 0
         totalDevices.value = summaryData.totalCount || 0
       } else {
         console.error('获取设备摘要失败:', response.status)
         // 备用方案：从设备数据计算
         onlineDevices.value = devices.value.filter(d => d.isUploaded).length
         totalDevices.value = devices.value.length
       }
     } catch (error) {
       console.error('获取设备摘要错误:', error)
       // 备用方案
       onlineDevices.value = devices.value.filter(d => d.isUploaded).length
       totalDevices.value = devices.value.length
     }
   }

    // 从后端获取设备数据
    const fetchDeviceData = async () => {
      try {
        // 获取设备最新数据
        const response = await fetch(`${API_CONFIG.BASE_URL}${API_CONFIG.ENDPOINTS.SENSOR_LATEST}`)
        if (response.ok) {
          const sensorData = await response.json()
          console.log('从后端获取的设备数据:', sensorData)
          
          // 转换数据格式，使用后端的实际数据
          const formattedDevices = sensorData.map((data, index) => {
            const temp = data.temperature || Math.floor(Math.random() * 40) + 50
            
            // 确保设备ID正确映射
            const deviceId = data.deviceId || `DEV-${(index + 1).toString().padStart(4, '0')}`
            const gridColumn = Math.floor(index % 5) + 1
            const gridRow = Math.floor(index / 5) + 1
            
            return {
              deviceId: deviceId, // 确保使用正确的设备ID
              sensorId: data.sensorId || `SENSOR-${index + 1}`,
              sensorName: data.sensorName || `温度传感器 ${index + 1}`,
              temperature: temp,
              predictedTemp: data.predictedTemperature || (temp + Math.floor(Math.random() * 10) - 5),
              humidity: data.humidity || Math.floor(Math.random() * 30) + 40,
              pressure: data.pressure || Math.floor(Math.random() * 200) + 1000,
              alarmTriggered: data.alarmTriggered || false,
              alarmMessage: data.alarmMessage || '',
              isUploaded: data.isUploaded !== undefined ? data.isUploaded : true,
              location: `区域${Math.ceil((index+1)/5)}-位置${(index)%5+1}`,
              onlineTime: '2023-06-15 08:30:00',
              lastMaintenance: '2023-08-10 14:20:00',
              status: temp < 70 ? 'normal' : temp < 85 ? 'warning' : 'danger',
              gridColumn: gridColumn,
              gridRow: gridRow,
              x: (gridColumn - 1),
              y: (gridRow - 1),
              z: (temp - 50) / 10
            }
          })
          
          devices.value = formattedDevices
          
          // 计算设备总数和在线设备数
          totalDevices.value = devices.value.length
          onlineDevices.value = devices.value.filter(d => d.isUploaded).length
          
          // 如果有设备数据，选择第一个设备作为默认选中
          if (devices.value.length > 0 && !selectedDevice.value) {
            selectedDevice.value = devices.value[0]
            updateDeviceScrollPosition()
            updatePredictionChart()
            highlightSelectedDevice()
          }
          
          // 更新3D场景
          if (sceneContainer.value) {
            update3DSceneDevices()
          }
          
          // 更新告警数据
          updateAlertsFromDeviceData()
          
          console.log('格式化后的设备数据:', formattedDevices)
        } else {
          console.error('获取设备数据失败:', response.status)
          // 如果后端获取失败，使用模拟数据
          initDevices()
        }
      } catch (error) {
		console.error('获取设备数据错误:', error)
		console.error('错误详情:', {
		  name: error.name,
		  message: error.message,
		  stack: error.stack
		})
		isUsingMockData.value = true  // 标记使用虚拟数据
		initDevices()
	  }
	}
	
	

    // 从设备数据更新告警
    const updateAlertsFromDeviceData = () => {
      // 清空原有告警
      recentAlerts.value = []
      
      devices.value.forEach(device => {
        // 根据设备状态生成告警
        if (device.alarmTriggered) {
          const alert = generateAlert(device, 'temperature_high', device.alarmMessage || `设备 ${device.deviceId} 触发告警`)
          addAlert(alert)
        }
        
        if (!device.isUploaded) {
          const alert = generateAlert(device, 'connection_lost', `设备 ${device.deviceId} 数据未上传`)
          addAlert(alert)
        }
      })
    }
    
    // 告警相关方法
    const generateAlert = (device, alertType, message) => {
      const alertId = Date.now() + Math.random()
      const alertTime = new Date().toLocaleString()
      
      let level = ALERT_LEVELS.INFO
      let alertMessage = message
      
      switch (alertType) {
        case 'temperature_high':
          level = device.temperature >= 85 ? ALERT_LEVELS.DANGER : ALERT_LEVELS.WARNING
          alertMessage = `温度过高: ${device.temperature}°C`
          break
        case 'temperature_low':
          level = ALERT_LEVELS.WARNING
          alertMessage = `温度过低: ${device.temperature}°C`
          break
        case 'connection_lost':
          level = ALERT_LEVELS.DANGER
          alertMessage = '设备连接丢失'
          break
        case 'connection_restored':
          level = ALERT_LEVELS.INFO
          alertMessage = '设备通讯恢复'
          break
        case 'prediction_alert':
          level = ALERT_LEVELS.WARNING
          alertMessage = `预测温度异常: ${device.predictedTemp}°C`
          break
        case 'fluctuation':
          level = ALERT_LEVELS.WARNING
          alertMessage = '温度异常波动'
          break
        default:
          alertMessage = message || '设备状态异常'
      }
      
      return {
        id: alertId,
        time: alertTime,
        deviceId: device.deviceId,
        message: alertMessage,
        level: level,
        temperature: device.temperature,
        location: device.location,
        acknowledged: false
      }
    }

    const addAlert = (alert) => {
      recentAlerts.value.unshift(alert)
      
      // 限制告警列表长度，最多保留50条
      if (recentAlerts.value.length > 50) {
        recentAlerts.value = recentAlerts.value.slice(0, 50)
      }
      
      // 自动滚动到最新告警
      currentAlertIndex.value = 0
      updateAlertScrollPosition()
      
      console.log('新告警:', alert)
    }

    const acknowledgeAlert = (alertId) => {
      const alert = recentAlerts.value.find(a => a.id === alertId)
      if (alert) {
        alert.acknowledged = true
      }
    }

    const getAlertStats = () => {
      const total = recentAlerts.value.length
      const unacknowledged = recentAlerts.value.filter(alert => !alert.acknowledged).length
      const dangerAlerts = recentAlerts.value.filter(alert => alert.level === ALERT_LEVELS.DANGER).length
      const warningAlerts = recentAlerts.value.filter(alert => alert.level === ALERT_LEVELS.WARNING).length
      
      return {
        total,
        unacknowledged,
        dangerAlerts,
        warningAlerts
      }
    }

    const detectTemperatureAlerts = (device, oldTemperature) => {
      const tempChange = Math.abs(device.temperature - oldTemperature)
      
      // 温度超过阈值告警
      if (device.temperature >= 85) {
        addAlert(generateAlert(device, 'temperature_high', `温度严重过高: ${device.temperature}°C`))
      } else if (device.temperature >= 80) {
        addAlert(generateAlert(device, 'temperature_high', `温度偏高: ${device.temperature}°C`))
      } else if (device.temperature <= 55) {
        addAlert(generateAlert(device, 'temperature_low', `温度偏低: ${device.temperature}°C`))
      }
      
      // 温度剧烈变化告警
      if (tempChange >= 8) {
        addAlert(generateAlert(device, 'fluctuation', `温度剧烈变化: ${oldTemperature}°C → ${device.temperature}°C`))
      }
      
      // 预测温度异常告警
      if (Math.abs(device.temperature - device.predictedTemp) >= 10) {
        addAlert(generateAlert(device, 'prediction_alert', `预测偏差过大: 实际${device.temperature}°C vs 预测${device.predictedTemp}°C`))
      }
    }

    const simulateConnectionIssues = () => {
      // 5%的概率模拟设备连接问题
      if (Math.random() < 0.05) {
        const randomDevice = devices.value[Math.floor(Math.random() * devices.value.length)]
        addAlert(generateAlert(randomDevice, 'connection_lost', '设备通讯中断'))
        
        // 临时标记设备为离线状态
        setTimeout(() => {
          const device = devices.value.find(d => d.deviceId === randomDevice.deviceId)
          if (device) {
            addAlert(generateAlert(device, 'connection_restored', '设备通讯恢复'))
          }
        }, 5000)
      }
    }

    const initAlerts = () => {
      // 清空原有示例数据
      recentAlerts.value = []
      
      // 为初始设备状态生成告警
      devices.value.forEach(device => {
        if (device.temperature >= 80) {
          addAlert(generateAlert(device, 'temperature_high', `初始温度过高: ${device.temperature}°C`))
        }
        if (!device.isUploaded) {
          addAlert(generateAlert(device, 'connection_lost', `设备 ${device.deviceId} 数据未上传`))
        }
      })
    }

// 天气图标映射 - 根据后端返回的天气状况描述
const weatherIcons = {
  '晴': '☀️',
  '多云': '⛅', 
  '阴': '☁️',
  '小雨': '🌧️',
  '中雨': '🌧️',
  '大雨': '🌧️',
  '阵雨': '🌦️',
  '雷阵雨': '⛈️',
  '雪': '❄️',
  '雾': '🌫️',
  '霾': '🌫️',
  '沙尘': '🌪️',
  '默认': '🌈'
}

// 获取天气图标表情 - 支持后端图标代码
const getWeatherIcon = (iconCode, condition) => {
  // 后端图标代码映射
  const backendIconMap = {
    "01d": "☀️", "01n": "🌙",
    "02d": "⛅", "02n": "☁️",
    "03d": "☁️", "03n": "☁️", 
    "04d": "☁️", "04n": "☁️",
    "09d": "🌧️", "09n": "🌧️",
    "10d": "🌦️", "10n": "🌦️",
    "11d": "⛈️", "11n": "⛈️",
    "13d": "❄️", "13n": "❄️",
    "50d": "🌫️", "50n": "🌫️",
  }
  
  // 如果后端提供了图标代码，优先使用
  if (iconCode && backendIconMap[iconCode]) {
    return backendIconMap[iconCode]
  }
  
  // 否则根据天气状况描述匹配
  for (const [key, icon] of Object.entries(weatherIcons)) {
    if (condition.includes(key)) {
      return icon
    }
  }
  
  return weatherIcons['默认']
}

const weatherIcon = computed(() => {
  const condition = weatherData.condition
  return getWeatherIcon(null, condition) 
})

// 天气预报方法
const fetchWeatherData = async () => {
  if (weatherLoading.value) return
  
  weatherLoading.value = true
  weatherError.value = ''

  try {
    const weatherResponse = await fetch('http://localhost:5000/api/weather/shenyang')
    
    if (weatherResponse.ok) {
      const weatherResult = await weatherResponse.json()
      console.log('后端天气API响应:', weatherResult)
      
      if (weatherResult && !weatherResult.error) {
        // 直接使用后端返回的顶层数据，不需要解析嵌套结构
        Object.assign(weatherData, {
          city: weatherResult.city || '沈阳',
          condition: weatherResult.condition || '未知',
          temperature: weatherResult.temperature || '--',
          humidity: weatherResult.humidity || '--',
          pressure: weatherResult.pressure || '--',
          updateTime: weatherResult.updateTime || new Date().toLocaleTimeString()
        })
        
        weatherLoading.value = false
        console.log('天气数据获取成功:', weatherData)
        return
      }
    } else {
      console.error('后端天气API请求失败:', weatherResponse.status)
    }
    
    throw new Error('后端API调用失败，使用模拟数据')
    
  } catch (error) {
    console.error('获取天气数据错误:', error)
    weatherError.value = '获取天气数据失败，使用模拟数据'
    useMockWeatherData()
  } finally {
    weatherLoading.value = false
  }
}

    // 模拟天气数据（备用）
    const useMockWeatherData = () => {
      const conditions = ['晴', '多云', '阴', '小雨', '中雨']
      const randomCondition = conditions[Math.floor(Math.random() * conditions.length)]
      
      Object.assign(weatherData, {
        city: WEATHER_CONFIG.DEFAULT_CITY,
        condition: randomCondition,
        temperature: Math.floor(Math.random() * 15) + 20,
        feelsLike: Math.floor(Math.random() * 15) + 18,
        humidity: Math.floor(Math.random() * 40) + 40,
        windSpeed: (Math.random() * 20).toFixed(1),
        pressure: Math.floor(Math.random() * 20) + 1000,
        visibility: (Math.random() * 10 + 5).toFixed(1),
        updateTime: new Date().toLocaleTimeString() + ' (模拟数据)'
      })
    }

	// 3D可视化方法
	const init3DScene = () => {
	  if (!sceneContainer.value) return

	  // 清除现有画布
	  const existingCanvas = sceneContainer.value.querySelector('canvas')
	  if (existingCanvas) {
		sceneContainer.value.removeChild(existingCanvas)
	  }

	  // 创建画布
	  const canvas = document.createElement('canvas')
	  const ctx = canvas.getContext('2d')
	  canvas.width = sceneContainer.value.clientWidth
	  canvas.height = sceneContainer.value.clientHeight
	  canvas.style.width = '100%'
	  canvas.style.height = '100%'
	  canvas.style.position = 'absolute'
	  canvas.style.top = '0'
	  canvas.style.left = '0'
	  canvas.style.zIndex = '2'
	  sceneContainer.value.appendChild(canvas)

	  // 等待设备数据加载完成
	  if (devices.value.length === 0) {
		console.log('等待设备数据加载...')
		setTimeout(init3DScene, 500) // 延迟重试
		return
	  }

	  // 创建设备数据 - 确保使用最新的设备信息
	  const deviceData = devices.value.slice(0, 10).map((device, index) => {
		const positions = [
		  { x: 1179 * 0.25, y: 1061 * 0.35 },
		  { x: 1179 * 0.80, y: 1061 * 0.15 },
		  { x: 1179 * 0.10, y: 1061 * 0.76 },
		  { x: 1179 * 0.83, y: 1061 * 0.85 },
		  { x: 1179 * 0.5, y: 1061 * 0.15 },
		  { x: 1179 * 0.50, y: 1061 * 0.85 },
		  { x: 1179 * 0.90, y: 1061 * 0.60 },
		  { x: 1179 * 0.85, y: 1061 * 0.40 },
		  { x: 1179 * 0.5, y: 1061 * 0.6 },
		  { x: 1179 * 0.3, y: 1061 * 0.7 }
		];
		
		const pos = positions[index] || { x: 100, y: 100 }
		
		return {
		  ...device, // 使用最新的设备数据
		  position: { 
			x: pos.x,
			y: pos.y,
			z: (device.temperature - 50) / 5 // 使用实时温度计算高度
		  },
		  type: ['valve', 'sensor', 'pump'][index % 3],
		  originalX: pos.x,
		  originalY: pos.y,
		  pulsePhase: Math.random() * Math.PI * 2,
		  color: getDevice3DColor(device), // 根据设备状态设置颜色
		  radius: 15
		}
	  })

	  sceneObjects = deviceData

	  // 开始动画循环
	  animate3D()

	  loading3D.value = false
	  console.log('3D场景初始化完成，设备数量:', sceneObjects.length)
	}

    const getDevice3DColor = (device) => {
      if (!device.isUploaded) return '#ff4d4d' // 红色 - 异常（未上传）
      if (device.alarmTriggered) return '#ffcc00' // 黄色 - 预警（触发告警）
      return '#00ff9d' // 绿色 - 正常
    }

    // 新增方法：更新3D场景设备数据
    const update3DSceneDevices = () => {
      sceneObjects.forEach(sceneDevice => {
        const device = devices.value.find(d => d.deviceId === sceneDevice.deviceId)
        if (device) {
          // 更新颜色
          sceneDevice.color = getDevice3DColor(device)
          // 更新Z轴位置（高度）
          sceneDevice.position.z = (device.temperature - 50) / 5
          // 更新设备温度显示
          sceneDevice.temperature = device.temperature
          // 更新设备状态
          sceneDevice.alarmTriggered = device.alarmTriggered
          sceneDevice.isUploaded = device.isUploaded
        }
      })
      
      // 强制重新渲染3D场景
      if (sceneContainer.value) {
        const canvas = sceneContainer.value.querySelector('canvas')
        if (canvas) {
          const ctx = canvas.getContext('2d')
          draw3DScene(ctx, canvas)
        }
      }
    }

    const project3DTo2D = (x, y, z) => {
      const scale = cameraDistance / (cameraDistance + z + cameraHeight)
      const projectedX = x * scale
      const projectedY = y * scale
      return { x: projectedX, y: projectedY, scale: scale }
    }

	const draw3DScene = (ctx, canvas) => {
	  ctx.clearRect(0, 0, canvas.width, canvas.height)
	  
	  // 检查是否有设备数据
	  if (sceneObjects.length === 0) {
		// 如果没有设备，显示提示信息
		ctx.fillStyle = '#a0c8ff'
		ctx.font = '16px Arial'
		ctx.textAlign = 'center'
		ctx.textBaseline = 'middle'
		ctx.fillText('正在加载设备数据...', canvas.width / 2, canvas.height / 2)
		return
	  }
	  
	  sceneObjects.forEach(device => {
		draw3DDevice(ctx, device, canvas)
	  })

	  draw3DConnections(ctx, canvas)
	}

    const draw3DDevice = (ctx, device, canvas) => {
      const projected = project3DTo2D(device.position.x, device.position.y, device.position.z)
      const { x, y, scale } = projected

      const gradient = ctx.createRadialGradient(x, y, 0, x, y, device.radius * scale)
      gradient.addColorStop(0, device.color)
      gradient.addColorStop(1, darkenColor(device.color, 0.7))

      ctx.fillStyle = gradient
      ctx.beginPath()
      ctx.arc(x, y, device.radius * scale, 0, Math.PI * 2)
      ctx.fill()

      ctx.fillStyle = 'rgba(255, 255, 255, 0.3)'
      ctx.beginPath()
      ctx.arc(x - device.radius * scale * 0.3, y - device.radius * scale * 0.3, device.radius * scale * 0.4, 0, Math.PI * 2)
      ctx.fill()

      ctx.strokeStyle = '#ffffff'
      ctx.lineWidth = 2 * scale
      ctx.beginPath()
      ctx.arc(x, y, device.radius * scale, 0, Math.PI * 2)
      ctx.stroke()

      if (selectedDevice.value && selectedDevice.value.deviceId === device.deviceId) {
        const pulseSize = 3 + Math.sin(device.pulsePhase) * 2
        ctx.strokeStyle = '#ff00ff'
        ctx.lineWidth = 3 * scale
        ctx.beginPath()
        ctx.arc(x, y, (device.radius + pulseSize) * scale, 0, Math.PI * 2)
        ctx.stroke()

        ctx.strokeStyle = 'rgba(255, 0, 255, 0.5)'
        ctx.lineWidth = 2 * scale
        ctx.beginPath()
        ctx.arc(x, y, (device.radius + 8 + pulseSize * 2) * scale, 0, Math.PI * 2)
        ctx.stroke()
      }

      ctx.fillStyle = '#ffffff'
      ctx.font = `bold ${16 * scale}px Arial`
      ctx.textAlign = 'center'
      ctx.textBaseline = 'middle'
      
      let icon = '⚙️'
      if (device.type === 'valve') icon = '🔧'
      else if (device.type === 'sensor') icon = '📡'
      else if (device.type === 'pump') icon = '⚡'
      
      ctx.fillText(icon, x, y)

      ctx.fillStyle = '#a0c8ff'
      ctx.font = `${20 * scale}px Arial`
      ctx.fillText(device.deviceId, x, y + (device.radius + 15) * scale)

      // 确保温度文本使用最新的设备温度
      ctx.fillStyle = device.color
      ctx.font = `bold ${16 * scale}px Arial`
      ctx.fillText(`${device.temperature}°C`, x, y + (device.radius - 40) * scale)

      // 显示设备状态
      ctx.fillStyle = '#ffffff'
      ctx.font = `${12 * scale}px Arial`
      const statusText = !device.isUploaded ? '离线' : device.alarmTriggered ? '告警' : '正常'
      ctx.fillText(statusText, x, y + (device.radius + 35) * scale)
    }

    const draw3DConnections = (ctx, canvas) => {
      ctx.strokeStyle = 'rgba(79, 172, 254, 0.4)'
      ctx.lineWidth = 2
      ctx.setLineDash([3, 3])

      for (let i = 0; i < sceneObjects.length; i++) {
        for (let j = i + 1; j < sceneObjects.length; j++) {
          const device1 = sceneObjects[i]
          const device2 = sceneObjects[j]
          
          const dx = device1.position.x - device2.position.x
          const dy = device1.position.y - device2.position.y
          const distance = Math.sqrt(dx * dx + dy * dy)
          
          if (distance < 200) {
            const projected1 = project3DTo2D(device1.position.x, device1.position.y, device1.position.z)
            const projected2 = project3DTo2D(device2.position.x, device2.position.y, device2.position.z)
            
            ctx.beginPath()
            ctx.moveTo(projected1.x, projected1.y)
            ctx.lineTo(projected2.x, projected2.y)
            ctx.stroke()
          }
        }
      }

      ctx.setLineDash([])
    }

    const darkenColor = (color, factor) => {
      const hex = color.replace('#', '')
      const num = parseInt(hex, 16)
      const amt = Math.round(2.55 * factor * 100)
      const R = (num >> 16) - amt
      const G = (num >> 8 & 0x00FF) - amt
      const B = (num & 0x0000FF) - amt
      return "#" + (0x1000000 + (R < 255 ? R < 1 ? 0 : R : 255) * 0x10000 +
              (G < 255 ? G < 1 ? 0 : G : 255) * 0x100 +
              (B < 255 ? B < 1 ? 0 : B : 255)).toString(16).slice(1)
    }

    const handle3DDeviceClick = (event) => {
      const rect = sceneContainer.value.getBoundingClientRect()
      const clickX = event.clientX - rect.left
      const clickY = event.clientY - rect.top

      let clickedDevice = null
      let minDistance = Infinity

      sceneObjects.forEach(device => {
        const projected = project3DTo2D(device.position.x, device.position.y, device.position.z)
        
        const dx = projected.x - clickX
        const dy = projected.y - clickY
        const distance = Math.sqrt(dx * dx + dy * dy)
        const clickRadius = device.radius * projected.scale

        if (distance <= clickRadius && distance < minDistance) {
          minDistance = distance
          clickedDevice = device
        }
      })

      if (clickedDevice) {
        const foundDevice = devices.value.find(d => d.deviceId === clickedDevice.deviceId)
        if (foundDevice) {
          selectedDevice.value = foundDevice
          updateDeviceScrollPosition()
          fetchTemperatureHistory(selectedDevice.value.deviceId) // 新增
          highlightSelectedDevice()
        }
      }
    }

    const animate3D = () => {
      if (!sceneContainer.value) return

      const canvas = sceneContainer.value.querySelector('canvas')
      if (!canvas) return

      const ctx = canvas.getContext('2d')
      draw3DScene(ctx, canvas)

      if (autoRotate.value) {
        sceneObjects.forEach(device => {
          device.pulsePhase += 0.1
        })
      }

      animationId = requestAnimationFrame(animate3D)
    }

    const toggleAutoRotate = () => {
      autoRotate.value = !autoRotate.value
    }

    const resetCamera = () => {
      cameraAngle = 0
      cameraDistance = 300
      cameraHeight = 200
    }

    // 原有方法
    const updateTime = () => {
      currentTime.value = new Date().toLocaleString()
    }
    
    const initDevices = () => {
      // 这个方法现在只作为备用，主要使用fetchDeviceData从后端获取数据
      const newDevices = []
      for (let i = 1; i <= 10; i++) {
        const temp = Math.floor(Math.random() * 40) + 50
        const gridColumn = Math.floor((i-1) % 5) + 1
        const gridRow = Math.floor((i-1) / 5) + 1
        
        newDevices.push({
          deviceId: `DEV-${i.toString().padStart(4, '0')}`,
          sensorId: `SENSOR-${i}`,
          sensorName: `温度传感器 ${i}`,
          temperature: temp,
          predictedTemp: temp + Math.floor(Math.random() * 10) - 5,
          humidity: Math.floor(Math.random() * 30) + 40,
          pressure: Math.floor(Math.random() * 200) + 1000,
          alarmTriggered: Math.random() > 0.7,
          alarmMessage: Math.random() > 0.7 ? '温度过高告警' : '',
          isUploaded: Math.random() > 0.2,
          location: `区域${Math.ceil(i/5)}-位置${(i-1)%5+1}`,
          onlineTime: '2023-06-15 08:30:00',
          lastMaintenance: '2023-08-10 14:20:00',
          status: temp < 70 ? 'normal' : temp < 85 ? 'warning' : 'danger',
          gridColumn: gridColumn,
          gridRow: gridRow,
          x: (gridColumn - 1),
          y: (gridRow - 1),
          z: (temp - 50) / 10
        })
      }
      devices.value = newDevices
      totalDevices.value = devices.value.length
      onlineDevices.value = devices.value.filter(d => d.isUploaded).length
      selectRandomDevice()
    }
    
    const selectRandomDevice = () => {
      if (devices.value.length > 0) {
        const randomIndex = Math.floor(Math.random() * devices.value.length)
        selectedDevice.value = devices.value[randomIndex]
        updateDeviceScrollPosition()
        fetchTemperatureHistory(selectedDevice.value.deviceId) // 新增
        highlightSelectedDevice()
      }
    }
    
    const updateDeviceScrollPosition = () => {
      if (selectedDevice.value) {
        const deviceIndex = devices.value.findIndex(d => d.deviceId === selectedDevice.value.deviceId)
        const itemHeight = 40
        const visibleItems = 4
        const maxScroll = Math.max(0, (devices.value.length - visibleItems) * itemHeight)
        deviceScrollOffset.value = Math.min(deviceIndex * itemHeight, maxScroll)
      }
    }
    
    const updateAlertScrollPosition = () => {
      const itemHeight = 60
      const visibleItems = 3
      const maxScroll = Math.max(0, (recentAlerts.value.length - visibleItems) * itemHeight)
      alertScrollOffset.value = Math.min(currentAlertIndex.value * itemHeight, maxScroll)
    }
    
    const startDeviceCarousel = () => {
      deviceCarouselInterval.value = setInterval(() => {
        selectRandomDevice()
      }, 3000)
    }
    
    const startAlertCarousel = () => {
      alertCarouselInterval.value = setInterval(() => {
        currentAlertIndex.value = (currentAlertIndex.value + 1) % recentAlerts.value.length
        updateAlertScrollPosition()
      }, 2000)
    }
    
    const highlightSelectedDevice = () => {
      if (surface3dChart.value && selectedDevice.value) {
        const option = surface3dChart.value.getOption()
        
        const deviceData = devices.value.map(device => {
          let color
          if (!device.isUploaded) {
            color = '#ff4d4d' // 红色 - 异常
          } else if (device.alarmTriggered) {
            color = '#ffcc00' // 黄色 - 预警
          } else {
            color = '#00ff9d' // 绿色 - 正常
          }
          
          const surfaceZ = 2 * Math.sin(device.x / 4 * Math.PI) * Math.cos(device.y / 1 * Math.PI)
          const finalZ = surfaceZ + device.z
          
          if (device.deviceId === selectedDevice.value.deviceId) {
            return {
              value: [device.x, device.y, finalZ],
              name: device.deviceId,
              temperature: device.temperature,
              itemStyle: {
                color: '#ff00ff',
                opacity: 1
              },
              symbolSize: 20,
              symbol: 'circle'
            }
          }
          
          return {
            value: [device.x, device.y, finalZ],
            name: device.deviceId,
            temperature: device.temperature,
            itemStyle: {
              color: color,
              opacity: 0.8
            },
            symbolSize: 8
          }
        })
        
        option.series[1].data = deviceData
        surface3dChart.value.setOption(option)
      }
    }
    
    const updatePredictionChart = () => {
      if (predictionChart.value && selectedDevice.value) {
        const option = predictionChart.value.getOption()
        
        const baseTemp = selectedDevice.value.temperature
        const actualData = []
        const predictedData = []
        
        for (let i = 0; i < 7; i++) {
          const actualTemp = baseTemp + Math.floor(Math.random() * 8) - 4
          const predictedTemp = baseTemp + Math.floor(Math.random() * 10) - 5
          actualData.push(actualTemp)
          predictedData.push(predictedTemp)
        }
        
        option.series[0].data = actualData
        option.series[1].data = predictedData
        predictionChart.value.setOption(option)
      }
    }
    
    const initCharts = () => {
      console.log('初始化图表...')
      
      if (predictionChartRef.value) {
        predictionChart.value = echarts.init(predictionChartRef.value)
        const predictionOption = getPredictionOption()
        predictionChart.value.setOption(predictionOption)
        console.log('温度趋势预测图表初始化完成')
      }
      
      initSurface3dChart()
      initGaugeCharts()
    }
    
    const initSurface3dChart = () => {
      if (surface3dChartRef.value) {
        surface3dChart.value = echarts.init(surface3dChartRef.value)
        const surface3dOption = getSurface3dOption()
        surface3dChart.value.setOption(surface3dOption)
        console.log('3D曲面图初始化完成')
      }
    }
    
    const getSurface3dOption = () => {
      const deviceData = devices.value.map(device => {
        let color
        if (!device.isUploaded) {
          color = '#ff4d4d' // 红色 - 异常
        } else if (device.alarmTriggered) {
          color = '#ffcc00' // 黄色 - 预警
        } else {
          color = '#00ff9d' // 绿色 - 正常
        }
        
        const surfaceZ = 2 * Math.sin(device.x / 4 * Math.PI) * Math.cos(device.y / 1 * Math.PI)
        const finalZ = surfaceZ + device.z
        
        return {
          value: [device.x, device.y, finalZ],
          name: device.deviceId,
          temperature: device.temperature,
          itemStyle: {
            color: color,
            opacity: 0.8
          },
          symbolSize: 8
        }
      })
      
      return {
        tooltip: {
          show: false
        },
        visualMap: {
          show: true,
          dimension: 2,
          min: 0,
          max: 5,
          inRange: {
            color: ['#00ff9d', '#ffcc00', '#ff4d4d']
          },
          textStyle: {
            color: '#fff'
          }
        },
        xAxis3D: {
          type: 'value',
          min: 0,
          max: 4,
          name: 'X轴',
          nameTextStyle: {
            color: '#a0c8ff'
          },
          axisLine: {
            lineStyle: {
              color: '#4facfe'
            }
          },
          axisLabel: {
            textStyle: {
              color: '#a0c8ff'
            }
          },
          splitLine: {
            lineStyle: {
              color: 'rgba(79, 172, 254, 0.2)'
            }
          }
        },
        yAxis3D: {
          type: 'value',
          min: 0,
          max: 1,
          name: 'Y轴',
          nameTextStyle: {
            color: '#a0c8ff'
          },
          axisLine: {
            lineStyle: {
              color: '#4facfe'
            }
          },
          axisLabel: {
            textStyle: {
              color: '#a0c8ff'
            }
          },
          splitLine: {
            lineStyle: {
              color: 'rgba(79, 172, 254, 0.2)'
            }
          }
        },
        zAxis3D: {
          type: 'value',
          min: -2,
          max: 5,
          name: '温度',
          nameTextStyle: {
            color: '#a0c8ff'
          },
          axisLine: {
            lineStyle: {
              color: '#4facfe'
            }
          },
          axisLabel: {
            textStyle: {
              color: '#a0c8ff'
            }
          },
          splitLine: {
            lineStyle: {
              color: 'rgba(79, 172, 254, 0.2)'
            }
          }
        },
        grid3D: {
          boxWidth: 100,
          boxDepth: 80,
          boxHeight: 60,
          environment: '#0a1a35',
          viewControl: {
            autoRotate: true,
            autoRotateSpeed: 5,
            distance: 120,
            beta: 40,
            alpha: 30
          },
          light: {
            main: {
              intensity: 1.5,
              shadow: false
            },
            ambient: {
              intensity: 0.7
            }
          }
        },
        series: [
          {
            type: 'surface',
            wireframe: {
              show: true,
              lineStyle: {
                color: 'rgba(79, 172, 254, 0.6)',
                width: 1
              }
            },
            itemStyle: {
              color: 'rgba(79, 172, 254, 0.1)',
              opacity: 0.8
            },
            parametric: true,
            parametricEquation: {
              u: {
                min: 0,
                max: Math.PI
              },
              v: {
                min: 0,
                max: Math.PI
              },
              x: function (u, v) {
                return 4 * u / Math.PI
              },
              y: function (u, v) {
                return 1 * v / Math.PI
              },
              z: function (u, v) {
                return 2 * Math.sin(u) * Math.cos(v)
              }
            }
          },
          {
            type: 'scatter3D',
            symbol: 'circle',
            data: deviceData,
            itemStyle: {
              opacity: 0.8
            }
          }
        ]
      }
    }
    
    const getPredictionOption = () => {
      return {
        tooltip: {
          trigger: 'axis',
          textStyle: {
            color: '#fff'
          },
          backgroundColor: 'rgba(16, 35, 70, 0.9)',
          borderColor: '#4facfe'
        },
        legend: {
          data: ['实际温度', '预测温度'],
          textStyle: {
            color: '#fff'
          },
          top: '2%',
        },
        grid: {
          left: '0%',
          right: '8%',
          bottom: '5%',
          top: '15%',
          containLabel: true
        },
        xAxis: {
          type: 'category',
          boundaryGap: false,
          data: ['10:30', '10:35', '10:40', '10:45', '10:50', '10:55', '11:00'],
          axisLine: {
            lineStyle: {
              color: '#4facfe'
            }
          },
          axisLabel: {
            color: '#a0c8ff'
          }
        },
        yAxis: {
          type: 'value',
          axisLine: {
            lineStyle: {
              color: '#4facfe'
            }
          },
          axisLabel: {
            color: '#a0c8ff'
          },
          splitLine: {
            lineStyle: {
              color: 'rgba(79, 172, 254, 0.2)'
            }
          }
        },
        series: [
          {
            name: '实际温度',
            type: 'line',
            data: [65, 68, 70, 72, 71, 69, 67],
            lineStyle: {
              color: '#00f2fe',
              width: 2
            },
            symbol: 'circle',
            symbolSize: 6,
            itemStyle: {
              color: '#00f2fe'
            }
          },
          {
            name: '预测温度',
            type: 'line',
            data: [65, 67, 69, 72, 70, 68, 66],
            lineStyle: {
              color: '#ffcc00',
              width: 2,
              type: 'dashed'
            },
            symbol: 'circle',
            symbolSize: 6,
            itemStyle: {
              color: '#ffcc00'
            }
          }
        ]
      }
    }
    
    const initGaugeCharts = () => {
      if (gaugeChartNormalRef.value) {
        gaugeChartNormal.value = echarts.init(gaugeChartNormalRef.value)
        const gaugeOptionNormal = {
          series: [
            {
              type: 'gauge',
              radius: '100%',
              center: ['50%', '50%'],
              startAngle: 180,
              endAngle: 0,
              min: 0,
              max: 100,
              splitNumber: 10,
              itemStyle: {
                color: '#00ff9d'
              },
              progress: {
                show: true,
                width: 15,
                roundCap: true
              },
              pointer: {
                show: false
              },
              axisLine: {
                roundCap: true,
                lineStyle: {
                  width: 15,
                  color: [[1, 'rgba(0, 255, 157, 0.2)']]
                }
              },
              axisTick: {
                show: false
              },
              splitLine: {
                show: false
              },
              axisLabel: {
                show: false
              },
              title: {
                show: false
              },
              detail: {
                show: false
              },
              data: [
                {
                  value: normalPercentage.value
                }
              ]
            }
          ]
        }
        gaugeChartNormal.value.setOption(gaugeOptionNormal)
      }
      
      if (gaugeChartWarningRef.value) {
        gaugeChartWarning.value = echarts.init(gaugeChartWarningRef.value)
        const gaugeOptionWarning = {
          series: [
            {
              type: 'gauge',
              radius: '100%',
              center: ['50%', '50%'],
              startAngle: 180,
              endAngle: 0,
              min: 0,
              max: 100,
              splitNumber: 10,
              itemStyle: {
                color: '#ffcc00'
              },
              progress: {
                show: true,
                width: 15,
                roundCap: true
              },
              pointer: {
                show: false
              },
              axisLine: {
                roundCap: true,
                lineStyle: {
                  width: 15,
                  color: [[1, 'rgba(255, 204, 0, 0.2)']]
                }
              },
              axisTick: {
                show: false
              },
              splitLine: {
                show: false
              },
              axisLabel: {
                show: false
              },
              title: {
                show: false
              },
              detail: {
                show: false
              },
              data: [
                {
                  value: warningPercentage.value
                }
              ]
            }
          ]
        }
        gaugeChartWarning.value.setOption(gaugeOptionWarning)
      }
      
      if (gaugeChartDangerRef.value) {
        gaugeChartDanger.value = echarts.init(gaugeChartDangerRef.value)
        const gaugeOptionDanger = {
          series: [
            {
              type: 'gauge',
              radius: '100%',
              center: ['50%', '50%'],
              startAngle: 180,
              endAngle: 0,
              min: 0,
              max: 100,
              splitNumber: 10,
              itemStyle: {
                color: '#ff4d4d'
              },
              progress: {
                show: true,
                width: 15,
                roundCap: true
              },
              pointer: {
                show: false
              },
              axisLine: {
                roundCap: true,
                lineStyle: {
                  width: 15,
                  color: [[1, 'rgba(255, 77, 77, 0.2)']]
                }
              },
              axisTick: {
                show: false
              },
              splitLine: {
                show: false
              },
              axisLabel: {
                show: false
              },
              title: {
                show: false
              },
              detail: {
                show: false
              },
              data: [
                {
                  value: dangerPercentage.value
                }
              ]
            }
          ]
        }
        gaugeChartDanger.value.setOption(gaugeOptionDanger)
      }
    }
    
    const updateDeviceData = () => {
      const isUsingMockData = ref(false)
	  
	  // 从后端获取最新数据
      fetchDeviceData()
      fetchDeviceSummary()
	  
      lastUpdate.value = new Date().toLocaleTimeString()
      
      // 更新3D场景中的设备颜色和位置
      update3DSceneDevices()
      
      if (surface3dChart.value) {
        highlightSelectedDevice()
      }
      
      updateGaugeCharts()
      updatePredictionChart()
    }
    
    const updateGaugeCharts = () => {
      if (gaugeChartNormal.value) {
        const option = gaugeChartNormal.value.getOption()
        option.series[0].data[0].value = normalPercentage.value
        gaugeChartNormal.value.setOption(option)
      }
      
      if (gaugeChartWarning.value) {
        const option = gaugeChartWarning.value.getOption()
        option.series[0].data[0].value = warningPercentage.value
        gaugeChartWarning.value.setOption(option)
      }
      
      if (gaugeChartDanger.value) {
        const option = gaugeChartDanger.value.getOption()
        option.series[0].data[0].value = dangerPercentage.value
        gaugeChartDanger.value.setOption(option)
      }
    }
    
    const updateNetworkDelay = () => {
      networkDelay.value = Math.floor(Math.random() * 30) + 20
    }
    
    const getTempClass = (temp) => {
      if (temp < 70) return 'stat-normal'
      if (temp < 85) return 'stat-warning'
      return 'stat-danger'
    }
    
    const getTempStatus = (temp) => {
      if (temp < 70) return '正常'
      if (temp < 85) return '预警'
      return '异常'
    }
    
    const handleResize = () => {
      const charts = [
        predictionChart.value,
        surface3dChart.value,
        gaugeChartNormal.value,
        gaugeChartWarning.value,
        gaugeChartDanger.value
      ]
      
      charts.forEach(chart => {
        if (chart) {
          chart.resize()
        }
      })
      
      if (sceneContainer.value) {
        const canvas = sceneContainer.value.querySelector('canvas')
        if (canvas) {
          canvas.width = sceneContainer.value.clientWidth
          canvas.height = sceneContainer.value.clientHeight
        }
      }
    }

    // 生命周期
    onMounted(() => {
      updateTime()
      setInterval(updateTime, 1000)
      
      // 从后端获取设备数据
	  fetchDeviceData().then(() => {
		// 设备数据加载完成后初始化3D场景
		nextTick(() => {
		  setTimeout(() => {
			initCharts()
			init3DScene()
			if (sceneContainer.value) {
			  sceneContainer.value.addEventListener('click', handle3DDeviceClick)
			}
		  }, 500)
		})
	  })
      initAlerts() // 初始化告警数据
      startDeviceCarousel()
      startAlertCarousel()
      
	  // 获取设备状态统计
	  fetchDeviceStatusSummary()
	    
	  
      // 初始化天气数据
      fetchWeatherData()
      setInterval(fetchWeatherData, 600000) // 每10分钟更新一次天气数据
      
      nextTick(() => {
        setTimeout(() => {
          initCharts()
          init3DScene()
          if (sceneContainer.value) {
            sceneContainer.value.addEventListener('click', handle3DDeviceClick)
          }
        }, 500)
      })
      
      setInterval(updateDeviceData, 3000)
      setInterval(updateNetworkDelay, 5000)
      // 设置定时更新设备状态统计（每5秒更新一次）
      setInterval(fetchDeviceStatusSummary, 5000)
	  
      lastUpdate.value = new Date().toLocaleTimeString()
      
      window.addEventListener('resize', handleResize)
    })

    onBeforeUnmount(() => {
      const charts = [
        predictionChart.value,
        surface3dChart.value,
        gaugeChartNormal.value,
        gaugeChartWarning.value,
        gaugeChartDanger.value
      ]
      
      charts.forEach(chart => {
        if (chart) {
          chart.dispose()
        }
      })
      
      if (deviceCarouselInterval.value) {
        clearInterval(deviceCarouselInterval.value)
      }
      
      if (alertCarouselInterval.value) {
        clearInterval(alertCarouselInterval.value)
      }
      
      if (animationId) {
        cancelAnimationFrame(animationId)
      }
      
      if (sceneContainer.value) {
        sceneContainer.value.removeEventListener('click', handle3DDeviceClick)
      }
      
      window.removeEventListener('resize', handleResize)
    })

    return {
      currentTime,
      devices,
      selectedDevice,
      recentAlerts,
      networkDelay,
      lastUpdate,
      onlineDevices,
      totalDevices,
      currentAlert,
      deviceScrollOffset,
      alertScrollOffset,
      normalDevices,
      warningDevices,
      dangerDevices,
      normalPercentage,
      warningPercentage,
      dangerPercentage,
      getTempClass,
      getTempStatus,
      gaugeChartNormalRef,
      gaugeChartWarningRef,
      gaugeChartDangerRef,
      predictionChartRef,
      surface3dChartRef,
      sceneContainer,
      loading3D,
      autoRotate,
      deviceCount,
      weatherData,
      weatherLoading,
      weatherError,
      weatherIcon,
      fetchWeatherData,
      toggleAutoRotate,
      resetCamera,
      acknowledgeAlert,
      getAlertStats,
	  deviceStats,
	  fetchDeviceStatusSummary,
      // 新增的返回数据
      temperatureHistory,
      historyLoading,
      fetchTemperatureHistory
    }
  }
}
</script>

<!-- 样式部分保持不变 -->
<style scoped>
/* 样式部分保持不变，与之前相同 */
/* 设置根字体大小，基于屏幕宽度 */
:root {
  font-size: 14px; /* 设置基准字体大小 */
}

/* 告警模块样式 */
.alert-stats {
  display: flex;
  gap: 0.8rem;
  margin-left: auto;
  font-size: 0.8rem;
}

.stat-danger {
  color: #ff4d4d;
  font-weight: bold;
}

.stat-warning {
  color: #ffcc00;
  font-weight: bold;
}

.no-alerts {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 6rem;
  color: #a0c8ff;
}

.no-alerts-icon {
  font-size: 1.5rem;
  margin-bottom: 0.5rem;
}

.no-alerts-text {
  font-size: 0.9rem;
}

.alert-item {
  display: flex;
  flex-direction: column;
  padding: 0.5rem;
  border-radius: 0.4rem;
  margin-bottom: 0.25rem;
  transition: all 0.3s ease;
  height: 3.8rem;
  cursor: pointer;
  position: relative;
  border-left: 0.25rem solid;
}

.alert-item.alert-danger {
  background: rgba(255, 77, 77, 0.15);
  border-color: #ff4d4d;
  border-left-color: #ff4d4d;
}

.alert-item.alert-warning {
  background: rgba(255, 204, 0, 0.15);
  border-color: #ffcc00;
  border-left-color: #ffcc00;
}

.alert-item.alert-info {
  background: rgba(79, 172, 254, 0.15);
  border-color: #4facfe;
  border-left-color: #4facfe;
}

.alert-item.alert-acknowledged {
  opacity: 0.6;
}

.alert-item.active {
  box-shadow: 0 0 0.6rem rgba(79, 172, 254, 0.3);
}

.alert-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 0.25rem;
}

.alert-time {
  color: #a0c8ff;
  font-size: 0.7rem;
}

.alert-level {
  font-size: 0.7rem;
  padding: 0.15rem 0.4rem;
  border-radius: 0.2rem;
  font-weight: bold;
}

.alert-level.danger {
  background: #ff4d4d;
  color: white;
}

.alert-level.warning {
  background: #ffcc00;
  color: #333;
}

.alert-level.info {
  background: #4facfe;
  color: white;
}

.alert-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex: 1;
}

.alert-device {
  font-weight: bold;
  font-size: 0.8rem;
  color: #fff;
}

.alert-message {
  flex: 1;
  font-size: 0.7rem;
  margin: 0 0.5rem;
  color: #fff;
}

.alert-temp {
  font-weight: bold;
  font-size: 0.8rem;
  width: 3rem;
  text-align: right;
}

.alert-acknowledged-badge {
  position: absolute;
  top: 0.25rem;
  right: 0.25rem;
  background: #00ff9d;
  color: #333;
  font-size: 0.6rem;
  padding: 0.1rem 0.3rem;
  border-radius: 0.2rem;
  font-weight: bold;
}

/* 调整告警容器高度 */
.alerts-container {
  height: 9.5rem;
  overflow: hidden;
  position: relative;
}

.alerts-scroll-wrapper {
  transition: transform 0.5s ease;
}

/* 天气预报模块样式 */
.weather-widget {
  position: absolute;
  top: 0.8rem;
  left: 0.8rem;
  background: rgba(16, 35, 70, 0.95);
  border: 0.06rem solid rgba(79, 172, 254, 0.5);
  border-radius: 0.5rem;
  padding: 0.5rem;
  z-index: 10;
  min-width: 10rem;
  backdrop-filter: blur(0.3rem);
  box-shadow: 0 0 1.3rem rgba(0, 100, 255, 0.4);
}

.weather-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 0.25rem;
  padding-bottom: 0.25rem;
  border-bottom: 0.06rem solid rgba(79, 172, 254, 0.3);
}

.weather-icon {
  font-size: 0.9rem;
  margin-right: 0.25rem;
}

.weather-title {
  color: #4facfe;
  font-size: 0.9rem;
  font-weight: bold;
  flex: 1;
}

.refresh-btn {
  background: rgba(79, 172, 254, 0.3);
  border: 0.06rem solid rgba(79, 172, 254, 0.5);
  color: #a0c8ff;
  padding: 0.1rem 0.1rem;
  border-radius: 0.25rem;
  cursor: pointer;
  font-size: 1rem;
  transition: all 0.3s ease;
}

.refresh-btn:hover:not(:disabled) {
  background: rgba(79, 172, 254, 0.5);
  border-color: #4facfe;
}

.refresh-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.weather-content {
  display: flex;
  flex-direction: column;
  gap: 0.4rem;
}

.weather-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0.2rem 0;
}

.weather-label {
  color: #a0c8ff;
  font-size: 0.8rem;
  font-weight: normal;
}

.weather-value {
  color: #ffffff;
  font-size: 0.8rem;
  font-weight: bold;
}

.weather-error {
  color: #ff6b6b;
  font-size: 0.7rem;
  margin-top: 0.5rem;
  padding: 0.25rem;
  background: rgba(255, 77, 77, 0.1);
  border-radius: 0.25rem;
  text-align: center;
}

/* 3D可视化样式 */
.factory-pipeline-3d {
  width: 100%;
  height: 100%;
  position: relative;
  background: radial-gradient(ellipse at center, #0a1a35 0%, #050d1a 100%);
  border-radius: 0.5rem;
  overflow: hidden;
}

.scene-container {
  width: 100%;
  height: 100%;
  cursor: pointer;
  position: relative;
}

.factory-map {
  width: 100%;
  height: 100%;
  object-fit: cover;
  position: absolute;
  top: 0;
  left: 0;
  z-index: 1;
  opacity: 0.7;
}

.control-panel {
  position: absolute;
  top: 0.8rem;
  right: 0.8rem;
  display: flex;
  flex-direction: column;
  gap: 0.3rem;
  z-index: 10;
}

.control-btn {
  background: rgba(16, 35, 70, 0.8);
  border: 0.06rem solid rgba(79, 172, 254, 0.5);
  color: #a0c8ff;
  padding: 0.3rem 0.6rem;
  border-radius: 0.25rem;
  cursor: pointer;
  font-size: 0.8rem;
  transition: all 0.3s ease;
}

.control-btn:hover {
  background: rgba(79, 172, 254, 0.3);
  border-color: #4facfe;
}

.device-count {
  background: rgba(16, 35, 70, 0.8);
  border: 0.06rem solid rgba(79, 172, 254, 0.5);
  color: #a0c8ff;
  padding: 0.3rem 0.6rem;
  border-radius: 0.25rem;
  font-size: 0.8rem;
  text-align: center;
}

.loading-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  background: rgba(10, 26, 53, 0.8);
  z-index: 10;
}

.loading-spinner {
  width: 2.5rem;
  height: 2.5rem;
  border: 0.25rem solid rgba(79, 172, 254, 0.3);
  border-top: 0.25rem solid #4facfe;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin-bottom: 1rem;
}

.loading-text {
  color: #a0c8ff;
  font-size: 1rem;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

/* 原有样式保持不变 */
.device-detail-panel {
  min-height: 8rem;
  padding: 0.5rem;
}

.device-detail-content {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.detail-info {
  display: flex;
  flex-direction: column;
  gap: 0.25rem;
}

.info-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0.15rem 0;
  border-bottom: 0.06rem solid rgba(64, 128, 255, 0.2);
}

.info-label {
  color: #a0c8ff;
  font-size: 0.7rem;
  font-weight: bold;
}

.info-value {
  font-size: 0.7rem;
  color: #fff;
}

.status-overview-container {
  height: 10rem;
  overflow: hidden;
  position: relative;
}

.status-scroll-wrapper {
  transition: transform 0.5s ease;
}

.status-item {
  display: flex;
  justify-content: space-between;
  padding: 0.5rem;
  border-radius: 0.4rem;
  background: rgba(16, 35, 70, 0.5);
  border: 0.06rem solid rgba(64, 128, 255, 0.2);
  margin-bottom: 0.25rem;
  transition: all 0.3s ease;
  height: 2.5rem;
  align-items: center;
}

.status-item.active {
  background: rgba(79, 172, 254, 0.2);
  border-color: #4facfe;
  box-shadow: 0 0 0.6rem rgba(79, 172, 254, 0.3);
}

.status-device {
  font-weight: bold;
  width: 4.5rem;
  font-size: 0.8rem;
}

.status-temp {
  font-weight: bold;
  width: 3rem;
  text-align: right;
  font-size: 0.8rem;
}

.status-location {
  flex: 1;
  text-align: right;
  font-size: 0.7rem;
  color: #a0c8ff;
}

* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
  font-family: "Microsoft YaHei", sans-serif;
}

body {
  background-color: #0a1a35;
  color: #fff;
}

.dashboard {
  color: #ffffff !important;
  width: 100vw;
  height: 100vh;
  padding: 1rem;
  display: flex;
  flex-direction: column;
  background: radial-gradient(ellipse at center, #0a1a35 0%, #050d1a 100%);
  position: relative;
  overflow: hidden;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0.5rem 1rem;
  background: rgba(16, 35, 70, 0.7);
  border-radius: 0.5rem;
  margin-bottom: 0.8rem;
  box-shadow: 0 0 1rem rgba(0, 100, 255, 0.3);
  border: 0.06rem solid rgba(64, 128, 255, 0.2);
  z-index: 10;
  height: 3.2rem;
}

.header-left {
  flex: 1;
  display: flex;
  justify-content: flex-start;
}

.header-right {
  flex: 1;
  display: flex;
  justify-content: flex-end;
}

.title {
  font-size: 1.4rem;
  font-weight: bold;
  background: linear-gradient(90deg, #4facfe, #00f2fe);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  text-shadow: 0 0 0.5rem rgba(0, 242, 254, 0.5);
  text-align: center;
  flex: 1;
}

.update-info {
  display: flex;
  flex-direction: column;
  gap: 0.3rem;
}

.update-time, .device-count {
  color: #a0c8ff;
  font-size: 0.8rem;
  background: rgba(16, 35, 70, 0.5);
  padding: 0.2rem 0.5rem;
  border-radius: 0.2rem;
  border: 0.06rem solid rgba(64, 128, 255, 0.3);
}

.time {
  font-size: 0.9rem;
  color: #4facfe;
  background: rgba(16, 35, 70, 0.5);
  padding: 0.25rem 0.6rem;
  border-radius: 0.2rem;
  border: 0.06rem solid rgba(64, 128, 255, 0.3);
}

.main-content {
  display: flex;
  flex: 1.2;
  gap: 1rem;
  z-index: 5;
  min-height: 0;
}

.left-panel, .right-panel {
  flex: 0.8;
  display: flex;
  flex-direction: column;
  gap: 1rem;
  min-width: 0;
}

.left-panel {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.center-panel {
  flex: 2;
  display: flex;
  flex-direction: column;
  gap: 1rem;
  min-width: 0;
}

.panel {
  background: rgba(16, 35, 70, 0.7);
  border-radius: 0.6rem;
  padding: 0.8rem;
  box-shadow: 0 0 1.3rem rgba(0, 100, 255, 0.2);
  border: 0.06rem solid rgba(64, 128, 255, 0.2);
  position: relative;
  overflow: hidden;
  display: flex;
  flex-direction: column;
}

.panel::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 0.2rem;
  background: linear-gradient(90deg, #4facfe, #00f2fe);
}

.panel-title {
  font-size: 1rem;
  margin-bottom: 0.8rem;
  color: #4facfe;
  display: flex;
  align-items: center;
}

.panel-title i {
  margin-right: 0.4rem;
  font-size: 1.1rem;
}

.chart-container {
  flex: 1;
  min-height: 12.5rem;
  position: relative;
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
}

.prediction-chart {
  width: 100%;
  height: 100%;
  min-height: 12.5rem;
  flex: 1;
  min-height: 0;
  margin: 0 auto;
}

.surface-3d-chart {
  width: 100%;
  height: 100%;
  min-height: 12.5rem;
  flex: 1;
  min-height: 0;
  margin: 0 auto;
}

.device-map-container {
  flex: 1;
  position: relative;
  min-height: 0;
  width: 100%;
}

.stats-container {
  display: flex;
  justify-content: space-between;
  margin-top: 0.6rem;
}

.stat-item {
  text-align: center;
  flex: 1;
}

.stat-value {
  font-size: 1.3rem;
  font-weight: bold;
  margin: 0.3rem 0;
}

.stat-normal {
  color: #00ff9d;
}

.stat-warning {
  color: #ffcc00;
}

.stat-danger {
  color: #ff4d4d;
}

.stat-label {
  font-size: 0.8rem;
  color: #a0c8ff;
}

.system-status .status-item{
  display: flex;
  justify-content: space-between;
  padding: 0.15rem 0;
  height: 1.3rem;
  align-items: center;
}

.status-label {
  color: #a0c8ff;
  font-size: 0.8rem;
}

.status-value.normal {
  color: #00ff9d;
  font-size: 0.8rem;
}

.grid-line {
  position: absolute;
  background: rgba(64, 128, 255, 0.1);
}

.grid-line.horizontal {
  width: 100%;
  height: 0.06rem;
  left: 0;
}

.grid-line.vertical {
  width: 0.06rem;
  height: 100%;
  top: 0;
}

.data-stream {
  position: absolute;
  width: 0.13rem;
  height: 6.3rem;
  background: linear-gradient(to bottom, transparent, #4facfe, transparent);
  opacity: 0.5;
  animation: streamFlow 3s linear infinite;
}

@keyframes streamFlow {
  0% {
    transform: translateY(-6.3rem);
  }
  100% {
    transform: translateY(100vh);
  }
}

.overview-panel {
  margin-top: auto;
  height: 7.5rem;
}

.ring-chart-container {
  display: flex;
  flex-direction: column;
  height: 11.3rem;
}

.ring-chart {
  flex: 1;
  min-height: 0;
}

.ring-legend {
  display: flex;
  justify-content: space-around;
  margin-top: 0.6rem;
  padding-top: 0.6rem;
  border-top: 0.06rem solid rgba(64, 128, 255, 0.2);
}

.legend-item {
  display: flex;
  align-items: center;
  font-size: 0.8rem;
}

.legend-color {
  width: 0.8rem;
  height: 0.8rem;
  border-radius: 50%;
  margin-right: 0.3rem;
}

.legend-normal {
  background-color: #00ff9d;
}

.legend-warning {
  background-color: #ffcc00;
}

.legend-danger {
  background-color: #ff4d4d;
}

.ring-gauge-container {
  display: flex;
  justify-content: space-around;
  align-items: center;
  height: 100%;
  padding: 0.6rem 0;
}

.ring-gauge-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  width: 30%;
}

.ring-gauge-chart {
  width: 5rem;
  height: 5rem;
}

.ring-gauge-label {
  margin-top: 0.6rem;
  font-size: 0.8rem;
  text-align: center;
}

.ring-gauge-title {
  font-size: 0.9rem;
  font-weight: bold;
  margin-bottom: 0.3rem;
  color: #4facfe;
}

.ring-gauge-value {
  font-size: 1rem;
  font-weight: bold;
  margin-top: 0.3rem;
}

.loading-indicator {
  margin-left: auto;
  display: flex;
  align-items: center;
}

.loading-text {
  color: #ffcc00;
  font-size: 0.8rem;
  animation: pulse 1.5s infinite;
}

@keyframes pulse {
  0% { opacity: 0.6; }
  50% { opacity: 1; }
  100% { opacity: 0.6; }
}

.no-data-prompt {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  text-align: center;
  color: #a0c8ff;
}

.no-data-icon {
  font-size: 2rem;
  margin-bottom: 0.5rem;
}

.no-data-text {
  font-size: 0.9rem;
}
</style>