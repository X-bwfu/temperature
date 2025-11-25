<template>
  <div class="main-container">
    <!-- 顶部导航栏 -->
    <header class="navbar">
      <div class="logo">工业测温平台</div>
      <nav class="nav-buttons">
        <el-button type="text" @click="goPage('first')">首页</el-button>
        <el-button type="text" @click="goPage('dashboard')">大屏展示</el-button>
        <el-button type="text" @click="goPage('simulate')">工业模拟</el-button>
        <el-button type="text" class="active">个人信息</el-button>
      </nav>
    </header>

    <!-- 内容区域 -->
    <section class="content-section">
      <div class="profile-header">
        <h2 class="section-title">个人信息管理</h2>
        <p class="section-subtitle">管理您的账户信息和安全设置</p>
      </div>
      
      <div class="profile-content">
        <!-- 基本信息区域 -->
        <div class="info-section">
          <div class="section-header">
            <div class="header-icon">👤</div>
            <h3>基本信息</h3>
          </div>
          
          <div class="form-container">
            <div class="form-group">
              <label class="form-label">用户名</label>
              <div class="input-wrapper">
                <el-input 
                  v-model="userInfo.username" 
                  placeholder="请输入用户名"
                  clearable
                  size="large"
                  :class="{ 'error-input': usernameError }"
                ></el-input>
                <div class="input-tip" :class="{ 'error-text': usernameError }">
                  {{ usernameError || '用户名长度在3到20个字符' }}
                </div>
              </div>
            </div>
            
            
            
            <div class="action-buttons">
              <el-button 
                type="primary" 
                size="large" 
                @click="saveBasicInfo" 
                class="save-btn"
                :loading="basicInfoLoading"
                :disabled="!isBasicInfoValid"
              >
                <span class="btn-icon">💾</span>
                保存修改
              </el-button>
            </div>
          </div>
        </div>

        <!-- 密码修改区域 -->
        <div class="info-section">
          <div class="section-header">
            <div class="header-icon">🔒</div>
            <h3>修改密码</h3>
          </div>
          
          <div class="form-container">
            <div class="form-group">
              <label class="form-label">当前密码</label>
              <div class="input-wrapper">
                <el-input 
                  v-model="passwordInfo.currentPassword" 
                  type="password"
                  placeholder="请输入当前密码"
                  show-password
                  size="large"
                  :class="{ 'error-input': currentPasswordError }"
                ></el-input>
                <div class="input-tip" :class="{ 'error-text': currentPasswordError }">
                  {{ currentPasswordError || '请输入当前密码进行验证' }}
                </div>
              </div>
            </div>
            
            <div class="form-group">
              <label class="form-label">新密码</label>
              <div class="input-wrapper">
                <el-input 
                  v-model="passwordInfo.newPassword" 
                  type="password"
                  placeholder="请输入新密码"
                  show-password
                  size="large"
                  :class="{ 'error-input': newPasswordError }"
                ></el-input>
                <div class="password-strength" :class="getPasswordStrengthClass">
                  {{ newPasswordError || getPasswordStrengthText }}
                </div>
              </div>
            </div>
            
            <div class="form-group">
              <label class="form-label">确认密码</label>
              <div class="input-wrapper">
                <el-input 
                  v-model="passwordInfo.confirmPassword" 
                  type="password"
                  placeholder="请再次输入新密码"
                  show-password
                  size="large"
                  :class="{ 'error-input': confirmPasswordError }"
                ></el-input>
                <div class="password-match" :class="{ 
                  'match': isPasswordMatch && !confirmPasswordError, 
                  'mismatch': confirmPasswordError 
                }">
                  {{ confirmPasswordError || getPasswordMatchText }}
                </div>
              </div>
            </div>
            
            <div class="action-buttons">
              <el-button 
                type="primary" 
                size="large" 
                @click="changePassword" 
                class="save-btn" 
                :disabled="!isPasswordValid"
                :loading="passwordLoading"
              >
                <span class="btn-icon">🔄</span>
                修改密码
              </el-button>
            </div>
          </div>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted, watch } from "vue";
import { useRouter } from "vue-router";
import { ElMessage, ElMessageBox } from 'element-plus';
import { useUserStore } from '../stores/my-user.ts';
const router = useRouter();
const userStore = useUserStore();

const updateUserInfo = async () => {
  try {
    // 注意：根据后端接口，修改用户信息可能需要重新设计后端接口
    // 当前后端没有提供修改用户信息的接口，只有注册接口
    ElMessage.warning('用户信息修改功能需要后端支持，当前后端暂无更新接口');
    return false;
  } catch (error) {
    ElMessage.error('更新失败：网络错误');
    return false;
  }
};



// 用户信息
const userInfo = reactive({
  username: '',
  email: ''
});

// 密码修改信息
const passwordInfo = reactive({
  currentPassword: '',
  newPassword: '',
  confirmPassword: ''
});

// 加载状态
const basicInfoLoading = ref(false);
const passwordLoading = ref(false);

// 错误信息
const usernameError = ref('');
const emailError = ref('');
const currentPasswordError = ref('');
const newPasswordError = ref('');
const confirmPasswordError = ref('');



// 验证函数
const validateUsername = (username: string) => {
  if (!username.trim()) {
    return '用户名不能为空';
  }
  if (username.length < 3 || username.length > 20) {
    return '用户名长度必须在3到20个字符之间';
  }
  return '';
};



const validatePassword = (password: string) => {
  if (!password) {
    return '密码不能为空';
  }
  if (password.length < 6 || password.length > 20) {
    return '密码长度必须在6到20个字符之间';
  }
  return '';
};

// 计算属性
const getPasswordStrengthClass = computed(() => {
  if (newPasswordError.value) return 'mismatch';
  const password = passwordInfo.newPassword;
  if (!password) return '';
  if (password.length < 6) return 'weak';
  if (password.length < 8) return 'medium';
  return 'strong';
});

const getPasswordStrengthText = computed(() => {
  const password = passwordInfo.newPassword;
  if (!password) return '请输入密码';
  if (password.length < 6) return '密码强度：弱';
  if (password.length < 8) return '密码强度：中';
  return '密码强度：强';
});

const isPasswordMatch = computed(() => {
  return passwordInfo.newPassword === passwordInfo.confirmPassword && passwordInfo.confirmPassword !== '';
});

const getPasswordMatchText = computed(() => {
  if (!passwordInfo.confirmPassword) return '请确认密码';
  return isPasswordMatch.value ? '密码匹配' : '密码不匹配';
});

const isBasicInfoValid = computed(() => {
  return !usernameError.value && !emailError.value && 
         userInfo.username.trim() && userInfo.email.trim();
});

const isPasswordValid = computed(() => {
  return !currentPasswordError.value && !newPasswordError.value && !confirmPasswordError.value &&
         passwordInfo.currentPassword && passwordInfo.newPassword && passwordInfo.confirmPassword &&
         isPasswordMatch.value;
});

// 实时验证
const validateForm = () => {
  usernameError.value = validateUsername(userInfo.username);

  newPasswordError.value = validatePassword(passwordInfo.newPassword);
  
  if (passwordInfo.confirmPassword && !isPasswordMatch.value) {
    confirmPasswordError.value = '两次输入的密码不一致';
  } else {
    confirmPasswordError.value = '';
  }
};








const changeUserPassword = async () => {
  try {
    const response = await fetch('http://localhost:8080/auth/change-password', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/x-www-form-urlencoded',
      },
      // 根据后端接口，需要传递用户名、旧密码和新密码
      body: `username=${encodeURIComponent(userInfo.username)}&oldPassword=${encodeURIComponent(passwordInfo.currentPassword)}&newPassword=${encodeURIComponent(passwordInfo.newPassword)}`
    });

    const result = await response.text();
    
    if (result === 'Password changed successfully') {
      // 更新全局状态中的密码
      userStore.setUserInfo(userStore.uname, passwordInfo.newPassword, userStore.uemail);
      ElMessage.success('密码修改成功');
      return true;
    } else {
      ElMessage.error('密码修改失败：' + result);
      return false;
    }
  } catch (error) {
    ElMessage.error('密码修改失败：网络错误');
    return false;
  }
};

// 修改密码
const changePassword = async () => {
  // 前端验证：检查输入的当前密码是否与用户当前密码匹配
  if (passwordInfo.currentPassword !== userStore.upwd) {
    currentPasswordError.value = '当前密码错误';
    ElMessage.error('当前密码错误，请重新输入');
    return;
  }

  // 验证新密码格式
  newPasswordError.value = validatePassword(passwordInfo.newPassword);
  
  // 验证确认密码是否匹配
  if (passwordInfo.confirmPassword && !isPasswordMatch.value) {
    confirmPasswordError.value = '两次输入的密码不一致';
  }
  
  // 如果有任何错误，停止执行
  if (newPasswordError.value || confirmPasswordError.value) {
    ElMessage.error('请修正表单错误');
    return;
  }

  passwordLoading.value = true;
  try {
    const success = await changeUserPassword();
    if (success) {
      // 清空表单和错误信息
      passwordInfo.currentPassword = '';
      passwordInfo.newPassword = '';
      passwordInfo.confirmPassword = '';
      currentPasswordError.value = '';
      newPasswordError.value = '';
      confirmPasswordError.value = '';
    }
  } finally {
    passwordLoading.value = false;
  }
};
// 保存基本信息
const saveBasicInfo = async () => {
  // 最终验证
  usernameError.value = validateUsername(userInfo.username);

  
  if (usernameError.value || emailError.value) {
    ElMessage.error('请修正表单错误');
    return;
  }

  basicInfoLoading.value = true;
  try {
    const success = await updateUserInfo();
    if (success) {
      // 清空错误信息
      usernameError.value = '';
      emailError.value = '';
    }
  } finally {
    basicInfoLoading.value = false;
  }
};



// 页面跳转
const goPage = (path: string) => {
  if (path === 'home') {
    router.push('/');
  } else {
    router.push(`/${path}`);
  }
};

// 组件挂载时初始化数据
onMounted(() => {
  // 从全局状态初始化数据
  userInfo.username = userStore.uname;
  userInfo.email = userStore.uemail;
});

// 监听输入变化进行实时验证
watch(() => userInfo.username, () => {
  usernameError.value = validateUsername(userInfo.username);
});



watch(() => passwordInfo.currentPassword, () => {
  currentPasswordError.value = '';
});

watch(() => passwordInfo.newPassword, () => {
  newPasswordError.value = validatePassword(passwordInfo.newPassword);
});

watch(() => passwordInfo.confirmPassword, () => {
  if (passwordInfo.confirmPassword && !isPasswordMatch.value) {
    confirmPasswordError.value = '两次输入的密码不一致';
  } else {
    confirmPasswordError.value = '';
  }
});
</script>

<style scoped>
/* 之前的样式保持不变，只添加错误样式 */

.error-input :deep(.el-input__wrapper) {
  box-shadow: 0 0 0 1px #f56c6c inset !important;
}

.error-text {
  color: #f56c6c !important;
}

/* 其他样式保持不变 */
.main-container {
  font-family: "Microsoft YaHei", sans-serif;
  background: linear-gradient(135deg, #2D8CFF 0%, #090a0f 100%);
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  color: #ecf0f1;
}

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
  background-color: rgba(255, 216, 111, 0.2);
  border-radius: 4px;
}

.content-section {
  width: 90%;
  max-width: 800px;
  margin: 40px auto 60px;
  display: flex;
  flex-direction: column;
  gap: 40px;
}

.profile-header {
  text-align: center;
  margin-bottom: 30px;
}

.section-title {
  font-size: 32px;
  font-weight: bold;
  margin-bottom: 10px;
  color: #ecf0f1;
  position: relative;
  display: inline-block;
}

.section-title::after {
  content: '';
  position: absolute;
  bottom: -10px;
  left: 50%;
  transform: translateX(-50%);
  width: 80px;
  height: 3px;
  background: linear-gradient(90deg, #3498db, #9b59b6);
  border-radius: 2px;
}

.section-subtitle {
  color: #bdc3c7;
  font-size: 16px;
  margin-top: 20px;
}

.profile-content {
  display: flex;
  flex-direction: column;
  gap: 40px;
}

.info-section {
  background: linear-gradient(135deg, rgba(44,62,80,0.9), rgba(52,152,219,0.2));
  backdrop-filter: blur(6px);
  border-radius: 20px;
  padding: 30px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.3);
  transition: all 0.5s ease;
  position: relative;
  overflow: hidden;
}

.info-section::before {
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

.info-section:hover {
  transform: translateY(-5px);
  box-shadow: 0 15px 40px rgba(0, 0, 0, 0.4);
}

.section-header {
  display: flex;
  align-items: center;
  margin-bottom: 25px;
  position: relative;
  z-index: 1;
}

.header-icon {
  font-size: 32px;
  margin-right: 15px;
  animation: icon-bounce 2s infinite;
}

@keyframes icon-bounce {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-5px); }
}

.section-header h3 {
  color: #ecf0f1;
  font-size: 22px;
  margin: 0;
  font-weight: 600;
}

.form-container {
  position: relative;
  z-index: 1;
}

.form-group {
  margin-bottom: 25px;
}

.form-label {
  display: block;
  color: #ecf0f1;
  font-weight: 500;
  margin-bottom: 8px;
  font-size: 16px;
}

.input-wrapper {
  position: relative;
}

.input-tip {
  font-size: 12px;
  color: #bdc3c7;
  margin-top: 5px;
}

.password-strength {
  font-size: 12px;
  margin-top: 5px;
  transition: color 0.3s;
}

.password-strength.weak {
  color: #f56c6c;
}

.password-strength.medium {
  color: #e6a23c;
}

.password-strength.strong {
  color: #67c23a;
}

.password-match {
  font-size: 12px;
  margin-top: 5px;
  transition: color 0.3s;
}

.password-match.match {
  color: #67c23a;
}

.password-match.mismatch {
  color: #f56c6c;
}

.action-buttons {
  display: flex;
  justify-content: flex-end;
  margin-top: 30px;
}

.save-btn {
  background: linear-gradient(135deg, #3498db, #9b59b6);
  border: none;
  border-radius: 10px;
  padding: 12px 30px;
  font-weight: 500;
  transition: all 0.3s ease;
}

.save-btn:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 5px 15px rgba(52, 152, 219, 0.4);
}

.save-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.btn-icon {
  margin-right: 8px;
}

@media (max-width: 768px) {
  .navbar {
    padding: 15px 20px;
  }
  
  .content-section {
    width: 95%;
    margin: 20px auto 40px;
  }
  
  .info-section {
    padding: 20px;
  }
  
  .section-title {
    font-size: 28px;
  }
  
  .section-header h3 {
    font-size: 20px;
  }
}

@media (max-width: 480px) {
  .nav-buttons {
    display: flex;
    flex-wrap: wrap;
    gap: 10px;
  }
  
  .nav-buttons .el-button {
    margin-left: 0;
    font-size: 14px;
  }
  
  .action-buttons {
    justify-content: center;
  }
  
  .save-btn {
    width: 100%;
  }
}
</style>