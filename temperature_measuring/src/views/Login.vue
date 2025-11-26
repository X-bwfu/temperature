<template>
<div class="login-container">
  <div class="box">
    <h2>Login in</h2>
    用户名：<el-input
            v-model="uname"
            class="transparent-input"
            placeholder="请输入用户名"
            clearable
          /><br /><br />
    密码： <el-input
             v-model="upwd"
             class="transparent-input"
             type="password"
             placeholder="请输入密码"
             show-password
            /><br /><br />
      <el-button 
        type="primary" 
        class="transparent-btn"
        @click="login">登录</el-button>
      <el-button 
        type="success" 
        class="transparent-btn register-btn"
        @click="showRegisterDialog = true">注册</el-button>
  </div>
  
  <!-- 注册对话框 -->
  <el-dialog 
    v-model="showRegisterDialog" 
    title="用户注册"
    width="500px"
    :close-on-click-modal="false">
    <el-form 
      ref="registerFormRef" 
      :model="registerForm" 
      :rules="registerRules"
      label-width="80px">
      
      <el-form-item label="用户名" prop="uname">
        <el-input v-model="registerForm.uname" placeholder="请输入用户名"/>
      </el-form-item>
      
      <el-form-item label="密码" prop="upwd">
        <el-input 
          v-model="registerForm.upwd" 
          type="password" 
          placeholder="请输入密码"
          show-password/>
      </el-form-item>
      
      <el-form-item label="确认密码" prop="confirmUpwd">
        <el-input 
          v-model="registerForm.confirmUpwd" 
          type="password" 
          placeholder="请再次输入密码"
          show-password/>
      </el-form-item>
      
      <el-form-item label="邮箱" prop="uemail">
        <el-input v-model="registerForm.uemail" placeholder="请输入邮箱地址"/>
      </el-form-item>
    </el-form>
    
    <template #footer>
      <el-button @click="showRegisterDialog = false">取消</el-button>
      <el-button type="primary" @click="register">注册</el-button>
    </template>
  </el-dialog>
</div>
</template>

<script setup lang="ts">
import { useUserStore } from '../stores/my-user.ts';
let store = useUserStore()

import { ElMessage } from 'element-plus';
import { ref, reactive } from 'vue';
import { useRouter } from 'vue-router';
import type { FormInstance, FormRules } from 'element-plus';
import axios from 'axios';

const router = useRouter()
// 统一使用 uname，去掉 uid
const uname = ref("")
const upwd = ref("")
const showRegisterDialog = ref(false)

const registerForm = reactive({
  uname: '',
  upwd: '',
  confirmUpwd: '',
  uemail: ''  // 改为邮箱
});

const registerFormRef = ref<FormInstance>()

// 注册表单验证规则
const registerRules = reactive<FormRules>({
  uname: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度在 3 到 20 个字符', trigger: 'blur' }
  ],
  upwd: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度在 6 到 20 个字符', trigger: 'blur' }
  ],
  confirmUpwd: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    { 
      validator: (rule, value, callback) => {
        if (value !== registerForm.upwd) {
          callback(new Error('两次输入密码不一致!'))
        } else {
          callback()
        }
      },
      trigger: 'blur' 
    }
  ],
  uemail: [
    { required: true, message: '请输入邮箱地址', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
  ]
});
    
// 修改登录函数
function login(){
  const loginData = new URLSearchParams();
  loginData.append('username', uname.value);  
  loginData.append('password', upwd.value);
  
  axios.post("http://localhost:8080/auth/login", loginData, {
    headers: {
      'Content-Type': 'application/x-www-form-urlencoded'
    }
  })
  .then(res => {
    if(res.data === "Login successful"){
      // 登录成功，存储用户信息到公共变量
      store.setUserInfo(uname.value, upwd.value,""); 
      console.log("登录成功:", store.uname);
      ElMessage.success("登录成功");
      router.push("/first");
    }else{
      ElMessage.error("登录失败: " + res.data);
    }
  })
  .catch(error => {
    console.error("登录请求失败:", error);
    ElMessage.error("登录失败: " + (error.response?.data || error.message));
  });
}

// 修改注册函数
function register() {
  registerFormRef.value?.validate((valid) => {
    if (valid) {
      const registerData = new URLSearchParams();
      registerData.append('username', registerForm.uname);
      registerData.append('password', registerForm.upwd);
      registerData.append('email', registerForm.uemail); // 直接使用邮箱
      
      axios.post("http://localhost:8080/auth/register", registerData, {
        headers: {
          'Content-Type': 'application/x-www-form-urlencoded'
        }
      })
      .then(res => {
        if (res.data === "Registration successful") {
          ElMessage.success("注册成功！");
          showRegisterDialog.value = false;
          registerFormRef.value?.resetFields();
        } else {
          ElMessage.error("注册失败: " + res.data);
        }
      })
      .catch(error => {
        console.error("注册请求失败:", error);
        ElMessage.error("注册失败: " + (error.response?.data || error.message));
      });
    }
  });
}
</script>

<style scoped>
/* 原有样式保持不变 */
.login-container {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: url('bg.jpg') no-repeat center center fixed;
  background-size: cover;
  display: flex;
  justify-content: center;
  align-items: center;
}
.box {
  width: 350px;
  padding: 30px;
  border-radius: 15px;
  background: rgba(255, 255, 255, 0.15);
  backdrop-filter: blur(12px);
  border: 1px solid rgba(255, 255, 255, 0.3);
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
}

h2 {
  color: #fff;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.2);
  margin-bottom: 30px;
}

/* 输入框优化 */
.transparent-input :deep(.el-input__inner) {
  background: transparent !important;
  border: 1px solid rgba(255, 255, 255, 0.4) !important;
  color: #000000 !important;
  height: 40px;
  transition: all 0.3s;
}

/* 输入框聚焦效果 */
.transparent-input :deep(.el-input__inner:focus) {
  border-color: rgba(255, 255, 255, 0.8) !important;
  box-shadow: 0 0 8px rgba(255, 255, 255, 0.2) !important;
}

/* 按钮样式修改 */
.transparent-btn {
  width: 80% !important;
  display: block !important;
  margin: 0 auto !important;
  background: rgba(86, 140, 255, 0.7) !important;
  border: 1px solid rgba(255, 255, 255, 0.4) !important;
  height: 42px;
  font-size: 16px;
  letter-spacing: 2px;
  transition: all 0.3s !important;
  margin-top: 10px !important;
}

.transparent-btn:hover {
  background: rgba(86, 140, 255, 0.9) !important;
  transform: scale(1.02);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
}

/* 注册按钮特殊样式 */
.register-btn {
  background: rgba(103, 194, 58, 0.7) !important;
}

.register-btn:hover {
  background: rgba(103, 194, 58, 0.9) !important;
}
</style>