<template>
  <div class="virtual-lab">
    <!-- 背景效果 -->
    <div class="background-effects">
      <div class="grid-lines"></div>
      <div class="floating-elements">
        <div class="floating-element" v-for="i in 12" :key="i" :style="floatingStyle(i)"></div>
      </div>
    </div>
    
    <!-- 连接状态指示器 -->
    <div class="connection-status" :class="connectionStatus">
      <div class="status-dot"></div>
      <span>{{ connectionText }}</span>
    </div>
    
    <!-- 左侧区域 -->
    <div class="left-panel">
      <div class="panel-header">
        <h2>工业仪器监控</h2>
        <div class="status-indicator">
          <div class="pulse"></div>
          <span>实时监控</span>
        </div>
      </div>
      
      <!-- 五个核心变量 -->
      <div class="core-variables">
        <h3>核心参数</h3>
        <div class="variable-list">
          <div class="variable-item" v-for="(variable, index) in instrumentVariables" :key="index">
            <div class="variable-header">
              <div class="variable-icon">
                <div class="icon-bg"></div>
                <span class="icon">{{ getVariableIcon(variable.name) }}</span>
              </div>
              <div class="variable-info">
                <div class="variable-name">{{ variable.name }}</div>
                <div class="variable-value">{{ variable.value }}{{ variable.unit }}</div>
              </div>
              <div class="variable-status" :class="variable.status">
                <div class="status-dot"></div>
                <span>{{ variable.statusText }}</span>
              </div>
            </div>
            <div class="variable-trend">
              <div class="trend-chart">
                <div class="trend-point" 
                     v-for="(point, pIndex) in variable.history" 
                     :key="pIndex"
                     :style="{ height: point + '%' }"></div>
              </div>
            </div>
          </div>
        </div>
      </div>
      
      <!-- 温度趋势图 -->
      <div class="temperature-trend">
        <h3>温度趋势分析</h3>
        <div class="trend-chart-container">
          <div class="chart-area">
            <div class="chart-grid">
              <div class="grid-line" v-for="i in 5" :key="i"></div>
            </div>
            <div class="chart-line">
              <svg width="100%" height="100%" viewBox="0 0 100 100" preserveAspectRatio="none">
                <path :d="temperatureCurve" fill="none" stroke="url(#temperatureGradient)" stroke-width="2" />
              </svg>
              <defs>
                <linearGradient id="temperatureGradient" x1="0%" y1="0%" x2="100%" y2="0%">
                  <stop offset="0%" stop-color="#64ff8f" />
                  <stop offset="100%" stop-color="#ff4164" />
                </linearGradient>
              </defs>
            </div>
            <div class="chart-points">
              <div class="data-point" v-for="(point, index) in temperatureData" :key="index"
                   :style="{ left: (index * 20) + '%', bottom: (point.value / 2) + '%' }">
                <div class="point-glow"></div>
                <span class="point-value">{{ point.value }}°C</span>
              </div>
            </div>
          </div>
          <div class="chart-labels">
            <span v-for="(label, index) in timeLabels" :key="index">{{ label }}</span>
          </div>
        </div>
      </div>
      
      <!-- 参数分布 -->
      <div class="parameter-distribution">
        <h3>参数分布</h3>
        <div class="distribution-chart">
          <div class="distribution-bar" v-for="(param, index) in distributionData" :key="index">
            <div class="bar-label">{{ param.name }}</div>
            <div class="bar-container">
              <div class="bar-fill" :style="{ width: param.value + '%' }"></div>
              <span class="bar-value">{{ param.value }}%</span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 中间区域 -->
    <div class="center-panel">
      <div class="panel-header">
        <h2>高级3D工业测温实验室</h2>
        <div class="control-buttons">
          <button class="control-btn" @click="zoomIn">
            <span class="btn-icon">+</span>
          </button>
          <button class="control-btn" @click="zoomOut">
            <span class="btn-icon">−</span>
          </button>
          <button class="control-btn" @click="resetView">
            <span class="btn-icon">↺</span>
          </button>
          <div class="zoom-level">缩放: {{ (zoomLevel * 100).toFixed(0) }}%</div>
        </div>
      </div>
      
      <!-- 高级3D工厂模型 -->
      <div class="model-container" @wheel="handleWheel" :style="{ transform: `scale(${zoomLevel})` }">
        <div class="advanced-factory-3d">
          <!-- 地面和基础 -->
          <div class="factory-foundation">
            <div class="concrete-base"></div>
            <div class="ground-texture"></div>
            <div class="access-roads">
              <div class="road" v-for="i in 3" :key="i"></div>
            </div>
          </div>
          
          <!-- 主生产厂房 -->
          <div class="main-production-plant">
            <div class="plant-structure">
              <!-- 钢结构框架 -->
              <div class="steel-frame">
                <div class="frame-column" v-for="col in 8" :key="'col'+col"></div>
                <div class="frame-beam" v-for="beam in 6" :key="'beam'+beam"></div>
              </div>
              
              <!-- 外墙系统 -->
              <div class="facade-system">
                <div class="wall-panel" v-for="panel in 12" :key="'panel'+panel"
                     :style="{ '--temp-effect': variables.temperature / 100 }"></div>
                <div class="insulation-layer" :style="{ opacity: 0.3 + variables.temperature / 200 }"></div>
              </div>
              
              <!-- 屋顶系统 -->
              <div class="roof-complex">
                <div class="roof-truss">
                  <div class="truss-member" v-for="i in 16" :key="'truss'+i"></div>
                </div>
                <div class="skylight-system">
                  <div class="skylight" v-for="i in 4" :key="'sky'+i"></div>
                </div>
                <div class="roof-ventilation-system">
                  <div class="industrial-fan" :style="{ transform: `rotate(${variables.turbine * 7.2}deg)` }">
                    <div class="fan-housing"></div>
                    <div class="fan-blades">
                      <div class="blade" v-for="i in 8" :key="'blade'+i"></div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
            
            <!-- 生产设备 -->
            <div class="production-equipment">
              <!-- 反应釜系统 -->
              <div class="reactor-system">
                <div class="reactor-vessel" :style="{ '--pressure-level': variables.pressure / 120 }">
                  <div class="vessel-shell">
                    <div class="internal-agitation" :style="{ transform: `rotate(${variables.turbine * 3.6}deg)` }">
                      <div class="agitator-blade" v-for="i in 6" :key="'agitator'+i"></div>
                    </div>
                    <div class="reaction-chamber" :style="{ background: temperatureGradient }">
                      <div class="chemical-reaction">
                        <div class="reaction-bubble" v-for="i in 15" :key="'bubble'+i" 
                             :style="bubbleStyle(i)"></div>
                      </div>
                    </div>
                  </div>
                  <div class="heating-jacket" :style="{ opacity: variables.temperature / 100 }">
                    <div class="heat-element" v-for="i in 8" :key="'heat'+i"></div>
                  </div>
                </div>
                <div class="reactor-controls">
                  <div class="control-panel">
                    <div class="indicator-light" :class="getPressureStatus"></div>
                    <div class="pressure-readout">{{ variables.pressure }} kPa</div>
                  </div>
                </div>
              </div>
              
              <!-- 蒸馏塔系统 -->
              <div class="distillation-system">
                <div class="distillation-column">
                  <div class="column-trays">
                    <div class="tray" v-for="i in 10" :key="'tray'+i"
                         :style="{ opacity: 0.3 + (i * variables.temperature / 1000) }"></div>
                  </div>
                  <div class="temperature-gradient" :style="{ background: temperatureGradient }"></div>
                  <div class="vapor-flow" :style="{ '--flow-intensity': variables.temperature / 100 }">
                    <div class="vapor-particle" v-for="i in 20" :key="'vapor'+i" 
                         :style="vaporParticleStyle(i)"></div>
                  </div>
                </div>
                <div class="condenser-unit">
                  <div class="condenser-tubes">
                    <div class="cooling-tube" v-for="i in 12" :key="'tube'+i"
                         :style="{ opacity: 0.4 + variables.humidity / 250 }"></div>
                  </div>
                </div>
              </div>
            </div>
          </div>
          
          <!-- 能源中心 -->
          <div class="energy-center">
            <div class="turbine-hall">
              <div class="power-turbine" :style="{ transform: `rotate(${variables.turbine * 7.2}deg)` }">
                <div class="turbine-rotor">
                  <div class="rotor-blade" v-for="i in 12" :key="'rotor'+i"></div>
                </div>
                <div class="turbine-casing">
                  <div class="steam-inlet" :style="{ '--steam-flow': variables.turbine / 100 }">
                    <div class="steam-jet" v-for="i in 8" :key="'steam'+i" 
                         :style="steamJetStyle(i)"></div>
                  </div>
                </div>
              </div>
              <div class="generator-unit">
                <div class="generator-stator"></div>
                <div class="generator-rotor" :style="{ transform: `rotate(${variables.turbine * 7.2}deg)` }"></div>
              </div>
            </div>
            
            <!-- 热交换系统 -->
            <div class="heat-exchange-system">
              <div class="heat-exchanger">
                <div class="exchanger-tubes">
                  <div class="tube-bundle">
                    <div class="heat-tube" v-for="i in 24" :key="'heat-tube'+i"
                         :style="{ '--temp-color': `hsl(${200 - variables.temperature * 1.5}, 70%, 50%)` }"></div>
                  </div>
                </div>
                <div class="thermal-fluid" :style="{ opacity: variables.temperature / 100 }"></div>
              </div>
            </div>
          </div>
          
          <!-- 环境控制系统 -->
          <div class="environmental-control">
            <div class="humidity-control-tower">
              <div class="humidification-system" :style="{ '--humidity-level': variables.humidity / 100 }">
                <div class="water-spray">
                  <div class="water-droplet" v-for="i in 25" :key="'droplet'+i"
                       :style="dropletStyle(i)"></div>
                </div>
                <div class="air-flow">
                  <div class="air-stream" v-for="i in 12" :key="'air'+i"
                       :style="{ opacity: 0.2 + variables.humidity / 200 }"></div>
                </div>
              </div>
            </div>
            
            <div class="pressure-regulation">
              <div class="pressure-vessels">
                <div class="pressure-tank" v-for="i in 3" :key="'ptank'+i"
                     :style="{ '--pressure-fill': (variables.pressure - 80) / 40 }">
                  <div class="tank-pressure"></div>
                  <div class="safety-valve" :class="{ 'active': variables.pressure > 110 }">
                    <div class="valve-release" v-if="variables.pressure > 110"></div>
                  </div>
                </div>
              </div>
            </div>
          </div>
          
          <!-- 管道网络系统 -->
          <div class="advanced-piping-network">
            <div class="main-process-lines">
              <div class="process-pipe" v-for="pipe in 6" :key="'pipe'+pipe"
                   :style="{ '--flow-velocity': variables.turbine / 80 }">
                <div class="pipe-section">
                  <div class="fluid-indicator"></div>
                  <div class="flow-animation"></div>
                </div>
                <div class="pipe-valves">
                  <div class="control-valve" :style="{ transform: `rotate(${variables.pressure * 0.9}deg)` }"></div>
                </div>
              </div>
            </div>
            
            <div class="utility-lines">
              <div class="utility-pipe" v-for="util in 4" :key="'util'+util">
                <div class="insulation-layer" :style="{ opacity: 0.3 + variables.temperature / 200 }"></div>
              </div>
            </div>
          </div>
          
          <!-- 监测和控制系统 -->
          <div class="monitoring-system">
            <!-- 热成像监测 -->
            <div class="thermal-imaging-overlay">
              <div class="thermal-hotspot" v-for="hotspot in thermalHotspots" :key="hotspot.id"
                   :style="{
                     left: hotspot.x + '%',
                     top: hotspot.y + '%',
                     '--thermal-intensity': hotspot.intensity,
                     '--thermal-color': `hsl(${30 - hotspot.intensity * 30}, 100%, 50%)`
                   }"></div>
            </div>
            
            <!-- 传感器网络 -->
            <div class="sensor-array">
              <div class="sensor-cluster" v-for="cluster in sensorClusters" :key="cluster.id"
                   :style="{ left: cluster.x + '%', top: cluster.y + '%' }">
                <div class="sensor-node" :class="cluster.status">
                  <div class="sensor-probe"></div>
                  <div class="data-transmission">
                    <div class="data-packet" v-for="i in 3" :key="'packet'+i"></div>
                  </div>
                </div>
                <div class="sensor-reading">
                  {{ cluster.temperature }}°C
                  <div class="humidity-indicator" :style="{ width: cluster.humidity + '%' }"></div>
                </div>
              </div>
            </div>
            
            <!-- 环境参数可视化 -->
            <div class="environmental-visualization">
              <div class="temperature-field" :style="{ opacity: variables.temperature / 150 }"></div>
              <div class="humidity-mist" :style="{ opacity: variables.humidity / 120 }">
                <div class="mist-particle" v-for="i in 18" :key="'mist'+i"></div>
              </div>
              <div class="pressure-wave" :style="{ '--pressure-wave': variables.pressure / 120 }">
                <div class="wave-front" v-for="i in 3" :key="'wave'+i"></div>
              </div>
            </div>
          </div>
          
          <!-- 动态照明系统 -->
          <div class="dynamic-lighting">
            <div class="safety-lighting">
              <div class="emergency-light" v-for="i in 8" :key="'safety'+i"></div>
            </div>
            <div class="process-lighting" :style="{ opacity: 0.3 + variables.turbine / 200 }">
              <div class="work-light" v-for="i in 12" :key="'work'+i"></div>
            </div>
            <div class="status-lighting">
              <div class="status-indicator" :class="getSystemStatus"></div>
            </div>
          </div>
        </div>
      </div>
      
      <!-- 启动按钮 -->
      <div class="action-section">
        <button class="start-button" @click="toggleExperiment" :class="{ active: isRunning }">
          <div class="button-glow"></div>
          <span class="button-text">{{ isRunning ? '停止实验' : '启动实验' }}</span>
          <div class="button-pulse" v-if="isRunning"></div>
        </button>
      </div>
      
      <!-- 变量控制 -->
      <div class="variable-controls">
        <h3>环境参数调节</h3>
        <div class="control-grid">
          <div class="control-item" v-for="(control, key) in variableControls" :key="key">
            <div class="control-header">
              <div class="control-icon">{{ getControlIcon(key) }}</div>
              <div class="control-info">
                <label>{{ control.label }}</label>
                <span class="control-value">{{ variables[key] }}{{ control.unit }}</span>
              </div>
            </div>
            <div class="slider-container">
              <input 
                type="range" 
                :min="control.min" 
                :max="control.max" 
                :step="control.step"
                v-model.number="variables[key]"
                class="slider"
                :style="{'--value': (variables[key] - control.min) / (control.max - control.min) * 100 + '%'}"
                @input="onParameterChange(key)"
              >
              <div class="slider-track"></div>
              <div class="slider-fill"></div>
              <div class="slider-glow"></div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 右侧区域 - 模型测试台 -->
    <div class="right-panel model-testing-panel">
      <div class="panel-header">
        <h2><i class="fa-solid fa-network-wired mr-2 text-purple-400"></i>模型测试台</h2>
        <span class="text-[10px] bg-slate-800 text-slate-400 px-2 py-0.5 rounded border border-slate-700" id="model-count">Ready</span>
      </div>
      
      <!-- 仿真器控制 -->
      <div class="simulator-section p-4 border-b border-slate-700">
        <h3 class="text-sm font-bold text-slate-300 mb-3">仿真环境控制</h3>
        <div class="grid grid-cols-2 gap-3 mb-4" id="controls-container"></div>
        <div class="bg-slate-800/50 rounded-lg p-3">
          <h4 class="text-xs font-bold text-blue-400 mb-2">传感器实时反馈</h4>
          <div class="space-y-2" id="sensors-container"></div>
        </div>
      </div>
      
      <!-- 模型列表 -->
      <div class="flex-1 overflow-y-auto p-4 space-y-4" id="models-container">
        <div id="empty-state" class="flex flex-col items-center justify-center h-64 text-slate-600 border-2 border-dashed border-slate-800 rounded-xl">
          <i class="fa-solid fa-microchip text-3xl mb-3"></i>
          <p class="text-sm">暂无挂载模型</p>
        </div>
      </div>
      
      <!-- 挂载按钮 -->
      <div class="p-4 border-t border-slate-800 bg-slate-900">
        <button @click="openModal" class="w-full dashed-box h-10 rounded text-sm text-slate-400 hover:text-white hover:bg-slate-800 transition group flex items-center justify-center">
          <i class="fa-solid fa-plus mr-2"></i> 挂载新模型
        </button>
      </div>

      <!-- 模态框 -->
      <div v-if="showModal" class="modal-overlay" @click="closeModal">
        <div class="modal-content" :style="modalStyle" @click.stop>
          <div class="modal-header">
            <h3 class="text-md font-bold text-white">挂载预测模型</h3>
            <button @click="closeModal" class="text-slate-400 hover:text-white">
              <i class="fa-solid fa-xmark"></i>
            </button>
          </div>

          <div class="modal-body">
            <div class="grid grid-cols-2 gap-4">
              <div>
                <label class="block text-xs font-bold text-slate-400 mb-1">模型名称</label>
                <input type="text" v-model="modalData.name" class="w-full bg-slate-900 border border-slate-600 rounded p-2 text-white text-sm">
              </div>
              <div>
                <label class="block text-xs font-bold text-slate-400 mb-1">预测目标</label>
                <select v-model="modalData.target" class="w-full bg-slate-900 border border-slate-600 rounded p-2 text-white text-sm">
                  <option value="TAT">TAT (出口温度)</option>
                  <option value="TIT">TIT (进口温度)</option>
                </select>
              </div>
            </div>

            <div>
              <label class="block text-xs font-bold text-slate-400 mb-1">模型架构</label>
              <select v-model="modalData.arch" class="w-full bg-slate-900 border border-slate-600 rounded p-2 text-white text-sm">
                <option value="Seq2Seq">Seq2Seq + Attention (推荐)</option>
                <option value="LSTM">Standard LSTM</option>
                <option value="Transformer">Transformer (Lite)</option>
              </select>
            </div>

            <div>
              <label class="block text-xs font-bold text-slate-400 mb-1">预训练权重</label>
              <select v-model="modalData.weightId" @change="onWeightsChange" class="w-full bg-slate-900 border border-slate-600 rounded p-2 text-white text-sm">
                <option v-for="weight in savedWeights" :key="weight.id" :value="weight.id">{{ weight.name }}</option>
              </select>
              <p class="text-[10px] text-slate-500 mt-1">{{ weightDescription }}</p>
            </div>

            <div class="bg-slate-900/50 p-3 rounded border border-slate-700">
              <label class="block text-xs font-bold text-slate-400 mb-2">可见特征</label>
              <div class="flex flex-wrap gap-3 text-xs text-slate-300">
                <label class="flex items-center gap-1">
                  <input type="checkbox" v-model="modalData.features.AT" class="accent-blue-500"> AT
                </label>
                <label class="flex items-center gap-1">
                  <input type="checkbox" v-model="modalData.features.AP" class="accent-blue-500"> AP
                </label>
                <label class="flex items-center gap-1">
                  <input type="checkbox" v-model="modalData.features.AH" class="accent-blue-500"> AH
                </label>
                <label class="flex items-center gap-1">
                  <input type="checkbox" v-model="modalData.features.TEY" class="accent-blue-500"> TEY
                </label>
              </div>
            </div>

            <div class="p-3 border border-slate-700 rounded bg-slate-800 hover:bg-slate-700/50 transition">
              <label class="flex items-center justify-between cursor-pointer">
                <div>
                  <div class="text-xs font-bold text-white">启用在线微调</div>
                  <div class="text-[10px] text-slate-400">开启后将显示训练日志终端</div>
                </div>
                <div class="relative">
                  <input type="checkbox" v-model="modalData.onlineTrain" class="sr-only peer">
                  <div class="w-9 h-5 bg-slate-600 peer-focus:outline-none rounded-full peer peer-checked:after:translate-x-full peer-checked:after:border-white after:content-[''] after:absolute after:top-[2px] after:left-[2px] after:bg-white after:border-gray-300 after:border after:rounded-full after:h-4 after:w-4 after:transition-all peer-checked:bg-blue-600"></div>
                </div>
              </label>
              <div v-if="modalData.onlineTrain" class="mt-3 pt-3 border-t border-slate-600 grid grid-cols-2 gap-3">
                <div>
                  <label class="block text-[10px] text-slate-400 mb-1">Learning Rate</label>
                  <input type="number" v-model="modalData.learningRate" class="w-full bg-slate-900 border border-slate-600 rounded p-1 text-white text-xs">
                </div>
                <div>
                  <label class="block text-[10px] text-slate-400 mb-1">Max Steps</label>
                  <input type="number" v-model="modalData.maxSteps" class="w-full bg-slate-900 border border-slate-600 rounded p-1 text-white text-xs">
                </div>
              </div>
            </div>
          </div>

          <div class="modal-footer">
            <button @click="closeModal" class="px-4 py-1.5 text-xs text-slate-300 hover:bg-slate-800 rounded transition">取消</button>
            <button @click="addModel" class="px-4 py-1.5 text-xs bg-blue-600 hover:bg-blue-500 text-white font-bold rounded shadow-lg shadow-blue-500/30 transition">
              <i class="fa-solid fa-plug mr-1"></i> 挂载运行
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, reactive, onMounted, computed, nextTick, watch } from 'vue'

export default {
  name: 'VirtualLab',
  setup() {
    // ==============================
    // 左侧面板数据
    // ==============================
    
    const instrumentVariables = ref([
      { 
        name: '炉温传感器', 
        value: 245, 
        unit: '°C', 
        status: 'normal', 
        statusText: '正常',
        history: [45, 52, 48, 60, 55, 65, 70, 62]
      },
      { 
        name: '压力传感器', 
        value: 1.2, 
        unit: 'MPa', 
        status: 'normal', 
        statusText: '正常',
        history: [80, 75, 82, 78, 85, 88, 90, 92]
      },
      { 
        name: '湿度传感器', 
        value: 45, 
        unit: '%', 
        status: 'warning', 
        statusText: '偏高',
        history: [30, 35, 40, 38, 42, 45, 48, 45]
      },
      { 
        name: '流量计', 
        value: 120, 
        unit: 'L/min', 
        status: 'normal', 
        statusText: '正常',
        history: [100, 105, 110, 115, 112, 118, 120, 122]
      },
      { 
        name: '涡轮功效', 
        value: 75, 
        unit: '%', 
        status: 'normal', 
        statusText: '正常',
        history: [60, 65, 70, 68, 72, 75, 73, 75]
      }
    ])
    
    const temperatureData = ref([
      { value: 22 }, { value: 26 }, { value: 30 }, { value: 28 }, { value: 32 },
      { value: 35 }, { value: 38 }, { value: 36 }, { value: 40 }, { value: 42 }
    ])
    
    const timeLabels = ref(['09:00', '10:00', '11:00', '12:00', '13:00', '14:00'])
    
    const distributionData = ref([
      { name: '温度', value: 75 },
      { name: '压力', value: 60 },
      { name: '湿度', value: 45 },
      { name: '流量', value: 80 },
      { name: '功率', value: 65 }
    ])
    
    // ==============================
    // 中间面板数据
    // ==============================
    
    const variables = reactive({
      temperature: 65,
      humidity: 45,
      pressure: 101,
      turbine: 75
    })
    
    const variableControls = reactive({
      temperature: { label: '温度', min: 0, max: 100, step: 1, unit: '°C' },
      humidity: { label: '湿度', min: 0, max: 100, step: 1, unit: '%' },
      pressure: { label: '压强', min: 80, max: 120, step: 1, unit: 'kPa' },
      turbine: { label: '涡轮功效', min: 0, max: 100, step: 1, unit: '%' }
    })
    
    const zoomLevel = ref(1)
    const sensorClusters = ref([])
    const thermalHotspots = ref([])
    const isRunning = ref(false)
    
    // ==============================
    // WebSocket 连接管理
    // ==============================
    
    const websocket = ref(null)
    const sessionId = ref(null)
    const connectionStatus = ref('disconnected')
    
    const connectWebSocket = (sId) => {
      connectionStatus.value = 'connecting'
      
      if (websocket.value) {
        websocket.value.close()
      }
      
      const protocol = window.location.protocol === 'https:' ? 'wss:' : 'ws:'
      const host = process.env.NODE_ENV === 'development' ? 'localhost:8080' : window.location.host
      const wsUrl = `${protocol}//${host}/lab/ws/${sId}`
      
      console.log('连接 WebSocket:', wsUrl)
      websocket.value = new WebSocket(wsUrl)
      
      websocket.value.onopen = () => {
        console.log('WebSocket 连接已建立')
        connectionStatus.value = 'connected'
        showMessage('WebSocket 连接成功', 'success')
        
        // 发送连接成功消息
        sendWebSocketMessage({
          type: 'connected',
          session_id: sessionId.value
        })
      }
      
      websocket.value.onmessage = (event) => {
        try {
          const data = JSON.parse(event.data)
          console.log('收到 WebSocket 消息:', data)
          handleWebSocketMessage(data)
        } catch (error) {
          console.error('解析 WebSocket 消息错误:', error)
        }
      }
      
      websocket.value.onclose = (event) => {
        console.log('WebSocket 连接已关闭:', event.code, event.reason)
        connectionStatus.value = 'disconnected'
        if (event.code !== 1000) {
          showMessage('WebSocket 连接断开', 'error')
        }
      }
      
      websocket.value.onerror = (error) => {
        console.error('WebSocket 错误:', error)
        connectionStatus.value = 'error'
        showMessage('WebSocket 连接错误', 'error')
      }
    }
    
    const handleWebSocketMessage = (data) => {
      switch (data.type) {
		 case 'update':  // 新增：处理后端update消息
		      updateFrontendWithBackendData(data)
		      break
        case 'simulation_data':
          updateSimulationData(data.data)
          break
		case 'instrument_data':  // 新增：处理左侧参数数据
		      updateInstrumentVariables(data.data)
		      break
        case 'mounted':
          showMessage(`模型 ${data.model_id} 挂载成功`, 'success')
          break
        case 'unmounted':
          showMessage(`模型 ${data.model_id} 卸载成功`, 'info')
          break
        case 'training_controlled':
          showMessage(`模型训练${data.start ? '开始' : '暂停'}`, 'info')
          break
        case 'weights_exported':
          showMessage(`权重导出成功: ${data.model_id}`, 'success')
          savedWeights.value.push({
            id: data.model_id,
            name: `💾 ${data.model_id} (导出)`,
            desc: '从训练中导出的权重文件'
          })
          break
        case 'connected':
          // WebSocket连接成功后发送当前所有参数
          console.log('WebSocket连接成功，发送当前环境参数')
          Object.keys(variables).forEach(key => {
            sendParameterUpdate(key, variables[key])
          })
          break
        case 'error':
          console.error('后端错误:', data.detail)
          showMessage(`错误: ${data.detail}`, 'error')
          break
        default:
          console.log('未知消息类型:', data)
      }
    }
    // 处理后端数据更新 - 只更新左侧显示，不影响中间面板的手动控制
    const updateFrontendWithBackendData = (backendData) => {
      console.log('收到后端更新数据:', backendData)
      
      if (backendData.predicted_outputs && backendData.current_inputs) {
        const outputs = backendData.predicted_outputs
        const inputs = backendData.current_inputs
        
        // 只更新左侧五个核心参数的显示值
        updateInstrumentVariables({
          furnace_temperature: outputs.TIT,        // 炉温 = 涡轮进口温度
          pressure: outputs.AFDP,                  // 压力 = 空气过滤压差
          humidity: inputs.AH,                     // 湿度 = 环境湿度
          flow_rate: outputs.GTEP,                 // 流量 = 燃气轮机排气压力  
          turbine_efficiency: inputs.TEY           // 涡轮功效 = 涡轮发电量
        })
        
        // 更新温度趋势图（只读显示）
        updateTemperatureTrend(outputs.TAT)
        
        // 更新参数分布图（只读显示）
        updateParameterDistribution({
          temperature: outputs.TIT,
          pressure: outputs.AFDP, 
          humidity: inputs.AH,
          flow: outputs.GTEP,
          power: inputs.TEY
        })
        
        console.log('✅ 左侧显示参数已根据后端数据更新')
      }
    }
	// 更新温度趋势图
	const updateTemperatureTrend = (newTemperature) => {
	  if (newTemperature === undefined) return
	  
	  // 将实际温度转换为适合显示的范围内
	  const displayTemp = Math.max(20, Math.min(45, (newTemperature - 500) / 15))
	  
	  temperatureData.value.push({ value: Math.round(displayTemp) })
	  if (temperatureData.value.length > 10) {
	    temperatureData.value.shift()
	  }
	}
	
	// 更新参数分布
	const updateParameterDistribution = (newData) => {
	  if (!newData) return
	  
	  distributionData.value = [
	    { name: '温度', value: Math.round((newData.temperature - 1000) / 1.2) },
	    { name: '压力', value: Math.round(newData.pressure * 28) },
	    { name: '湿度', value: Math.round(newData.humidity) },
	    { name: '流量', value: Math.round(newData.flow * 3.5) },
	    { name: '功率', value: Math.round((newData.power - 100) * 2.5) }
	  ].map(item => ({
	    ...item,
	    value: Math.max(10, Math.min(95, item.value))
	  }))
	}
    const sendWebSocketMessage = (message) => {
      if (websocket.value && websocket.value.readyState === WebSocket.OPEN) {
        websocket.value.send(JSON.stringify(message))
        console.log('发送 WebSocket 消息:', message)
      } else {
        console.error('WebSocket 未连接，无法发送消息:', message)
        showMessage('WebSocket 未连接', 'error')
      }
    }
    
    // ==============================
    // 环境参数实时同步到后端
    // ==============================
    
    // 监听所有环境参数的变化
    watch(() => variables.temperature, (newVal) => {
      sendParameterUpdate('temperature', newVal)
    })
    
    watch(() => variables.humidity, (newVal) => {
      sendParameterUpdate('humidity', newVal)
    })
    
    watch(() => variables.pressure, (newVal) => {
      sendParameterUpdate('pressure', newVal)
    })
    
    watch(() => variables.turbine, (newVal) => {
      sendParameterUpdate('turbine', newVal)
    })
    
    // 发送参数更新到后端
    const sendParameterUpdate = (parameter, value) => {
      if (!isRunning.value || !websocket.value || websocket.value.readyState !== WebSocket.OPEN) {
        console.log(`WebSocket未连接，无法发送参数更新: ${parameter} = ${value}`)
        return
      }
      
      // 根据后端接口定义发送消息格式
      const message = {
        type: 'parameter_update',
        parameter: parameter,
        value: value,
        session_id: sessionId.value,
        timestamp: Date.now()
      }
      
      sendWebSocketMessage(message)
      console.log(`✅ 发送参数更新: ${parameter} = ${value}`)
    }
	// 新增方法：根据后端数据更新左侧参数
	const updateInstrumentVariables = (backendData) => {
	  instrumentVariables.value.forEach(variable => {
	    if (variable.name === '炉温传感器' && backendData.furnace_temperature !== undefined) {
	      variable.value = backendData.furnace_temperature
	      updateVariableHistory(variable, backendData.furnace_temperature)
	    }
	    if (variable.name === '压力传感器' && backendData.pressure !== undefined) {
	      variable.value = backendData.pressure
	      updateVariableHistory(variable, backendData.pressure)
	    }
	    if (variable.name === '湿度传感器' && backendData.humidity !== undefined) {
	      variable.value = backendData.humidity
	      updateVariableHistory(variable, backendData.humidity)
	    }
	    if (variable.name === '流量计' && backendData.flow_rate !== undefined) {
	      variable.value = backendData.flow_rate
	      updateVariableHistory(variable, backendData.flow_rate)
	    }
	    if (variable.name === '涡轮功效' && backendData.turbine_efficiency !== undefined) {
	      variable.value = backendData.turbine_efficiency
	      updateVariableHistory(variable, backendData.turbine_efficiency)
	    }
	  })
	}
	const updateVariableHistory = (variable, newValue) => {
	  // 将新值转换为适合图表显示的百分比 (假设范围是 0-100)
	  const chartValue = Math.max(10, Math.min(95, (newValue / 300) * 100))
	  variable.history.push(chartValue)
	  if (variable.history.length > 8) {
	    variable.history.shift()
	  }
	}
    
    // ==============================
    // 实验控制
    // ==============================
    
    const toggleExperiment = async () => {
      if (!isRunning.value) {
        await startExperiment()
		clearTimeout(simulationTimeout)
      } else {
        await stopExperiment()
      }
    }
    
    const startExperiment = async () => {
      try {
        console.log('启动实验...')
        
        // 使用当前的环境参数作为初始状态
        const initialState = {
          AT: variables.temperature, // 环境温度
          AP: variables.pressure,    // 环境压力  
          AH: variables.humidity,    // 环境湿度
          TEY: variables.turbine,    // 涡轮功效
          TIT: 1050.0               // 进口温度
        }
        
        console.log('发送初始状态:', initialState)
        
        const host = process.env.NODE_ENV === 'development' ? 'http://localhost:8080' : ''
        const response = await fetch(`${host}/lab/start`, {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
          },
          body: JSON.stringify(initialState)
        })
        
        if (!response.ok) {
          const errorText = await response.text()
          throw new Error(`HTTP ${response.status}: ${errorText}`)
        }
        
        const result = await response.json()
        console.log('实验启动响应:', result)
        
        sessionId.value = result.session_id
        isRunning.value = true
        
        connectWebSocket(sessionId.value)
        generateSensorClusters()
        generateThermalHotspots()
        simulateExperiment()
        
        showMessage('实验启动成功', 'success')
        
        // 实验启动后立即发送当前所有环境参数
        setTimeout(() => {
          Object.keys(variables).forEach(key => {
            sendParameterUpdate(key, variables[key])
          })
        }, 1000)
        
      } catch (error) {
        console.error('启动实验错误:', error)
        showMessage(`启动实验失败: ${error.message}`, 'error')
      }
    }
    
    const stopExperiment = async () => {
      try {
        if (sessionId.value) {
          const host = process.env.NODE_ENV === 'development' ? 'http://localhost:8080' : ''
          await fetch(`${host}/lab/${sessionId.value}`, {
            method: 'DELETE'
          })
        }
      } catch (error) {
        console.error('停止实验错误:', error)
      } finally {
        if (websocket.value) {
          websocket.value.close()
        }
        isRunning.value = false
        sessionId.value = null
        showMessage('实验已停止', 'info')
      }
    }
    
    // ==============================
    // 右侧面板数据 - 模型测试台
    // ==============================
    
    const simState = reactive({
      inputs: { AT: 15.0, TEY: 130.0 }, 
      outputs: { TIT: 1050.0, TAT: 540.0 }, 
      controls: { AT: 'hold', TEY: 'hold' }
    })
    
    const ranges = {
      AT: { min: 0, max: 35, step: 0.1, unit: '°C' },
      TEY: { min: 100, max: 180, step: 0.5, unit: 'MW' },
      TIT: { min: 1000, max: 1100, color: 'bg-yellow-500' },
      TAT: { min: 500, max: 600, color: 'bg-orange-500' }
    }
    
    const savedWeights = ref([
      { id: 'none', name: '❌ 无 (随机初始化 / 从头训练)', desc: '随机初始化权重，必须开启在线训练以收敛。' },
      { id: 'official_v1', name: '✅ 官方基准 (Official Baseline)', desc: '官方 Seq2Seq 权重 (R² > 0.81)。' },
      { id: 'lite_v1', name: '⚡ 轻量版 (Lite v1)', desc: '轻量级 LSTM 权重。' }
    ])
    
    const models = ref([])
    const modelIdCounter = ref(1)
    
    // 模态框数据
    const showModal = ref(false)
    const modalAnimation = reactive({
      opacity: 1,
      scale: 1
    })
    
    const modalData = reactive({
      name: 'Model-01',
      target: 'TAT',
      arch: 'Seq2Seq',
      weightId: 'official_v1',
      onlineTrain: false,
      learningRate: 0.001,
      maxSteps: 1000,
      features: {
        AT: true,
        AP: true,
        AH: true,
        TEY: true
      }
    })
    
    const weightDescription = ref('选择权重描述将显示在这里...')
    
    // ==============================
    // 计算属性
    // ==============================
    
    const temperatureCurve = computed(() => {
      const points = temperatureData.value.map((point, index) => {
        const x = index * 10
        const y = 100 - (point.value / 50 * 100)
        return `${index === 0 ? 'M' : 'L'} ${x} ${y}`
      })
      return points.join(' ')
    })
    
    const temperatureGradient = computed(() => {
      const temp = variables.temperature
      if (temp < 30) return 'linear-gradient(to top, #64b5ff, #4078ff)'
      if (temp < 60) return 'linear-gradient(to top, #64ff8f, #4caf50)'
      if (temp < 80) return 'linear-gradient(to top, #ffc107, #ff9800)'
      return 'linear-gradient(to top, #ff4164, #ff4b2b)'
    })
    
    const getPressureStatus = computed(() => {
      return variables.pressure > 110 ? 'warning' : 'normal'
    })
    
    const getSystemStatus = computed(() => {
      if (variables.temperature > 80 || variables.pressure > 110) return 'warning'
      if (variables.temperature > 60 || variables.pressure > 100) return 'caution'
      return 'normal'
    })
    
    const modalStyle = computed(() => ({
      opacity: modalAnimation.opacity,
      transform: `scale(${modalAnimation.scale})`
    }))
    
    const connectionText = computed(() => {
      switch (connectionStatus.value) {
        case 'connected': return '已连接'
        case 'connecting': return '连接中...'
        case 'error': return '连接错误'
        default: return '未连接'
      }
    })
    
    // ==============================
    // 方法
    // ==============================
    
    // 左侧和中间面板方法
    const getVariableIcon = (name) => {
      const icons = {
        '炉温传感器': '🌡️',
        '压力传感器': '📊',
        '湿度传感器': '💧',
        '流量计': '📈',
        '涡轮功效': '⚙️'
      }
      return icons[name] || '📋'
    }
    
    const getControlIcon = (key) => {
      const icons = {
        'temperature': '🌡️',
        'humidity': '💧',
        'pressure': '📊',
        'turbine': '⚙️'
      }
      return icons[key] || '⚙️'
    }
    
    const floatingStyle = (index) => {
      const size = Math.random() * 8 + 2
      const duration = Math.random() * 20 + 15
      const delay = Math.random() * 8
      return {
        width: `${size}px`,
        height: `${size}px`,
        left: `${Math.random() * 100}%`,
        animationDuration: `${duration}s`,
        animationDelay: `${delay}s`
      }
    }
    
    const bubbleStyle = (index) => {
      const delay = index * 0.3
      const duration = 2 + Math.random() * 3
      const size = 4 + Math.random() * 8
      return {
        animationDelay: `${delay}s`,
        animationDuration: `${duration}s`,
        width: `${size}px`,
        height: `${size}px`
      }
    }
    
    const vaporParticleStyle = (index) => {
      const delay = index * 0.2
      const duration = 3 + Math.random() * 2
      return {
        animationDelay: `${delay}s`,
        animationDuration: `${duration}s`
      }
    }
    
    const steamJetStyle = (index) => {
      const delay = index * 0.4
      const duration = 1.5 + Math.random() * 1
      return {
        animationDelay: `${delay}s`,
        animationDuration: `${duration}s`
      }
    }
    
    const dropletStyle = (index) => {
      const delay = index * 0.1
      const duration = 2 + Math.random() * 2
      return {
        animationDelay: `${delay}s`,
        animationDuration: `${duration}s`
      }
    }
    
    const zoomIn = () => {
      zoomLevel.value = Math.min(2.5, zoomLevel.value + 0.1)
    }
    
    const zoomOut = () => {
      zoomLevel.value = Math.max(0.3, zoomLevel.value - 0.1)
    }
    
    const resetView = () => {
      zoomLevel.value = 1
    }
    
    const handleWheel = (event) => {
      event.preventDefault()
      if (event.deltaY < 0) {
        zoomIn()
      } else {
        zoomOut()
      }
    }
    
    const onParameterChange = (parameter) => {
      console.log(`${parameter} changed to:`, variables[parameter])
      
      // 立即发送参数更新
      sendParameterUpdate(parameter, variables[parameter])
      
      // 原有的set_message逻辑（如果需要保持兼容）
      const setMessage = {
        type: 'set_value',
        variable: parameter,
        value: variables[parameter]
      }
      sendWebSocketMessage(setMessage)
    }
    
    const generateSensorClusters = () => {
      sensorClusters.value = []
      const positions = [
        { x: 15, y: 35 }, { x: 30, y: 45 }, { x: 45, y: 40 },
        { x: 60, y: 50 }, { x: 75, y: 35 }, { x: 25, y: 60 },
        { x: 40, y: 70 }, { x: 65, y: 65 }, { x: 80, y: 55 }
      ]
      
      positions.forEach((pos, index) => {
        sensorClusters.value.push({
          id: index,
          x: pos.x,
          y: pos.y,
          temperature: Math.round(20 + Math.random() * 60),
          humidity: Math.round(30 + Math.random() * 50),
          status: Math.random() > 0.9 ? 'warning' : 'normal'
        })
      })
    }
    
    const generateThermalHotspots = () => {
      thermalHotspots.value = []
      const positions = [
        { x: 20, y: 40 }, { x: 35, y: 35 }, { x: 50, y: 45 },
        { x: 65, y: 40 }, { x: 40, y: 55 }, { x: 55, y: 60 },
        { x: 70, y: 50 }
      ]
      
      positions.forEach((pos, index) => {
        thermalHotspots.value.push({
          id: index,
          x: pos.x,
          y: pos.y,
          intensity: 0.3 + Math.random() * 0.6
        })
      })
    }
    
    const simulateExperiment = () => {
      if (!isRunning.value) return
      
      instrumentVariables.value.forEach(variable => {
        const change = (Math.random() - 0.5) * 4
        variable.value = Math.max(0, Math.round((variable.value + change) * 10) / 10)
        variable.history.push(Math.max(10, Math.min(95, variable.history[variable.history.length - 1] + (Math.random() - 0.5) * 10)))
        variable.history.shift()
      })
      
      sensorClusters.value.forEach(sensor => {
        sensor.temperature = Math.max(15, Math.min(85, sensor.temperature + (Math.random() - 0.5) * 4))
        sensor.humidity = Math.max(20, Math.min(80, sensor.humidity + (Math.random() - 0.5) * 3))
        sensor.status = sensor.temperature > 75 ? 'warning' : 'normal'
      })
      
      thermalHotspots.value.forEach(hotspot => {
        hotspot.intensity = Math.max(0.2, Math.min(1, hotspot.intensity + (Math.random() - 0.5) * 0.15))
      })
      
      setTimeout(simulateExperiment, 1200)
    }
    

    const mountDefaultModel = () => {
      const defaultModelMessage = {
        type: 'mount_model',
        model_id: 'default_model',
        arch: 'Seq2Seq',
        target: 'TAT',
        features: {
          AT: true,
          AP: true,
          AH: true,
          TEY: true
        },
        online_train: false
      }
      sendWebSocketMessage(defaultModelMessage)
    }
    
    const showMessage = (message, type = 'info') => {
      console.log(`[${type}] ${message}`)
    }
    
    // 右侧面板方法 - 模型测试台
    const startControl = (key, direction) => {
      simState.controls[key] = direction
      const controlMessage = {
        type: 'control',
        variable: key,
        mode: direction === 'inc' ? 'increase' : 'decrease'
      }
      sendWebSocketMessage(controlMessage)
    }
    
    const stopControl = (key) => {
      simState.controls[key] = 'hold'
    }
    
    const updatePhysics = () => {
      for (const [key, mode] of Object.entries(simState.controls)) {
        if (mode === 'inc') {
          simState.inputs[key] = Math.min(simState.inputs[key] + ranges[key].step, ranges[key].max)
        }
        if (mode === 'dec') {
          simState.inputs[key] = Math.max(simState.inputs[key] - ranges[key].step, ranges[key].min)
        }
      }
      
      let targetTIT = 1020 + (simState.inputs.TEY - 100) * 1.2
      simState.outputs.TIT += (targetTIT - simState.outputs.TIT) * 0.1
      
      let targetTAT = 530 + (simState.outputs.TIT - 1000) * 0.6
      simState.outputs.TAT += (targetTAT - simState.outputs.TAT) * 0.05
    }
    
    const renderSimulator = () => {
      const controlsContainer = document.getElementById('controls-container')
      const sensorsContainer = document.getElementById('sensors-container')
      
      if (controlsContainer && !controlsContainer.innerHTML) {
        controlsContainer.innerHTML = Object.keys(simState.inputs).map(key => `
          <div class="bg-slate-800 p-3 rounded border border-slate-700">
            <div class="flex justify-between mb-2 text-xs">
              <span class="font-bold text-slate-300">${key}</span>
              <span class="font-mono text-blue-400" id="val-${key}">${simState.inputs[key].toFixed(1)}</span>
            </div>
            <div class="flex gap-1">
              <button 
                onmousedown="window.startControl('${key}', 'dec')" 
                onmouseup="window.stopControl('${key}')"
                class="flex-1 bg-slate-700 hover:bg-slate-600 py-1 rounded text-[10px] transition-colors"
              >
                <i class="fa-solid fa-minus"></i>
              </button>
              <button 
                onmousedown="window.startControl('${key}', 'inc')" 
                onmouseup="window.stopControl('${key}')"
                class="flex-1 bg-slate-700 hover:bg-slate-600 py-1 rounded text-[10px] transition-colors"
              >
                <i class="fa-solid fa-plus"></i>
              </button>
            </div>
          </div>
        `).join('')
      }
      
      if (sensorsContainer && !sensorsContainer.innerHTML) {
        sensorsContainer.innerHTML = Object.keys(simState.outputs).map(key => `
          <div>
            <div class="flex justify-between text-xs mb-1">
              <span class="text-slate-400">${key}</span>
              <span class="font-mono text-slate-200" id="out-val-${key}">${simState.outputs[key].toFixed(2)}</span>
            </div>
            <div class="h-1.5 bg-slate-900 rounded-full overflow-hidden">
              <div id="out-bar-${key}" class="h-full ${ranges[key].color} progress-fill" style="width: 0%"></div>
            </div>
          </div>
        `).join('')
      }
      
      for (const key in simState.inputs) {
        const element = document.getElementById(`val-${key}`)
        if (element) element.innerText = simState.inputs[key].toFixed(1)
      }
      
      for (const key in simState.outputs) {
        const val = simState.outputs[key]
        const valElement = document.getElementById(`out-val-${key}`)
        const barElement = document.getElementById(`out-bar-${key}`)
        
        if (valElement) valElement.innerText = val.toFixed(2)
        if (barElement) {
          const pct = (val - ranges[key].min) / (ranges[key].max - ranges[key].min) * 100
          barElement.style.width = `${Math.max(0, Math.min(100, pct))}%`
        }
      }
    }
    
    const openModal = () => {
      console.log('打开模态框')
      modalData.name = `Model-${models.value.length + 1}`
      modalData.target = 'TAT'
      modalData.arch = 'Seq2Seq'
      modalData.weightId = 'official_v1'
      modalData.onlineTrain = false
      modalData.learningRate = 0.001
      modalData.maxSteps = 1000
      modalData.features = { AT: true, AP: true, AH: true, TEY: true }
      
      showModal.value = true
      modalAnimation.opacity = 1
      modalAnimation.scale = 1
      
      onWeightsChange()
    }
    
    const closeModal = () => {
      console.log('关闭模态框')
      modalAnimation.opacity = 0
      modalAnimation.scale = 0.95
      setTimeout(() => {
        showModal.value = false
      }, 300)
    }
    
    const onWeightsChange = () => {
      const weightObj = savedWeights.value.find(w => w.id === modalData.weightId)
      weightDescription.value = weightObj ? weightObj.desc : '选择权重描述将显示在这里...'
      
      if (modalData.weightId === 'none') {
        modalData.onlineTrain = true
      }
    }
    
    const addModel = () => {
      console.log('添加模型', modalData)
      
      const mountMessage = {
        type: 'mount_model',
        model_id: modalData.name,
        arch: modalData.arch,
        target: modalData.target,
        features: modalData.features,
        online_train: modalData.onlineTrain
      }
      
      sendWebSocketMessage(mountMessage)
      
      const weightObj = savedWeights.value.find(w => w.id === modalData.weightId)
      const weightName = weightObj ? weightObj.name.split('(')[0].trim() : 'Unknown'
    
      const model = {
        id: modelIdCounter.value++,
        name: modalData.name,
        arch: modalData.arch,
        weightId: modalData.weightId,
        weightName: weightName,
        target: modalData.target,
        isOnline: modalData.onlineTrain,
        isPaused: false,
        step: 0,
        loss: (modalData.weightId === 'none') ? '0.8' : '0.02',
        chart: null,
        lossChart: null
      }
    
      models.value.push(model)
      createModelCard(model)
      closeModal()
      updateModelCount()
      
      if (model.isOnline && !model.isPaused) {
        console.log(`Model ${model.id} started with online training`)
      }
      
      console.log(`Total models: ${models.value.length}`)
      console.log(`Model ${model.id} created with loss: ${model.loss}`)
    }
    
    const createModelCard = (model) => {
      const modelsContainer = document.getElementById('models-container')
      if (!modelsContainer) return
      
      const div = document.createElement('div')
      div.className = "bg-slate-900 border border-slate-700 rounded-lg p-0 overflow-hidden transition-all"
      div.id = `model-card-${model.id}`
      div.style.animation = 'fadeIn 0.3s ease-out'
      
      let controlButtons = ''
      if (model.isOnline) {
        controlButtons = `
          <div class="flex items-center gap-1 mr-2 border-r border-slate-700 pr-2">
            <button onclick="window.exportWeights(${model.id})" id="btn-export-${model.id}" class="hidden text-xs bg-slate-700 hover:bg-blue-600 text-white px-2 py-0.5 rounded transition flex items-center gap-1" title="导出当前权重">
              <i class="fa-solid fa-floppy-disk"></i> 导出
            </button>
            <button onclick="window.toggleTraining(${model.id})" id="btn-pause-${model.id}" class="text-slate-400 hover:text-yellow-400 w-6 h-6 flex items-center justify-center rounded hover:bg-slate-800 transition" title="暂停/继续训练">
              <i class="fa-solid fa-pause btn-icon-anim"></i>
            </button>
          </div>
        `
      }

      const headerHtml = `
        <div class="p-3 border-b border-slate-800 bg-slate-800/30 flex justify-between items-center">
          <div class="flex items-center gap-2">
            <div class="w-2 h-2 rounded-full ${model.isOnline ? 'bg-green-500 animate-pulse' : 'bg-blue-500'}" id="status-dot-${model.id}"></div>
            <div class="overflow-hidden">
              <h4 class="font-bold text-sm text-slate-200 leading-none truncate w-32" title="${model.name}">${model.name}</h4>
              <span class="text-[10px] text-slate-500 truncate block">${model.target} | ${model.weightName}</span>
            </div>
          </div>
          <div class="flex items-center gap-2 text-[10px]">
            ${controlButtons}
            <div class="text-right">
              <div class="text-slate-500">Loss</div>
              <div class="font-mono font-bold ${model.isOnline ? 'text-orange-400' : 'text-slate-300'} loss-value" id="loss-${model.id}">${model.loss}</div>
            </div>
            <button onclick="window.removeModel(${model.id})" class="text-slate-600 hover:text-red-400 px-2"><i class="fa-solid fa-xmark"></i></button>
          </div>
        </div>
      `

      const predictionChartHtml = `
        <div class="p-2 border-b border-slate-800">
          <div class="text-[10px] text-slate-400 mb-1">预测 vs 实际</div>
          <div class="h-16 w-full bg-slate-900 rounded relative">
            <canvas id="chart-${model.id}"></canvas>
          </div>
        </div>
      `

      const lossChartHtml = `
        <div class="p-2 border-b border-slate-800">
          <div class="flex justify-between items-center mb-1">
            <div class="text-[10px] text-slate-400">训练损失</div>
            <div class="text-[10px] text-orange-400 font-mono" id="current-loss-${model.id}">${model.loss}</div>
          </div>
          <div class="h-16 w-full bg-slate-900 rounded relative">
            <canvas id="loss-chart-${model.id}"></canvas>
          </div>
        </div>
      `

      let terminalHtml = ''
      if (model.isOnline) {
        terminalHtml = `
          <div class="bg-black p-2 h-24 overflow-y-auto terminal-logs" id="term-${model.id}">
            <div class="text-slate-500 mb-1"># Initialized.</div>
            <div class="${model.weightId === 'none' ? 'text-yellow-400' : 'text-green-400'}"># Weights: ${model.weightName}.</div>
            <div class="text-blue-400"># Training loop started...</div>
          </div>
        `
      }

      div.innerHTML = headerHtml + predictionChartHtml + lossChartHtml + terminalHtml
      modelsContainer.appendChild(div)

      const emptyState = document.getElementById('empty-state')
      if (emptyState) emptyState.style.display = 'none'
      
      nextTick(() => {
        initPredictionChart(model)
        initLossChart(model)
      })
    }
    
    const initPredictionChart = (model) => {
      const canvas = document.getElementById(`chart-${model.id}`)
      if (!canvas) {
        console.error(`Prediction chart canvas for model ${model.id} not found`)
        return
      }

      const ctx = canvas.getContext('2d')
      
      const parent = canvas.parentElement
      if (parent) {
        canvas.width = parent.offsetWidth
        canvas.height = parent.offsetHeight
      }

      console.log(`Initializing prediction chart for model ${model.id}`)

      const baseLoss = model.weightId === 'none' ? 0.7 : 0.02
      const initialData = Array.from({length: 10}, (_, index) => {
        const progress = index / 9
        const noise = (Math.random() - 0.5) * 0.05
        
        if (model.weightId === 'none') {
          return Math.max(0.1, baseLoss * (1 - progress * 0.3) + noise)
        } else {
          return Math.max(0.01, baseLoss + Math.sin(progress * Math.PI) * 0.01 + noise)
        }
      })

      const labels = Array.from({length: 10}, (_, i) => i)

      try {
        model.chart = new Chart(ctx, {
          type: 'line',
          data: {
            labels: labels,
            datasets: [
              {
                label: 'True',
                data: [...initialData],
                borderColor: '#10b981',
                backgroundColor: 'transparent',
                borderWidth: 1.5,
                tension: 0.4,
                pointRadius: 0,
                pointHoverRadius: 2,
                fill: false
              },
              {
                label: 'Pred',
                data: initialData.map(val => val + (Math.random() - 0.5) * 0.03),
                borderColor: '#3b82f6',
                backgroundColor: 'transparent',
                borderWidth: 1.5,
                tension: 0.4,
                pointRadius: 0,
                pointHoverRadius: 2,
                fill: false
              }
            ]
          },
          options: {
            responsive: true,
            maintainAspectRatio: false,
            animation: {
              duration: 0
            },
            plugins: {
              legend: { 
                display: false 
              },
              tooltip: { 
                enabled: false 
              }
            },
            scales: {
              x: { 
                display: false,
                type: 'linear',
                min: 0,
                max: 9
              },
              y: { 
                display: false,
                type: 'linear',
                suggestedMin: model.weightId === 'none' ? 0 : 0,
                suggestedMax: model.weightId === 'none' ? 1 : 0.1
              }
            },
            interaction: { 
              intersect: false 
            },
            elements: {
              line: {
                tension: 0.4
              }
            }
          }
        })
        
        console.log(`Prediction chart successfully initialized for model ${model.id}`)
        
      } catch (error) {
        console.error(`Failed to initialize prediction chart for model ${model.id}:`, error)
      }
    }
    
    const initLossChart = (model) => {
      const canvas = document.getElementById(`loss-chart-${model.id}`)
      if (!canvas) {
        console.error(`Loss chart canvas for model ${model.id} not found`)
        return
      }

      const ctx = canvas.getContext('2d')
      
      const parent = canvas.parentElement
      if (parent) {
        canvas.width = parent.offsetWidth
        canvas.height = parent.offsetHeight
      }

      console.log(`Initializing loss chart for model ${model.id}`)

      const baseLoss = model.weightId === 'none' ? 0.7 : 0.02
      const initialLossData = Array.from({length: 20}, (_, index) => {
        const progress = index / 19
        const noise = (Math.random() - 0.5) * 0.05
        
        if (model.weightId === 'none') {
          return Math.max(0.05, baseLoss * (1 - progress * 0.8) + noise)
        } else {
          return Math.max(0.001, baseLoss + Math.sin(progress * Math.PI) * 0.01 + noise)
        }
      })

      try {
        model.lossChart = new Chart(ctx, {
          type: 'line',
          data: {
            labels: Array.from({length: 20}, (_, i) => i),
            datasets: [
              {
                label: 'Loss',
                data: initialLossData,
                borderColor: '#f97316',
                backgroundColor: 'rgba(249, 115, 22, 0.1)',
                borderWidth: 1.5,
                tension: 0.4,
                pointRadius: 0,
                pointHoverRadius: 2,
                fill: true
              }
            ]
          },
          options: {
            responsive: true,
            maintainAspectRatio: false,
            animation: {
              duration: 0
            },
            plugins: {
              legend: { 
                display: false 
              },
              tooltip: { 
                enabled: false 
              }
            },
            scales: {
              x: { 
                display: false,
                type: 'linear',
                min: 0,
                max: 19
              },
              y: { 
                display: false,
                type: 'linear',
                beginAtZero: true,
                suggestedMin: 0,
                suggestedMax: model.weightId === 'none' ? 1 : 0.1
              }
            },
            interaction: { 
              intersect: false 
            },
            elements: {
              line: {
                tension: 0.4
              }
            }
          }
        })
        
        console.log(`Loss chart successfully initialized for model ${model.id}`)
        
      } catch (error) {
        console.error(`Failed to initialize loss chart for model ${model.id}:`, error)
      }
    }
    
    const updateModels = () => {
      const now = Date.now()
      
      models.value.forEach(m => {
        if (m._lastUpdate && now - m._lastUpdate < 100) {
          return
        }
        m._lastUpdate = now
        
        if (m.isOnline && !m.isPaused) {
          m.step++
          
          let baseLoss = m.weightId === 'none' ? 0.7 : 0.02
          let decayRate = m.weightId === 'none' ? 100 : 200
          
          let newLoss = baseLoss * Math.exp(-m.step / decayRate) + (Math.random() - 0.5) * 0.05
          newLoss = Math.max(0.001, newLoss)
          
          m.loss = newLoss.toFixed(4)

          const lossElement = document.getElementById(`loss-${m.id}`)
          const currentLossElement = document.getElementById(`current-loss-${m.id}`)
          if (lossElement) lossElement.innerText = m.loss
          if (currentLossElement) currentLossElement.innerText = m.loss

          if (m.step % 10 === 0) {
            logToTerminal(m.id, `[Step ${m.step}] Loss: ${m.loss}`)
          }
        }

        // 使用 requestAnimationFrame 确保图表更新在下一帧执行
        requestAnimationFrame(() => {
          updatePredictionChartSafely(m)
          updateLossChartSafely(m)
        })
      })
    }

    const updatePredictionChartSafely = (model) => {
      if (!model.chart) {
        console.warn(`Prediction chart for model ${model.id} not initialized`)
        return
      }

      try {
        if (model._updatingChart || !model.chart.data || !model.chart.data.datasets) {
          return
        }
        model._updatingChart = true

        const currentLoss = parseFloat(model.loss)
        const currentPredLoss = currentLoss * (0.9 + Math.random() * 0.2)
        
        const chartData = model.chart.data.datasets[0]?.data
        const predData = model.chart.data.datasets[1]?.data
        
        if (!chartData || !predData) {
          model._updatingChart = false
          return
        }
        
        chartData.push(currentLoss)
        predData.push(currentPredLoss)
        
        if (chartData.length > 30) {
          chartData.shift()
          predData.shift()
        }
        
        if (model.chart.data.labels) {
          model.chart.data.labels.push(model.chart.data.labels.length)
          if (model.chart.data.labels.length > 30) {
            model.chart.data.labels.shift()
          }
        }

        try {
          model.chart.update('none')
        } catch (e) {
          console.error(`Chart update error for model ${model.id}:`, e)
        } finally {
          model._updatingChart = false
        }
        
      } catch (error) {
        console.error(`Error updating prediction chart for model ${model.id}:`, error)
        model._updatingChart = false
      }
    }

    const updateLossChartSafely = (model) => {
      if (!model.lossChart) {
        console.warn(`Loss chart for model ${model.id} not initialized`)
        return
      }

      try {
        if (model._updatingLossChart || !model.lossChart.data || !model.lossChart.data.datasets) {
          return
        }
        model._updatingLossChart = true

        const currentLoss = parseFloat(model.loss)
        
        const lossData = model.lossChart.data.datasets[0]?.data
        if (!lossData) {
          model._updatingLossChart = false
          return
        }
        
        lossData.push(currentLoss)
        
        if (lossData.length > 20) {
          lossData.shift()
        }
        
        if (model.lossChart.data.labels) {
          model.lossChart.data.labels.push(model.lossChart.data.labels.length)
          if (model.lossChart.data.labels.length > 20) {
            model.lossChart.data.labels.shift()
          }
        }

        const dataArray = [...lossData]
        const maxLoss = dataArray.length > 0 ? Math.max(...dataArray) : 0.1
        
        try {
          if (model.lossChart.options && model.lossChart.options.scales && model.lossChart.options.scales.y) {
            model.lossChart.options.scales.y.suggestedMax = Math.max(maxLoss * 1.1, 0.1)
          }
          model.lossChart.update('none')
        } catch (e) {
          console.error(`Loss chart update error for model ${model.id}:`, e)
        } finally {
          model._updatingLossChart = false
        }
        
      } catch (error) {
        console.error(`Error updating loss chart for model ${model.id}:`, error)
        model._updatingLossChart = false
      }
    }

    const toggleTraining = (id) => {
      const model = models.value.find(m => m.id === id)
      if (!model) return

      const controlMessage = {
        type: 'control_training',
        model_id: model.name,
        start: model.isPaused
      }
      
      sendWebSocketMessage(controlMessage)

      model.isPaused = !model.isPaused
      
      const btn = document.getElementById(`btn-pause-${id}`)
      const icon = btn?.querySelector('i')
      const dot = document.getElementById(`status-dot-${id}`)
      const exportBtn = document.getElementById(`btn-export-${id}`)

      if (model.isPaused) {
        if (icon) icon.className = "fa-solid fa-play text-green-400 btn-icon-anim"
        if (dot) dot.className = "w-2 h-2 rounded-full bg-yellow-500"
        if (exportBtn) exportBtn.classList.remove('hidden')
        logToTerminal(id, "Paused. Ready to export.")
      } else {
        if (icon) icon.className = "fa-solid fa-pause text-slate-400 btn-icon-anim"
        if (dot) dot.className = "w-2 h-2 rounded-full bg-green-500 animate-pulse"
        if (exportBtn) exportBtn.classList.add('hidden')
        logToTerminal(id, "Resumed training.")
      }
    }

    const exportWeights = (id) => {
      const model = models.value.find(m => m.id === id)
      if (!model) return

      const exportMessage = {
        type: 'export_weights',
        model_id: model.name,
        user_id: 'current_user',
        new_name: `${model.name}_exported`
      }
      
      sendWebSocketMessage(exportMessage)

      logToTerminal(id, `>> Weights exported: ${model.name}_exported`)
      alert(`权重已成功导出到本地列表！\n\n名称: ${model.name}_exported\n\n下次挂载新模型时，您可以在"预训练权重"下拉列表中找到它。`)
    }

    const logToTerminal = (id, msg) => {
      const term = document.getElementById(`term-${id}`)
      if (term) {
        const log = document.createElement('div')
        log.className = "log-entry text-slate-300"
        log.innerText = msg
        term.appendChild(log)
        term.scrollTop = term.scrollHeight
      }
    }

    const removeModel = (id) => {
      const model = models.value.find(m => m.id === id)
      if (model) {
        const unmountMessage = {
          type: 'unmount_model',
          model_id: model.name
        }
        sendWebSocketMessage(unmountMessage)
        
        if (model.chart) {
          model.chart.destroy()
          model.chart = null
        }
        if (model.lossChart) {
          model.lossChart.destroy()
          model.lossChart = null
        }
        model._updatingChart = null
        model._updatingLossChart = null
        model._lastUpdate = null
      }
      
      models.value = models.value.filter(m => m.id !== id)
      const modelCard = document.getElementById(`model-card-${id}`)
      if (modelCard) modelCard.remove()
      
      if (models.value.length === 0) {
        const emptyState = document.getElementById('empty-state')
        if (emptyState) emptyState.style.display = 'flex'
      }
      
      updateModelCount()
    }
    
    const updateModelCount = () => { 
      const modelCountElement = document.getElementById('model-count')
      if (modelCountElement) {
        modelCountElement.innerText = `${models.value.length} Active`
      }
    }

    // ==============================
    // 生命周期
    // ==============================
    onMounted(() => {
      generateSensorClusters()
      generateThermalHotspots()
      
      nextTick(() => {
        renderSimulator()
      })
      
      const intervalId = setInterval(() => {
        updatePhysics()
        renderSimulator()
        updateModels()
      }, 1000)
      
      window.startControl = startControl
      window.stopControl = stopControl
      window.toggleTraining = toggleTraining
      window.exportWeights = exportWeights
      window.removeModel = removeModel
      window.simState = simState
      
      return () => {
        clearInterval(intervalId)
      }
    })
    
    return {
      instrumentVariables,
      temperatureData,
      timeLabels,
      distributionData,
      variables,
      variableControls,
      zoomLevel,
      sensorClusters,
      thermalHotspots,
      isRunning,
      simState,
      models,
      showModal,
      modalData,
      savedWeights,
      weightDescription,
      temperatureCurve,
      temperatureGradient,
      getPressureStatus,
      getSystemStatus,
      modalStyle,
      connectionStatus,
      connectionText,
      getVariableIcon,
      getControlIcon,
      floatingStyle,
      bubbleStyle,
      vaporParticleStyle,
      steamJetStyle,
      dropletStyle,
      zoomIn,
      zoomOut,
      resetView,
      handleWheel,
      onParameterChange,
      toggleExperiment,
      openModal,
      closeModal,
      onWeightsChange,
	  updateFrontendWithBackendData,
	  updateInstrumentVariables,
      addModel
    }
  }
}
</script>
<style scoped>
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.8);
  backdrop-filter: blur(10px);
  z-index: 50;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: opacity 0.3s;
}

.modal-content {
  background: #1e293b;
  border: 1px solid #475569;
  border-radius: 12px;
  width: 500px;
  box-shadow: 0 25px 50px -12px rgba(0, 0, 0, 0.5);
  transition: all 0.3s;
  overflow: hidden;
}

.modal-header {
  padding: 1rem 1.25rem;
  border-bottom: 1px solid #374151;
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: rgba(30, 41, 59, 0.5);
}

.modal-body {
  padding: 1.5rem;
  space-y: 1.25rem;
}
.modal-body input, .modal-body select {
  color: white !important;
}

.modal-body input::placeholder {
  color: #94a3b8 !important;
}

.modal-footer {
  padding: 1rem;
  border-top: 1px solid #374151;
  display: flex;
  justify-content: flex-end;
  gap: 0.5rem;
  background: rgba(30, 41, 59, 0.5);
}
/* 基础布局 */
.virtual-lab {
  display: flex;
  height: 100vh;
  width: 100vw;
  font-family: 'Segoe UI', 'SF Pro Display', -apple-system, BlinkMacSystemFont, sans-serif;
  background: linear-gradient(135deg, #0a0a1a 0%, #151528 50%, #0a0a1a 100%);
  color: #e0e0ff;
  overflow: hidden;
  position: relative;
}

/* 三栏布局 */
.left-panel {
  width: 25%;
  min-width: 300px;
  background: rgba(16, 18, 32, 0.8);
  backdrop-filter: blur(20px);
  border-right: 1px solid rgba(64, 120, 255, 0.1);
  display: flex;
  flex-direction: column;
  padding: 20px;
  overflow-y: auto;
}

.center-panel {
  width: 45%;
  background: rgba(12, 14, 28, 0.6);
  display: flex;
  flex-direction: column;
  padding: 20px;
  overflow-y: auto;
}

.right-panel {
  width: 30%;
  min-width: 320px;
  background: rgba(16, 18, 32, 0.8);
  backdrop-filter: blur(20px);
  border-left: 1px solid rgba(64, 120, 255, 0.1);
  display: flex;
  flex-direction: column;
  padding: 20px;
  overflow-y: auto;
}

.model-testing-panel {
  background: #0f172a !important;
  color: #e2e8f0;
}

/* 背景效果 */
.background-effects {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  pointer-events: none;
  z-index: 0;
}

.grid-lines {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-image: 
    linear-gradient(rgba(64, 120, 255, 0.1) 1px, transparent 1px),
    linear-gradient(90deg, rgba(64, 120, 255, 0.1) 1px, transparent 1px);
  background-size: 50px 50px;
  opacity: 0.3;
}

.floating-elements {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
}

.floating-element {
  position: absolute;
  background: rgba(100, 181, 255, 0.1);
  border-radius: 50%;
  animation: float 20s infinite linear;
}

@keyframes float {
  0% {
    transform: translateY(100vh) rotate(0deg);
    opacity: 0;
  }
  10% {
    opacity: 0.3;
  }
  90% {
    opacity: 0.3;
  }
  100% {
    transform: translateY(-100px) rotate(360deg);
    opacity: 0;
  }
}

/* 面板头部 */
.panel-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 25px;
  padding-bottom: 15px;
  border-bottom: 1px solid rgba(64, 120, 255, 0.2);
  position: relative;
  z-index: 1;
}

.panel-header h2 {
  margin: 0;
  color: #e0e0ff;
  font-weight: 600;
  font-size: 1.3rem;
  background: linear-gradient(135deg, #64b5ff, #64ff8f);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.status-indicator {
  display: flex;
  align-items: center;
  font-size: 0.85rem;
  color: #64ff8f;
}

.pulse {
  width: 8px;
  height: 8px;
  background: #64ff8f;
  border-radius: 50%;
  margin-right: 8px;
  animation: pulse 2s infinite;
}

@keyframes pulse {
  0% {
    box-shadow: 0 0 0 0 rgba(100, 255, 143, 0.7);
  }
  70% {
    box-shadow: 0 0 0 6px rgba(100, 255, 143, 0);
  }
  100% {
    box-shadow: 0 0 0 0 rgba(100, 255, 143, 0);
  }
}

/* 左侧面板样式 */
.core-variables, .temperature-trend, .parameter-distribution {
  background: rgba(30, 32, 56, 0.6);
  border-radius: 16px;
  padding: 20px;
  margin-bottom: 20px;
  border: 1px solid rgba(64, 120, 255, 0.15);
  backdrop-filter: blur(10px);
  position: relative;
  z-index: 1;
}

.core-variables h3, .temperature-trend h3, .parameter-distribution h3 {
  margin-top: 0;
  margin-bottom: 15px;
  color: #b0b0ff;
  font-size: 1rem;
  font-weight: 600;
}

.variable-item {
  background: rgba(40, 42, 66, 0.8);
  border-radius: 12px;
  padding: 15px;
  margin-bottom: 12px;
  border: 1px solid rgba(64, 120, 255, 0.1);
  transition: all 0.3s ease;
}

.variable-item:hover {
  border-color: rgba(64, 120, 255, 0.3);
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.2);
}

.variable-header {
  display: flex;
  align-items: center;
  margin-bottom: 10px;
}

.variable-icon {
  position: relative;
  width: 36px;
  height: 36px;
  margin-right: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.icon-bg {
  position: absolute;
  width: 100%;
  height: 100%;
  background: rgba(64, 120, 255, 0.2);
  border-radius: 8px;
  transform: rotate(45deg);
}

.variable-icon .icon {
  font-size: 1.1rem;
  z-index: 1;
}

.variable-info {
  flex: 1;
}

.variable-name {
  font-weight: 500;
  color: #b0b0ff;
  font-size: 0.9rem;
  margin-bottom: 4px;
}

.variable-value {
  font-size: 1.3rem;
  font-weight: 700;
  color: #64b5ff;
  text-shadow: 0 0 10px rgba(100, 181, 255, 0.5);
}

.variable-status {
  display: flex;
  align-items: center;
  font-size: 0.75rem;
  padding: 4px 8px;
  border-radius: 8px;
}

.variable-status.normal {
  background: rgba(100, 255, 143, 0.15);
  color: #64ff8f;
}

.variable-status.warning {
  background: rgba(255, 193, 7, 0.15);
  color: #ffc107;
}

.status-dot {
  width: 6px;
  height: 6px;
  border-radius: 50%;
  margin-right: 5px;
}

.variable-status.normal .status-dot {
  background: #64ff8f;
  box-shadow: 0 0 6px #64ff8f;
}

.variable-status.warning .status-dot {
  background: #ffc107;
  box-shadow: 0 0 6px #ffc107;
}

.variable-trend {
  margin-top: 8px;
}

.trend-chart {
  display: flex;
  align-items: flex-end;
  height: 30px;
  gap: 2px;
}

.trend-point {
  flex: 1;
  background: linear-gradient(to top, #64b5ff, #4078ff);
  border-radius: 1px;
  min-height: 2px;
  transition: height 0.3s ease;
}

/* 温度趋势图 */
.trend-chart-container {
  margin-top: 15px;
}

.chart-area {
  position: relative;
  height: 120px;
  background: rgba(20, 22, 46, 0.5);
  border-radius: 8px;
  padding: 10px;
}

.chart-grid {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  padding: 10px;
}

.grid-line {
  height: 1px;
  background: rgba(64, 120, 255, 0.1);
}

.chart-line {
  position: absolute;
  top: 10px;
  left: 10px;
  right: 10px;
  bottom: 10px;
}

.chart-points {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
}

.data-point {
  position: absolute;
  transform: translate(-50%, 50%);
}

.point-glow {
  width: 8px;
  height: 8px;
  background: #64ff8f;
  border-radius: 50%;
  box-shadow: 0 0 8px #64ff8f;
  animation: pulse 2s infinite;
}

.point-value {
  position: absolute;
  top: -20px;
  left: 50%;
  transform: translateX(-50%);
  font-size: 0.7rem;
  color: #64ff8f;
  white-space: nowrap;
}

.chart-labels {
  display: flex;
  justify-content: space-between;
  margin-top: 8px;
  font-size: 0.75rem;
  color: #b0b0ff;
}

/* 参数分布 */
.distribution-chart {
  margin-top: 15px;
}

.distribution-bar {
  display: flex;
  align-items: center;
  margin-bottom: 12px;
}

.bar-label {
  width: 80px;
  font-size: 0.8rem;
  color: #b0b0ff;
}

.bar-container {
  flex: 1;
  height: 20px;
  background: rgba(64, 120, 255, 0.1);
  border-radius: 10px;
  position: relative;
  overflow: hidden;
}

.bar-fill {
  height: 100%;
  background: linear-gradient(90deg, #64ff8f, #64b5ff);
  border-radius: 10px;
  transition: width 0.5s ease;
  box-shadow: 0 0 10px rgba(100, 255, 143, 0.3);
}

.bar-value {
  position: absolute;
  right: 8px;
  top: 50%;
  transform: translateY(-50%);
  font-size: 0.75rem;
  color: #e0e0ff;
  font-weight: 600;
}

/* 模型容器 */
.model-container {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #0c0c1e 0%, #151530 100%);
  border-radius: 20px;
  border: 1px solid rgba(64, 120, 255, 0.2);
  margin-bottom: 20px;
  overflow: hidden;
  position: relative;
  transition: transform 0.3s ease;
  transform-origin: center center;
}

.advanced-factory-3d {
  width: 95%;
  height: 95%;
  position: relative;
  transform-style: preserve-3d;
  perspective: 2000px;
}

/* === 高级3D工厂样式 === */

/* 地面和基础 */
.factory-foundation {
  position: absolute;
  bottom: 0;
  left: 0;
  width: 100%;
  height: 15%;
}

.concrete-base {
  position: absolute;
  bottom: 0;
  left: 0;
  width: 100%;
  height: 80%;
  background: linear-gradient(to top, #3a3a5a, #4a4a6a);
  border-radius: 8px 8px 0 0;
}

.ground-texture {
  position: absolute;
  bottom: 80%;
  left: 0;
  width: 100%;
  height: 20%;
  background: 
    radial-gradient(circle at 20% 50%, rgba(90, 90, 120, 0.3) 2px, transparent 2px),
    radial-gradient(circle at 80% 30%, rgba(90, 90, 120, 0.3) 2px, transparent 2px);
  background-size: 30px 30px;
}

.access-roads {
  position: absolute;
  bottom: 0;
  left: 10%;
  width: 80%;
  height: 40%;
  display: flex;
  justify-content: space-between;
}

.road {
  width: 15%;
  height: 100%;
  background: #5a5a7a;
  border-radius: 2px;
}

/* 主生产厂房 */
.main-production-plant {
  position: absolute;
  bottom: 15%;
  left: 5%;
  width: 60%;
  height: 50%;
}

.plant-structure {
  position: relative;
  width: 100%;
  height: 100%;
}

/* 钢结构框架 */
.steel-frame {
  position: absolute;
  width: 100%;
  height: 100%;
}

.frame-column {
  position: absolute;
  width: 3%;
  height: 100%;
  background: linear-gradient(90deg, #6a6a8a, #5a5a7a);
  border-radius: 2px;
}

.frame-column:nth-child(1) { left: 5%; }
.frame-column:nth-child(2) { left: 20%; }
.frame-column:nth-child(3) { left: 35%; }
.frame-column:nth-child(4) { left: 50%; }
.frame-column:nth-child(5) { left: 65%; }
.frame-column:nth-child(6) { left: 80%; }
.frame-column:nth-child(7) { left: 95%; }

.frame-beam {
  position: absolute;
  width: 100%;
  height: 3%;
  background: linear-gradient(to right, #6a6a8a, #5a5a7a);
  border-radius: 2px;
}

.frame-beam:nth-child(8) { top: 20%; }
.frame-beam:nth-child(9) { top: 40%; }
.frame-beam:nth-child(10) { top: 60%; }
.frame-beam:nth-child(11) { top: 80%; }
.frame-beam:nth-child(12) { top: 95%; }

/* 外墙系统 */
.facade-system {
  position: absolute;
  top: 10%;
  left: 8%;
  width: 84%;
  height: 80%;
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  grid-template-rows: repeat(3, 1fr);
  gap: 8px;
  padding: 10px;
}

.wall-panel {
  background: #4a4a6a;
  border: 1px solid #5a5a7a;
  border-radius: 4px;
  position: relative;
  overflow: hidden;
}

.wall-panel::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: linear-gradient(45deg, 
    transparent 0%, 
    rgba(255, 100, 100, var(--temp-effect, 0.1)) 50%, 
    transparent 100%);
  transition: all 0.5s ease;
}

.insulation-layer {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(100, 200, 255, 0.1);
  transition: opacity 0.5s ease;
}

/* 屋顶系统 */
.roof-complex {
  position: absolute;
  top: -15%;
  left: 0;
  width: 100%;
  height: 20%;
}

.roof-truss {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  display: flex;
  justify-content: space-between;
  padding: 0 5%;
}

.truss-member {
  width: 2%;
  height: 100%;
  background: linear-gradient(to bottom, #6a6a8a, #5a5a7a);
  border-radius: 1px;
}

.skylight-system {
  position: absolute;
  top: 30%;
  left: 20%;
  width: 60%;
  height: 40%;
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  grid-template-rows: repeat(2, 1fr);
  gap: 10px;
}

.skylight {
  background: rgba(100, 200, 255, 0.3);
  border: 1px solid #64b5ff;
  border-radius: 2px;
  box-shadow: 0 0 10px rgba(100, 181, 255, 0.5);
}

.roof-ventilation-system {
  position: absolute;
  top: -80%;
  left: 50%;
  transform: translateX(-50%);
}

.industrial-fan {
  width: 40px;
  height: 40px;
  position: relative;
  transition: transform 0.3s ease;
}

.fan-housing {
  width: 100%;
  height: 100%;
  background: #4a4a6a;
  border-radius: 50%;
  border: 3px solid #5a5a7a;
}

.fan-blades {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 80%;
  height: 80%;
}

.blade {
  position: absolute;
  width: 4px;
  height: 15px;
  background: #64b5ff;
  top: 50%;
  left: 50%;
  transform-origin: center bottom;
  border-radius: 2px;
}

.blade:nth-child(1) { transform: translate(-50%, -50%) rotate(0deg); }
.blade:nth-child(2) { transform: translate(-50%, -50%) rotate(45deg); }
.blade:nth-child(3) { transform: translate(-50%, -50%) rotate(90deg); }
.blade:nth-child(4) { transform: translate(-50%, -50%) rotate(135deg); }
.blade:nth-child(5) { transform: translate(-50%, -50%) rotate(180deg); }
.blade:nth-child(6) { transform: translate(-50%, -50%) rotate(225deg); }
.blade:nth-child(7) { transform: translate(-50%, -50%) rotate(270deg); }
.blade:nth-child(8) { transform: translate(-50%, -50%) rotate(315deg); }

/* 生产设备 */
.production-equipment {
  position: absolute;
  top: 25%;
  left: 10%;
  width: 80%;
  height: 60%;
}

/* 反应釜系统 */
.reactor-system {
  position: absolute;
  left: 5%;
  width: 30%;
  height: 80%;
}

.reactor-vessel {
  position: relative;
  width: 80%;
  height: 70%;
  margin: 0 auto;
}

.vessel-shell {
  position: absolute;
  width: 100%;
  height: 100%;
  background: #3a3a5a;
  border-radius: 50%;
  border: 3px solid #5a5a7a;
  overflow: hidden;
}

.internal-agitation {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 60%;
  height: 60%;
  transition: transform 0.5s ease;
}

.agitator-blade {
  position: absolute;
  width: 3px;
  height: 20px;
  background: #64ff8f;
  top: 50%;
  left: 50%;
  transform-origin: center bottom;
}

.agitator-blade:nth-child(1) { transform: translate(-50%, -50%) rotate(0deg); }
.agitator-blade:nth-child(2) { transform: translate(-50%, -50%) rotate(60deg); }
.agitator-blade:nth-child(3) { transform: translate(-50%, -50%) rotate(120deg); }
.agitator-blade:nth-child(4) { transform: translate(-50%, -50%) rotate(180deg); }
.agitator-blade:nth-child(5) { transform: translate(-50%, -50%) rotate(240deg); }
.agitator-blade:nth-child(6) { transform: translate(-50%, -50%) rotate(300deg); }

.reaction-chamber {
  position: absolute;
  top: 10%;
  left: 10%;
  width: 80%;
  height: 80%;
  border-radius: 50%;
  transition: background 0.5s ease;
}

.chemical-reaction {
  position: absolute;
  width: 100%;
  height: 100%;
}

.reaction-bubble {
  position: absolute;
  background: rgba(255, 255, 255, 0.8);
  border-radius: 50%;
  animation: bubbleRise 3s infinite linear;
  opacity: 0;
}

@keyframes bubbleRise {
  0% {
    transform: translateY(0) scale(0.5);
    opacity: 0;
  }
  20% {
    opacity: 0.7;
  }
  80% {
    opacity: 0.3;
  }
  100% {
    transform: translateY(-40px) scale(1.2);
    opacity: 0;
  }
}

.heating-jacket {
  position: absolute;
  top: -10%;
  left: -10%;
  width: 120%;
  height: 120%;
  border-radius: 50%;
  background: radial-gradient(circle, rgba(255, 100, 100, 0.3), transparent 70%);
  transition: opacity 0.5s ease;
}

.heat-element {
  position: absolute;
  width: 8px;
  height: 8px;
  background: #ff4164;
  border-radius: 50%;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
}

.reactor-controls {
  position: absolute;
  bottom: -20%;
  left: 50%;
  transform: translateX(-50%);
}

.control-panel {
  background: #2a2a4a;
  padding: 8px 12px;
  border-radius: 4px;
  border: 1px solid #64b5ff;
  display: flex;
  align-items: center;
  gap: 8px;
}

.indicator-light {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  transition: all 0.3s ease;
}

.indicator-light.normal {
  background: #64ff8f;
  box-shadow: 0 0 6px #64ff8f;
}

.indicator-light.warning {
  background: #ff4164;
  box-shadow: 0 0 8px #ff4164;
  animation: warningPulse 1s infinite;
}

@keyframes warningPulse {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.5; }
}

.pressure-readout {
  font-size: 0.7rem;
  color: #64b5ff;
  font-weight: 600;
}

/* 蒸馏塔系统 */
.distillation-system {
  position: absolute;
  right: 5%;
  width: 25%;
  height: 90%;
}

.distillation-column {
  position: relative;
  width: 40%;
  height: 100%;
  margin: 0 auto;
  background: #2a2a4a;
  border-radius: 8px;
  overflow: hidden;
}

.column-trays {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  padding: 5% 0;
}

.tray {
  width: 100%;
  height: 4%;
  background: rgba(100, 181, 255, 0.3);
  border-radius: 1px;
  transition: opacity 0.5s ease;
}

.temperature-gradient {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  transition: background 0.5s ease;
}

.vapor-flow {
  position: absolute;
  top: 0;
  left: 50%;
  transform: translateX(-50%);
  width: 60%;
  height: 100%;
}

.vapor-particle {
  position: absolute;
  width: 6px;
  height: 6px;
  background: rgba(255, 255, 255, 0.6);
  border-radius: 50%;
  animation: vaporFlow 4s infinite linear;
  opacity: 0;
  animation-delay: calc(var(--flow-intensity, 0.5) * 2s);
}

@keyframes vaporFlow {
  0% {
    transform: translateY(0) scale(0.8);
    opacity: 0;
  }
  20% {
    opacity: 0.6;
  }
  80% {
    opacity: 0.2;
  }
  100% {
    transform: translateY(-100px) scale(1.2);
    opacity: 0;
  }
}

.condenser-unit {
  position: absolute;
  top: -15%;
  left: 50%;
  transform: translateX(-50%);
  width: 80%;
  height: 20%;
}

.condenser-tubes {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  grid-template-rows: repeat(3, 1fr);
  gap: 4px;
}

.cooling-tube {
  background: rgba(100, 200, 255, 0.3);
  border-radius: 1px;
  transition: opacity 0.5s ease;
}

/* 能源中心 */
.energy-center {
  position: absolute;
  bottom: 15%;
  right: 5%;
  width: 25%;
  height: 40%;
}

.turbine-hall {
  position: relative;
  width: 100%;
  height: 60%;
}

.power-turbine {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 60px;
  height: 60px;
  transition: transform 0.3s ease;
}

.turbine-rotor {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 80%;
  height: 80%;
}

.rotor-blade {
  position: absolute;
  width: 3px;
  height: 12px;
  background: #64b5ff;
  top: 50%;
  left: 50%;
  transform-origin: center bottom;
}

.rotor-blade:nth-child(1) { transform: translate(-50%, -50%) rotate(0deg); }
.rotor-blade:nth-child(2) { transform: translate(-50%, -50%) rotate(30deg); }
.rotor-blade:nth-child(3) { transform: translate(-50%, -50%) rotate(60deg); }
.rotor-blade:nth-child(4) { transform: translate(-50%, -50%) rotate(90deg); }
.rotor-blade:nth-child(5) { transform: translate(-50%, -50%) rotate(120deg); }
.rotor-blade:nth-child(6) { transform: translate(-50%, -50%) rotate(150deg); }
.rotor-blade:nth-child(7) { transform: translate(-50%, -50%) rotate(180deg); }
.rotor-blade:nth-child(8) { transform: translate(-50%, -50%) rotate(210deg); }
.rotor-blade:nth-child(9) { transform: translate(-50%, -50%) rotate(240deg); }
.rotor-blade:nth-child(10) { transform: translate(-50%, -50%) rotate(270deg); }
.rotor-blade:nth-child(11) { transform: translate(-50%, -50%) rotate(300deg); }
.rotor-blade:nth-child(12) { transform: translate(-50%, -50%) rotate(330deg); }

.turbine-casing {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: #3a3a5a;
  border-radius: 50%;
  border: 3px solid #5a5a7a;
}

.steam-inlet {
  position: absolute;
  top: -20%;
  left: 50%;
  transform: translateX(-50%);
}

.steam-jet {
  position: absolute;
  width: 8px;
  height: 8px;
  background: rgba(255, 255, 255, 0.7);
  border-radius: 50%;
  animation: steamJet 2s infinite linear;
  opacity: 0;
}

@keyframes steamJet {
  0% {
    transform: translateY(0) scale(0.8);
    opacity: 0;
  }
  20% {
    opacity: 0.8;
  }
  80% {
    opacity: 0.2;
  }
  100% {
    transform: translateY(-25px) scale(1.2);
    opacity: 0;
  }
}

.generator-unit {
  position: absolute;
  top: 70%;
  left: 50%;
  transform: translateX(-50%);
  width: 40px;
  height: 40px;
}

.generator-stator {
  position: absolute;
  width: 100%;
  height: 100%;
  background: #4a4a6a;
  border-radius: 8px;
  border: 2px solid #5a5a7a;
}

.generator-rotor {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 60%;
  height: 60%;
  background: #64b5ff;
  border-radius: 50%;
  transition: transform 0.3s ease;
}

/* 热交换系统 */
.heat-exchange-system {
  position: absolute;
  top: 60%;
  width: 100%;
  height: 35%;
}

.heat-exchanger {
  position: relative;
  width: 80%;
  height: 80%;
  margin: 0 auto;
  background: #3a3a5a;
  border-radius: 8px;
  overflow: hidden;
}

.exchanger-tubes {
  position: absolute;
  top: 10%;
  left: 10%;
  width: 80%;
  height: 80%;
}

.tube-bundle {
  display: grid;
  grid-template-columns: repeat(6, 1fr);
  grid-template-rows: repeat(4, 1fr);
  gap: 4px;
  width: 100%;
  height: 100%;
}

.heat-tube {
  background: var(--temp-color, #64b5ff);
  border-radius: 1px;
  transition: background 0.5s ease;
}

.thermal-fluid {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: radial-gradient(circle at 30% 30%, rgba(255, 100, 100, 0.4), transparent 50%);
  transition: opacity 0.5s ease;
}

/* 环境控制系统 */
.environmental-control {
  position: absolute;
  bottom: 15%;
  left: 70%;
  width: 20%;
  height: 30%;
}

.humidity-control-tower {
  position: relative;
  width: 100%;
  height: 60%;
}

.humidification-system {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: #3a3a5a;
  border-radius: 8px;
  overflow: hidden;
}

.water-spray {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
}

.water-droplet {
  position: absolute;
  width: 3px;
  height: 3px;
  background: rgba(100, 200, 255, 0.8);
  border-radius: 50%;
  animation: waterSpray 3s infinite linear;
  opacity: 0;
}

@keyframes waterSpray {
  0% {
    transform: translateY(0) scale(0.5);
    opacity: 0;
  }
  20% {
    opacity: 0.7;
  }
  80% {
    opacity: 0.2;
  }
  100% {
    transform: translateY(60px) scale(1);
    opacity: 0;
  }
}

.air-flow {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  padding: 10% 0;
}

.air-stream {
  width: 100%;
  height: 8%;
  background: rgba(255, 255, 255, 0.1);
  border-radius: 2px;
  transition: opacity 0.5s ease;
}

.pressure-regulation {
  position: absolute;
  top: 70%;
  width: 100%;
  height: 25%;
}

.pressure-vessels {
  display: flex;
  justify-content: space-around;
  width: 100%;
  height: 100%;
}

.pressure-tank {
  position: relative;
  width: 25%;
  height: 100%;
  background: #3a3a5a;
  border-radius: 4px;
  overflow: hidden;
}

.tank-pressure {
  position: absolute;
  bottom: 0;
  width: 100%;
  height: calc(var(--pressure-fill, 0.5) * 100%);
  background: linear-gradient(to top, #64b5ff, #4078ff);
  transition: height 0.5s ease;
}

.safety-valve {
  position: absolute;
  top: -10%;
  left: 50%;
  transform: translateX(-50%);
  width: 12px;
  height: 8px;
  background: #4a4a6a;
  border-radius: 2px;
  transition: all 0.3s ease;
}

.safety-valve.active {
  background: #ff4164;
}

.valve-release {
  position: absolute;
  top: -8px;
  left: 50%;
  transform: translateX(-50%);
  width: 6px;
  height: 6px;
  background: rgba(255, 100, 100, 0.6);
  border-radius: 50%;
  animation: valveRelease 1s infinite;
}

@keyframes valveRelease {
  0%, 100% { transform: translateX(-50%) scale(1); opacity: 0.7; }
  50% { transform: translateX(-50%) scale(1.3); opacity: 1; }
}

/* 管道网络系统 */
.advanced-piping-network {
  position: absolute;
  bottom: 50%;
  left: 0;
  width: 100%;
  height: 20%;
}

.main-process-lines {
  position: absolute;
  top: 0;
  left: 10%;
  width: 80%;
  height: 60%;
  display: flex;
  justify-content: space-between;
}

.process-pipe {
  position: relative;
  width: 12%;
  height: 100%;
}

.pipe-section {
  position: absolute;
  width: 100%;
  height: 8px;
  background: #3a3a5a;
  border-radius: 4px;
  top: 50%;
  transform: translateY(-50%);
  overflow: hidden;
}

.fluid-indicator {
  position: absolute;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, #64b5ff, #4078ff);
  opacity: 0.6;
}

.flow-animation {
  position: absolute;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.4), transparent);
  animation: pipeFlow 2s infinite linear;
  animation-duration: calc(2s / var(--flow-velocity, 1));
}

@keyframes pipeFlow {
  0% { transform: translateX(-100%); }
  100% { transform: translateX(100%); }
}

.pipe-valves {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
}

.control-valve {
  width: 12px;
  height: 12px;
  background: #64b5ff;
  border-radius: 2px;
  transition: transform 0.3s ease;
}

.utility-lines {
  position: absolute;
  top: 70%;
  left: 15%;
  width: 70%;
  height: 20%;
  display: flex;
  justify-content: space-between;
}

.utility-pipe {
  position: relative;
  width: 20%;
  height: 6px;
  background: #4a4a6a;
  border-radius: 3px;
}

.insulation-layer {
  position: absolute;
  top: -2px;
  left: -2px;
  right: -2px;
  bottom: -2px;
  background: rgba(255, 200, 100, 0.2);
  border-radius: 5px;
  transition: opacity 0.5s ease;
}

/* 监测和控制系统 */
.monitoring-system {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  pointer-events: none;
}

/* 热成像监测 */
.thermal-imaging-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
}

.thermal-hotspot {
  position: absolute;
  width: 35px;
  height: 35px;
  background: radial-gradient(circle, 
    hsla(var(--thermal-color, 30), 100%, 50%, var(--thermal-intensity, 0.5)), 
    transparent 70%);
  border-radius: 50%;
  transform: translate(-50%, -50%);
  animation: thermalHotspot 4s infinite alternate;
  filter: blur(2px);
}

@keyframes thermalHotspot {
  0% { transform: translate(-50%, -50%) scale(1); }
  100% { transform: translate(-50%, -50%) scale(1.3); }
}

/* 传感器网络 */
.sensor-array {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
}

.sensor-cluster {
  position: absolute;
  transform: translate(-50%, -50%);
}

.sensor-node {
  position: relative;
  width: 12px;
  height: 12px;
  background: #3a3a5a;
  border-radius: 50%;
  border: 2px solid #64ff8f;
  transition: all 0.3s ease;
}

.sensor-node.warning {
  border-color: #ff4164;
  animation: sensorWarning 1s infinite;
}

@keyframes sensorWarning {
  0%, 100% { border-color: #ff4164; box-shadow: 0 0 8px #ff4164; }
  50% { border-color: #ff8a9e; box-shadow: 0 0 12px #ff8a9e; }
}

.sensor-probe {
  position: absolute;
  width: 4px;
  height: 4px;
  background: #64ff8f;
  border-radius: 50%;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
}

.sensor-node.warning .sensor-probe {
  background: #ff4164;
}

.data-transmission {
  position: absolute;
  top: -15px;
  left: 50%;
  transform: translateX(-50%);
}

.data-packet {
  position: absolute;
  width: 2px;
  height: 2px;
  background: #64b5ff;
  border-radius: 50%;
  animation: dataTransmit 2s infinite linear;
  opacity: 0;
}

.data-packet:nth-child(1) { animation-delay: 0s; }
.data-packet:nth-child(2) { animation-delay: 0.4s; }
.data-packet:nth-child(3) { animation-delay: 0.8s; }

@keyframes dataTransmit {
  0% {
    transform: translateY(0);
    opacity: 0;
  }
  20% {
    opacity: 1;
  }
  80% {
    opacity: 0.3;
  }
  100% {
    transform: translateY(-12px);
    opacity: 0;
  }
}

.sensor-reading {
  position: absolute;
  top: -30px;
  left: 50%;
  transform: translateX(-50%);
  font-size: 0.6rem;
  color: #64ff8f;
  white-space: nowrap;
  font-weight: 600;
  text-shadow: 0 0 3px rgba(0, 0, 0, 0.8);
  background: rgba(0, 0, 0, 0.5);
  padding: 2px 4px;
  border-radius: 2px;
}

.humidity-indicator {
  height: 2px;
  background: #64b5ff;
  margin-top: 1px;
  border-radius: 1px;
  transition: width 0.5s ease;
}

/* 环境参数可视化 */
.environmental-visualization {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  pointer-events: none;
}

.temperature-field {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: radial-gradient(circle at 30% 40%, rgba(255, 100, 100, 0.1), transparent 50%);
  transition: opacity 0.5s ease;
}

.humidity-mist {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  transition: opacity 0.5s ease;
}

.mist-particle {
  position: absolute;
  width: 20px;
  height: 20px;
  background: radial-gradient(circle, rgba(255, 255, 255, 0.1), transparent 70%);
  border-radius: 50%;
  animation: mistFloat 8s infinite linear;
  opacity: 0;
}

@keyframes mistFloat {
  0% {
    transform: translate(0, 0) scale(0.8);
    opacity: 0;
  }
  10% {
    opacity: 0.3;
  }
  90% {
    opacity: 0.1;
  }
  100% {
    transform: translate(100px, -50px) scale(1.2);
    opacity: 0;
  }
}

.pressure-wave {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
}

.wave-front {
  position: absolute;
  width: calc(var(--pressure-wave, 0.8) * 100px);
  height: calc(var(--pressure-wave, 0.8) * 100px);
  border: 1px solid rgba(100, 181, 255, 0.3);
  border-radius: 50%;
  animation: pressurePulse 3s infinite;
  opacity: 0;
}

.wave-front:nth-child(1) { animation-delay: 0s; }
.wave-front:nth-child(2) { animation-delay: 1s; }
.wave-front:nth-child(3) { animation-delay: 2s; }

@keyframes pressurePulse {
  0% {
    transform: scale(0.5);
    opacity: 0.7;
  }
  100% {
    transform: scale(2);
    opacity: 0;
  }
}

/* 动态照明系统 */
.dynamic-lighting {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  pointer-events: none;
}

.safety-lighting {
  position: absolute;
  bottom: 5%;
  left: 5%;
  width: 90%;
  height: 10%;
  display: flex;
  justify-content: space-between;
}

.emergency-light {
  width: 6px;
  height: 6px;
  background: #ff4164;
  border-radius: 50%;
  box-shadow: 0 0 8px #ff4164;
  animation: safetyBlink 3s infinite;
}

@keyframes safetyBlink {
  0%, 100% { opacity: 0.3; }
  50% { opacity: 1; }
}

.process-lighting {
  position: absolute;
  top: 20%;
  left: 10%;
  width: 80%;
  height: 60%;
  display: grid;
  grid-template-columns: repeat(6, 1fr);
  grid-template-rows: repeat(4, 1fr);
  gap: 10px;
  transition: opacity 0.5s ease;
}

.work-light {
  background: rgba(255, 255, 255, 0.1);
  border-radius: 1px;
  box-shadow: 0 0 10px rgba(255, 255, 255, 0.2);
}

.status-lighting {
  position: absolute;
  top: 10%;
  right: 10%;
}

.status-indicator {
  width: 12px;
  height: 12px;
  border-radius: 50%;
  transition: all 0.3s ease;
}

.status-indicator.normal {
  background: #64ff8f;
  box-shadow: 0 0 10px #64ff8f;
}

.status-indicator.caution {
  background: #ffc107;
  box-shadow: 0 0 10px #ffc107;
  animation: cautionPulse 2s infinite;
}

.status-indicator.warning {
  background: #ff4164;
  box-shadow: 0 0 12px #ff4164;
  animation: warningPulse 1s infinite;
}

@keyframes cautionPulse {
  0%, 100% { opacity: 0.7; }
  50% { opacity: 1; }
}

/* 控制按钮 */
.control-buttons {
  display: flex;
  gap: 8px;
  align-items: center;
}

.control-btn {
  width: 32px;
  height: 32px;
  background: rgba(64, 120, 255, 0.2);
  border: 1px solid rgba(64, 120, 255, 0.3);
  border-radius: 6px;
  color: #64b5ff;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s ease;
}

.control-btn:hover {
  background: rgba(64, 120, 255, 0.3);
  transform: translateY(-1px);
}

.zoom-level {
  font-size: 0.8rem;
  color: #b0b0ff;
  margin-left: 10px;
}

/* 操作按钮 */
.action-section {
  text-align: center;
  margin-bottom: 20px;
}

.start-button {
  position: relative;
  background: linear-gradient(135deg, #4078ff 0%, #7040ff 100%);
  color: white;
  border: none;
  padding: 14px 40px;
  font-size: 1.1rem;
  border-radius: 30px;
  cursor: pointer;
  transition: all 0.3s;
  font-weight: 600;
  box-shadow: 0 6px 20px rgba(64, 120, 255, 0.4);
  overflow: hidden;
  z-index: 1;
}

.start-button:hover {
  transform: translateY(-3px);
  box-shadow: 0 8px 25px rgba(64, 120, 255, 0.6);
}

.start-button.active {
  background: linear-gradient(135deg, #ff4164 0%, #ff4b2b 100%);
  box-shadow: 0 6px 20px rgba(255, 65, 100, 0.4);
}

.start-button.active:hover {
  box-shadow: 0 8px 25px rgba(255, 65, 100, 0.6);
}

.button-glow {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.2), transparent);
  transform: translateX(-100%);
}

.start-button:hover .button-glow {
  animation: shine 1.5s;
}

@keyframes shine {
  0% { transform: translateX(-100%); }
  100% { transform: translateX(100%); }
}

.button-pulse {
  position: absolute;
  width: 100%;
  height: 100%;
  border-radius: 30px;
  top: 0;
  left: 0;
  background: rgba(255, 255, 255, 0.2);
  animation: button-pulse 2s infinite;
  z-index: -1;
}

@keyframes button-pulse {
  0% {
    transform: scale(1);
    opacity: 0.7;
  }
  70% {
    transform: scale(1.1);
    opacity: 0;
  }
  100% {
    transform: scale(1);
    opacity: 0;
  }
}

/* 变量控制 */
.variable-controls {
  background: rgba(30, 32, 56, 0.6);
  border-radius: 16px;
  padding: 20px;
  border: 1px solid rgba(64, 120, 255, 0.15);
  backdrop-filter: blur(10px);
}

.variable-controls h3 {
  margin-top: 0;
  margin-bottom: 20px;
  color: #b0b0ff;
  font-size: 1rem;
  font-weight: 600;
}

.control-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
}

.control-item {
  background: rgba(40, 42, 66, 0.8);
  border-radius: 12px;
  padding: 15px;
  border: 1px solid rgba(64, 120, 255, 0.1);
}

.control-header {
  display: flex;
  align-items: center;
  margin-bottom: 15px;
}

.control-icon {
  font-size: 1.3rem;
  margin-right: 12px;
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(64, 120, 255, 0.2);
  border-radius: 8px;
}

.control-info {
  flex: 1;
}

.control-info label {
  display: block;
  font-weight: 600;
  color: #b0b0ff;
  font-size: 0.9rem;
  margin-bottom: 4px;
}

.control-value {
  font-weight: 700;
  color: #64b5ff;
  text-shadow: 0 0 8px rgba(100, 181, 255, 0.5);
  font-size: 1.1rem;
}

.slider-container {
  position: relative;
  height: 30px;
  display: flex;
  align-items: center;
}

.slider {
  position: absolute;
  width: 100%;
  height: 6px;
  -webkit-appearance: none;
  background: transparent;
  z-index: 2;
  outline: none;
  cursor: pointer;
}

.slider::-webkit-slider-thumb {
  -webkit-appearance: none;
  width: 18px;
  height: 18px;
  border-radius: 50%;
  background: #64b5ff;
  cursor: pointer;
  box-shadow: 0 0 10px rgba(100, 181, 255, 0.8);
  border: 2px solid #e0e0ff;
  transition: all 0.2s ease;
}

.slider::-webkit-slider-thumb:hover {
  transform: scale(1.1);
  box-shadow: 0 0 15px rgba(100, 181, 255, 1);
}

.slider-track {
  position: absolute;
  width: 100%;
  height: 6px;
  background: rgba(64, 120, 255, 0.1);
  border-radius: 3px;
}

.slider-fill {
  position: absolute;
  width: var(--value, 50%);
  height: 6px;
  background: linear-gradient(90deg, #4078ff, #7040ff);
  border-radius: 3px;
  box-shadow: 0 0 8px rgba(64, 120, 255, 0.6);
  transition: width 0.3s ease;
}

.slider-glow {
  position: absolute;
  width: var(--value, 50%);
  height: 12px;
  background: rgba(64, 120, 255, 0.2);
  border-radius: 6px;
  top: 50%;
  transform: translateY(-50%);
  filter: blur(4px);
  transition: width 0.3s ease;
}

/* 右侧面板样式 - 模型测试台 */
.model-testing-panel {
  background: #0f172a !important;
  color: #e2e8f0;
}

.model-testing-panel .panel-header h2 {
  background: none;
  -webkit-text-fill-color: #e0e0ff;
  color: #e0e0ff;
}

/* 响应式设计 */
@media (max-width: 1400px) {
  .control-grid {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 1200px) {
  .virtual-lab {
    flex-direction: column;
    height: auto;
  }
  
  .left-panel, .center-panel, .right-panel {
    width: 100%;
  }
}

/* 模型测试台特定样式 */
.progress-fill { transition: width 0.2s linear; }
.dashed-box {
  background-image: url("data:image/svg+xml,%3csvg width='100%25' height='100%25' xmlns='http://www.w3.org/2000/svg'%3e%3crect width='100%25' height='100%25' fill='none' rx='8' ry='8' stroke='%23334155FF' stroke-width='2' stroke-dasharray='10%2c 10' stroke-dashoffset='0' stroke-linecap='square'/%3e%3c/svg%3e");
  transition: all 0.3s ease;
}
.dashed-box:hover {
  background-image: url("data:image/svg+xml,%3csvg width='100%25' height='100%25' xmlns='http://www.w3.org/2000/svg'%3e%3crect width='100%25' height='100%25' fill='none' rx='8' ry='8' stroke='%233B82F6FF' stroke-width='2' stroke-dasharray='10%2c 10' stroke-dashoffset='0' stroke-linecap='square'/%3e%3c/svg%3e");
  background-color: rgba(30, 41, 59, 0.5);
}
.terminal-logs { font-family: 'Consolas', 'Monaco', monospace; font-size: 11px; line-height: 1.4; }
.log-entry { animation: fadeIn 0.2s ease; }
@keyframes fadeIn { from { opacity: 0; transform: translateY(2px); } to { opacity: 1; transform: translateY(0); } }

/* 按钮动画 */
.btn-icon-anim { transition: transform 0.2s; }
button:active .btn-icon-anim { transform: scale(0.9); }

/* 模态框样式 */
#modal {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.8);
  backdrop-filter: blur(10px);
  z-index: 50;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: opacity 0.3s;
}

#modal.hidden {
  display: none;
}

#modal.opacity-0 {
  opacity: 0;
}

#modal-content {
  background: #1e293b;
  border: 1px solid #475569;
  border-radius: 12px;
  width: 500px;
  box-shadow: 0 25px 50px -12px rgba(0, 0, 0, 0.5);
  transform: scale(0.95);
  transition: transform 0.3s;
  overflow: hidden;
}

#modal-content.scale-95 {
  transform: scale(0.95);
}

/* 确保Tailwind样式生效 */
.text-\[10px\] {
  font-size: 10px;
}
.text-\[11px\] {
  font-size: 11px;
}
.bg-slate-900 {
  background-color: #0f172a;
}
.bg-slate-800 {
  background-color: #1e293b;
}
.bg-slate-700 {
  background-color: #334155;
}
.border-slate-700 {
  border-color: #334155;
}
.border-slate-800 {
  border-color: #1e293b;
}
.text-slate-400 {
  color: #94a3b8;
}
.text-slate-300 {
  color: #cbd5e1;
}
.text-slate-200 {
  color: #e2e8f0;
}
.text-slate-500 {
  color: #64748b;
}
.text-slate-600 {
  color: #475569;
}
.bg-blue-600 {
  background-color: #2563eb;
}
.bg-blue-500 {
  background-color: #3b82f6;
}
.text-blue-400 {
  color: #60a5fa;
}
.bg-orange-500 {
  background-color: #f97316;
}
.bg-yellow-500 {
  background-color: #eab308;
}
.bg-green-500 {
  background-color: #22c55e;
}
.text-orange-400 {
  color: #fb923c;
}
.text-green-400 {
  color: #4ade80;
}
.text-yellow-400 {
  color: #facc15;
}
.text-red-400 {
  color: #f87171;
}
.text-purple-400 {
  color: #c084fc;
}
.bg-slate-900\/50 {
  background-color: rgba(15, 23, 42, 0.5);
}
.bg-slate-800\/30 {
  background-color: rgba(30, 41, 59, 0.3);
}
.bg-slate-800\/50 {
  background-color: rgba(30, 41, 59, 0.5);
}
.bg-slate-700\/50 {
  background-color: rgba(51, 65, 85, 0.5);
}
.hover\:bg-slate-800:hover {
  background-color: #1e293b;
}
.hover\:bg-blue-600:hover {
  background-color: #2563eb;
}
.hover\:bg-blue-500:hover {
  background-color: #3b82f6;
}
.hover\:text-white:hover {
  color: #ffffff;
}
.hover\:text-yellow-400:hover {
  color: #facc15;
}
.hover\:text-red-400:hover {
  color: #f87171;
}
.shadow-lg {
  box-shadow: 0 10px 15px -3px rgba(0, 0, 0, 0.1), 0 4px 6px -2px rgba(0, 0, 0, 0.05);
}
.shadow-blue-500\/30 {
  box-shadow: 0 0 20px rgba(59, 130, 246, 0.3);
}
.animate-\[fadeIn_0\.3s_ease-out\] {
  animation: fadeIn 0.3s ease-out;
}
.animate-pulse {
  animation: pulse 2s cubic-bezier(0.4, 0, 0.6, 1) infinite;
}
.animate-spin-slow {
  animation: spin 3s linear infinite;
}
/* 修复模态框字体颜色 */
.modal-body input, 
.modal-body select, 
.modal-body textarea {
  color: white !important;
  background-color: #1e293b !important;
  border: 1px solid #475569 !important;
}

.modal-body input::placeholder {
  color: #64748b !important;
}

.modal-body option {
  background-color: #1e293b;
  color: white;
}

/* 确保图表容器正确显示 */
#models-container canvas {
  width: 100% !important;
  height: 96px !important;
  display: block;
  transition: opacity 0.3s ease;
}


/* 修复模型卡片样式 */
.model-card {
  background: #1e293b;
  border: 1px solid #334155;
  border-radius: 8px;
  margin-bottom: 12px;
  overflow: hidden;
  transition: all 0.3s ease;
}
.loss-value {
  transition: color 0.3s ease;
}

.model-card-header {
  background: rgba(30, 41, 59, 0.5);
  padding: 12px;
  border-bottom: 1px solid #334155;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.model-card-body {
  padding: 0;
}

.chart-container {
  position: relative;
  height: 96px;
  width: 100%;
}

.terminal-container {
  height: 96px;
  background: black;
  padding: 8px;
  font-family: 'Courier New', monospace;
  font-size: 11px;
  overflow-y: auto;
}
@keyframes spin {
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
  }
}
/* 原有的完整CSS样式保持不变 */
/* 由于代码长度限制，这里省略了完整的CSS样式，但确保包含所有原有的样式 */
/* 包括：.virtual-lab, .background-effects, .left-panel, .center-panel, .right-panel */
/* 以及所有3D工厂的详细样式 */

.connection-status {
  position: fixed;
  top: 10px;
  right: 10px;
  padding: 8px 12px;
  border-radius: 6px;
  font-size: 12px;
  display: flex;
  align-items: center;
  gap: 6px;
  z-index: 1000;
  backdrop-filter: blur(10px);
}

.connection-status.connected {
  background: rgba(34, 197, 94, 0.2);
  border: 1px solid rgba(34, 197, 94, 0.5);
}

.connection-status.connecting {
  background: rgba(251, 191, 36, 0.2);
  border: 1px solid rgba(251, 191, 36, 0.5);
}

.connection-status.disconnected {
  background: rgba(100, 116, 139, 0.2);
  border: 1px solid rgba(100, 116, 139, 0.5);
}

.connection-status.error {
  background: rgba(239, 68, 68, 0.2);
  border: 1px solid rgba(239, 68, 68, 0.5);
}

.status-dot {
  width: 6px;
  height: 6px;
  border-radius: 50%;
}

.connected .status-dot { background: #22c55e; }
.connecting .status-dot { background: #fbbf24; animation: pulse 1.5s infinite; }
.disconnected .status-dot { background: #64748b; }
.error .status-dot { background: #ef4444; animation: pulse 0.5s infinite; }

/* 原有的完整CSS样式从这里开始 */
/* 由于代码长度限制，这里只显示连接状态样式，完整的CSS样式需要包含 */
</style>