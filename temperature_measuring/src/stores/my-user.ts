import { ref, computed } from 'vue'
import { defineStore } from 'pinia'

export const useUserStore = defineStore('myuser', () => {  // 修正拼写错误：mysuer -> myuser
  const uname = ref(localStorage.getItem('uname') || "")
  const upwd = ref(localStorage.getItem('upwd') || "")
  const uemail = ref(localStorage.getItem('uemail') || "")  // 改为 uemail
  
  // 去掉 uid，统一使用 uname
  const setUserInfo = (newUname: string, newUpwd: string, newUemail: string) => {
    uname.value = newUname;
    upwd.value = newUpwd;
    uemail.value = newUemail;
    
    // 同时更新 localStorage
    localStorage.setItem('uname', newUname);
    localStorage.setItem('upwd', newUpwd);
    localStorage.setItem('uemail', newUemail);
  }

  // 添加清除用户信息的方法
  const clearUserInfo = () => {
    uname.value = "";
    upwd.value = "";
    uemail.value = "";
    localStorage.removeItem('uname');
    localStorage.removeItem('upwd');
    localStorage.removeItem('uemail');
  }

  return { uname, upwd, uemail, setUserInfo, clearUserInfo }
})