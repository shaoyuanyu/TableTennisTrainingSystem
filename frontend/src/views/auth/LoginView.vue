<template>
  <div class="login-container">
    <!-- 背景装饰元素 -->
    <div class="bg-decorations">
      <div class="ping-pong-ball ball-1"></div>
      <div class="ping-pong-ball ball-2"></div>
      <div class="ping-pong-ball ball-3"></div>
      <div class="ping-pong-paddle paddle-1"></div>
      <div class="ping-pong-paddle paddle-2"></div>

      <!-- 新增乒乓球emoji装饰 -->
      <div class="emoji-decoration ping-pong-emoji-1">🏓</div>
      <div class="emoji-decoration ping-pong-emoji-2">🏓</div>
      <div class="emoji-decoration ping-pong-emoji-3">🏓</div>
      <div class="emoji-decoration ping-pong-emoji-4">🏓</div>

      <!-- 运动相关emoji -->
      <div class="emoji-decoration trophy-emoji-1">🏆</div>
      <div class="emoji-decoration trophy-emoji-2">🥇</div>
      <div class="emoji-decoration medal-emoji">🥈</div>
      <div class="emoji-decoration star-emoji-1">⭐</div>
      <div class="emoji-decoration star-emoji-2">✨</div>
      <div class="emoji-decoration fire-emoji">🔥</div>

      <!-- 球类相关emoji -->
      <div class="emoji-decoration ball-emoji-1">🏀</div>
      <div class="emoji-decoration ball-emoji-2">⚽</div>
      <div class="emoji-decoration ball-emoji-3">🎾</div>

      <div class="art-text">
        <div class="art-text-main">PING PONG</div>
        <div class="art-text-sub">乒乓球培训</div>
      </div>
      <div class="floating-shapes">
        <div class="shape shape-1"></div>
        <div class="shape shape-2"></div>
        <div class="shape shape-3"></div>
        <div class="shape shape-4"></div>
      </div>
    </div>

    <div class="login-box">
      <div class="login-header">
        <div class="logo-container">
          <div class="logo-icon">
            <div class="ping-pong-icon">🏓</div>
          </div>
          <h1 class="login-title">乒乓球培训管理系统</h1>
        </div>
        <p class="login-subtitle">专业的乒乓球培训管理平台</p>
      </div>

      <el-form
        ref="loginFormRef"
        :model="loginForm"
        :rules="loginRules"
        class="login-form"
        size="large"
      >
        <el-form-item prop="username">
          <el-input
            v-model="loginForm.username"
            placeholder="请输入用户名或手机号"
            :prefix-icon="User"
            clearable
          />
        </el-form-item>

        <el-form-item prop="password">
          <el-input
            v-model="loginForm.password"
            type="password"
            placeholder="请输入密码"
            :prefix-icon="Lock"
            show-password
            clearable
            @keyup.enter="handleLogin"
          />
        </el-form-item>

        <el-form-item>
          <el-checkbox v-model="loginForm.remember">记住密码</el-checkbox>
          <el-link type="primary" class="forgot-password">忘记密码？</el-link>
        </el-form-item>

        <el-form-item>
          <el-button type="primary" class="login-button" :loading="loading" @click="handleLogin">
            登录
          </el-button>
        </el-form-item>
      </el-form>

      <div class="login-footer">
        <p>还没有账户？</p>
        <div class="register-links">
          <el-link type="primary" @click="goToRegister('student')"> 学员注册 </el-link>
          <span class="divider">|</span>
          <el-link type="primary" @click="goToRegister('coach')"> 教练注册 </el-link>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
// 原权限相关导入 - 暂时注释掉，权限管理将在后端实现
// import { getDefaultHomePage } from '@/utils/permissions'
import { ElMessage } from 'element-plus'
import { User, Lock } from '@element-plus/icons-vue'

const router = useRouter()
const userStore = useUserStore()

// 表单引用
const loginFormRef = ref()

// 加载状态
const loading = ref(false)

// 登录表单数据
const loginForm = reactive({
  username: '',
  password: '',
  remember: false,
})

// 表单验证规则
const loginRules = {
  username: [
    { required: true, message: '请输入用户名或手机号', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度在 3 到 20 个字符', trigger: 'blur' },
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度在 6 到 20 个字符', trigger: 'blur' },
  ],
}

// 处理登录
const handleLogin = async () => {
  if (!loginFormRef.value) return

  try {
    const valid = await loginFormRef.value.validate()
    if (!valid) return

    loading.value = true

    const success = await userStore.login({
      username: loginForm.username,
      password: loginForm.password,
      remember: loginForm.remember,
    })

    if (success) {
      ElMessage.success('登录成功')
      // 原有的权限检查逻辑 - 暂时注释掉
      // const homePage = getDefaultHomePage(userStore.userRole)
      // router.push(homePage)

      // 临时简化逻辑 - 直接跳转到仪表盘
      router.push('/dashboard')
    } else {
      ElMessage.error('登录失败，请检查用户名和密码')
    }
  } catch (error) {
    console.error('登录错误:', error)
    ElMessage.error('登录失败，请稍后重试')
  } finally {
    loading.value = false
  }
}

// 前往注册页面
const goToRegister = (type) => {
  router.push(`/register/${type}`)
}
</script>

<style scoped>
.login-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 50%, #f093fb 100%);
  padding: 20px;
  position: relative;
  overflow: hidden;
}

/* 背景装饰元素 */
.bg-decorations {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  pointer-events: none;
  z-index: 0;
}

.ping-pong-ball {
  position: absolute;
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: white;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
  animation: float 6s ease-in-out infinite;
}

.ball-1 {
  top: 10%;
  left: 15%;
  animation-delay: 0s;
}

.ball-2 {
  top: 70%;
  right: 20%;
  animation-delay: 2s;
}

.ball-3 {
  bottom: 20%;
  left: 10%;
  animation-delay: 4s;
}

.ping-pong-paddle {
  position: absolute;
  width: 60px;
  height: 80px;
  background: #8b4513;
  border-radius: 30px 30px 10px 10px;
  transform: rotate(45deg);
  animation: rotate 8s linear infinite;
}

.paddle-1 {
  top: 20%;
  right: 10%;
  animation-delay: 1s;
}

.paddle-2 {
  bottom: 15%;
  right: 15%;
  animation-delay: 3s;
  transform: rotate(-45deg);
}

.art-text {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  text-align: center;
  opacity: 0.1;
  z-index: -1;
}

.art-text-main {
  font-size: 120px;
  font-weight: 900;
  color: white;
  letter-spacing: 10px;
  text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.3);
  font-family: 'Arial Black', sans-serif;
}

.art-text-sub {
  font-size: 48px;
  font-weight: 600;
  color: white;
  letter-spacing: 8px;
  margin-top: -20px;
}

.floating-shapes {
  position: absolute;
  width: 100%;
  height: 100%;
}

/* Emoji装饰样式 */
.emoji-decoration {
  position: absolute;
  font-size: 40px;
  opacity: 0.3;
  pointer-events: none;
  user-select: none;
  animation: float 6s ease-in-out infinite;
}

/* 乒乓球emoji */
.ping-pong-emoji-1 {
  top: 15%;
  left: 10%;
  animation-delay: 0s;
  animation-duration: 8s;
}

.ping-pong-emoji-2 {
  top: 75%;
  right: 15%;
  animation-delay: 2s;
  animation-duration: 7s;
}

.ping-pong-emoji-3 {
  bottom: 30%;
  left: 8%;
  animation-delay: 4s;
  animation-duration: 9s;
}

.ping-pong-emoji-4 {
  top: 45%;
  right: 8%;
  animation-delay: 6s;
  animation-duration: 6s;
}

/* 奖杯和奖牌emoji */
.trophy-emoji-1 {
  top: 20%;
  right: 20%;
  font-size: 35px;
  animation: pulse 4s ease-in-out infinite;
  animation-delay: 1s;
}

.trophy-emoji-2 {
  bottom: 20%;
  right: 25%;
  font-size: 30px;
  animation: bounce 6s ease-in-out infinite;
  animation-delay: 3s;
}

.medal-emoji {
  top: 65%;
  left: 15%;
  font-size: 28px;
  animation: wiggle 3s ease-in-out infinite;
  animation-delay: 5s;
}

/* 星星和火焰emoji */
.star-emoji-1 {
  top: 35%;
  left: 5%;
  font-size: 25px;
  animation: pulse 5s ease-in-out infinite;
  animation-delay: 1.5s;
}

.star-emoji-2 {
  bottom: 15%;
  left: 20%;
  font-size: 20px;
  animation: bounce 4s ease-in-out infinite;
  animation-delay: 4.5s;
}

.fire-emoji {
  top: 80%;
  right: 10%;
  font-size: 30px;
  animation: wiggle 2s ease-in-out infinite;
  animation-delay: 2.5s;
}

/* 其他球类emoji */
.ball-emoji-1 {
  top: 25%;
  left: 25%;
  font-size: 22px;
  opacity: 0.2;
  animation-delay: 3.5s;
  animation-duration: 11s;
}

.ball-emoji-2 {
  bottom: 40%;
  right: 30%;
  font-size: 20px;
  opacity: 0.2;
  animation-delay: 5.5s;
  animation-duration: 7s;
}

.ball-emoji-3 {
  top: 55%;
  left: 30%;
  font-size: 18px;
  opacity: 0.2;
  animation-delay: 7s;
  animation-duration: 9s;
}

.shape {
  position: absolute;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.1);
  animation: float 8s ease-in-out infinite;
}

.shape-1 {
  width: 80px;
  height: 80px;
  top: 30%;
  left: 5%;
  animation-delay: 0.5s;
}

.shape-2 {
  width: 120px;
  height: 120px;
  top: 60%;
  right: 5%;
  animation-delay: 2.5s;
}

.shape-3 {
  width: 60px;
  height: 60px;
  top: 15%;
  right: 30%;
  animation-delay: 4.5s;
}

.shape-4 {
  width: 100px;
  height: 100px;
  bottom: 10%;
  left: 30%;
  animation-delay: 6.5s;
}

@keyframes float {
  0%,
  100% {
    transform: translateY(0px) rotate(0deg);
  }

  50% {
    transform: translateY(-20px) rotate(180deg);
  }
}

@keyframes rotate {
  from {
    transform: rotate(0deg);
  }

  to {
    transform: rotate(360deg);
  }
}

/* 新增emoji专用动画 */
@keyframes bounce {
  0%,
  20%,
  50%,
  80%,
  100% {
    transform: translateY(0px) scale(1);
  }

  40% {
    transform: translateY(-15px) scale(1.1);
  }

  60% {
    transform: translateY(-10px) scale(1.05);
  }
}

@keyframes pulse {
  0%,
  100% {
    transform: scale(1);
    opacity: 0.3;
  }

  50% {
    transform: scale(1.2);
    opacity: 0.5;
  }
}

@keyframes wiggle {
  0%,
  100% {
    transform: rotate(0deg);
  }

  25% {
    transform: rotate(5deg);
  }

  75% {
    transform: rotate(-5deg);
  }
}

.login-box {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(20px);
  padding: 50px 40px;
  border-radius: 20px;
  box-shadow: 0 30px 60px rgba(0, 0, 0, 0.15);
  width: 100%;
  max-width: 450px;
  position: relative;
  z-index: 1;
  border: 1px solid rgba(255, 255, 255, 0.2);
}

.login-header {
  text-align: center;
  margin-bottom: 40px;
}

.logo-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-bottom: 16px;
}

.logo-icon {
  width: 80px;
  height: 80px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 20px;
  box-shadow: 0 10px 30px rgba(102, 126, 234, 0.3);
  position: relative;
  overflow: hidden;
}

.logo-icon::before {
  content: '';
  position: absolute;
  top: -50%;
  left: -50%;
  width: 200%;
  height: 200%;
  background: linear-gradient(45deg, transparent, rgba(255, 255, 255, 0.3), transparent);
  animation: shimmer 3s infinite;
}

.ping-pong-icon {
  font-size: 36px;
  z-index: 1;
}

@keyframes shimmer {
  0% {
    transform: translateX(-100%) translateY(-100%) rotate(45deg);
  }

  100% {
    transform: translateX(100%) translateY(100%) rotate(45deg);
  }
}

.login-title {
  font-size: 28px;
  font-weight: 700;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  margin: 0;
  letter-spacing: 1px;
}

.login-subtitle {
  font-size: 16px;
  color: #666;
  margin: 0;
  font-weight: 400;
}

.login-form {
  margin-bottom: 30px;
}

.login-form :deep(.el-form-item) {
  margin-bottom: 24px;
}

.login-form :deep(.el-form-item:last-child) {
  margin-bottom: 0;
}

.login-form :deep(.el-form-item__content) {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.login-form :deep(.el-input__wrapper) {
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  border: 1px solid #e1e5e9;
  transition: all 0.3s ease;
}

.login-form :deep(.el-input__wrapper:hover) {
  border-color: #667eea;
}

.login-form :deep(.el-input__wrapper.is-focus) {
  border-color: #667eea;
  box-shadow: 0 0 0 2px rgba(102, 126, 234, 0.2);
}

.forgot-password {
  margin-left: auto;
  font-size: 13px;
  font-weight: 500;
}

.login-button {
  width: 100%;
  height: 50px;
  font-size: 16px;
  font-weight: 600;
  border-radius: 12px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  position: relative;
  overflow: hidden;
  transition: all 0.3s ease;
}

.login-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 10px 25px rgba(102, 126, 234, 0.3);
}

.login-button:active {
  transform: translateY(0);
}

.login-footer {
  text-align: center;
  padding-top: 30px;
  border-top: 1px solid #e8e8e8;
}

.login-footer p {
  margin: 0 0 15px 0;
  font-size: 14px;
  color: #666;
  font-weight: 500;
}

.register-links {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 15px;
}

.register-links :deep(.el-link) {
  font-weight: 600;
  font-size: 14px;
}

.divider {
  color: #ccc;
  font-size: 14px;
}

@media (max-width: 480px) {
  .login-container {
    padding: 15px;
  }

  .login-box {
    padding: 40px 30px;
  }

  .login-title {
    font-size: 24px;
  }

  .art-text-main {
    font-size: 80px;
    letter-spacing: 5px;
  }

  .art-text-sub {
    font-size: 32px;
    letter-spacing: 4px;
  }

  .ping-pong-ball {
    width: 30px;
    height: 30px;
  }

  .ping-pong-paddle {
    width: 45px;
    height: 60px;
  }
}

@media (max-width: 320px) {
  .login-box {
    padding: 30px 20px;
  }

  .login-title {
    font-size: 20px;
  }
}
</style>
