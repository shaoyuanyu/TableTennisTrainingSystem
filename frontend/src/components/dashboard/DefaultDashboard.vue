<template>
  <div class="default-dashboard">
    <!-- 背景装饰元素 -->
    <div class="background-decorations">
      <div class="floating-element ping-pong-1">🏓</div>
      <div class="floating-element ping-pong-2">🏓</div>
      <div class="floating-element ping-pong-3">🏓</div>
      <div class="floating-element trophy-1">🏆</div>
      <div class="floating-element star-1">⭐</div>
      <div class="floating-element star-2">⭐</div>
      <div class="geometric-shape shape-1"></div>
      <div class="geometric-shape shape-2"></div>
      <div class="geometric-shape shape-3"></div>
      <div class="artistic-text main-title">PING PONG</div>
      <div class="artistic-text sub-title">培训管理系统</div>
    </div>

    <!-- 欢迎页面 -->
    <div class="welcome-container">
      <div class="welcome-content">
        <h1 class="welcome-title">欢迎来到乒乓球培训管理系统</h1>
        <p class="welcome-subtitle">专业的乒乓球培训，个性化的学习体验</p>

        <div class="feature-cards">
          <div class="feature-card">
            <div class="feature-icon">🎯</div>
            <h3>专业培训</h3>
            <p>经验丰富的教练团队，提供专业的乒乓球培训服务</p>
          </div>
          <div class="feature-card">
            <div class="feature-icon">📅</div>
            <h3>灵活预约</h3>
            <p>便捷的在线预约系统，随时安排您的训练时间</p>
          </div>
          <div class="feature-card">
            <div class="feature-icon">📊</div>
            <h3>数据分析</h3>
            <p>详细的训练数据分析，帮助您更好地提升技能</p>
          </div>
          <div class="feature-card">
            <div class="feature-icon">🏆</div>
            <h3>竞赛机会</h3>
            <p>定期举办各类比赛，展示您的训练成果</p>
          </div>
        </div>

        <div class="action-buttons">
          <el-button type="primary" size="large" @click="goToLogin"> 立即登录 </el-button>
          <el-dropdown @command="handleRegisterCommand">
            <el-button size="large">
              注册账号
              <el-icon class="el-icon--right">
                <ArrowDown />
              </el-icon>
            </el-button>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="student">
                  <el-icon>
                    <User />
                  </el-icon>
                  学员注册
                </el-dropdown-item>
                <el-dropdown-item command="coach">
                  <el-icon>
                    <UserFilled />
                  </el-icon>
                  教练注册
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { ArrowDown, User, UserFilled } from '@element-plus/icons-vue'

const router = useRouter()

const goToLogin = () => {
  router.push('/login')
}

const handleRegisterCommand = (command) => {
  if (command === 'student') {
    router.push('/register/student')
  } else if (command === 'coach') {
    router.push('/register/coach')
  }
}
</script>

<style scoped>
/* 背景装饰元素 */
.background-decorations {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  pointer-events: none;
  z-index: 0;
  overflow: hidden;
}

.floating-element {
  position: absolute;
  font-size: 2rem;
  opacity: 0.1;
  animation: float 6s ease-in-out infinite;
}

.ping-pong-1 {
  top: 10%;
  left: 10%;
  animation-delay: 0s;
}

.ping-pong-2 {
  top: 30%;
  right: 15%;
  animation-delay: 2s;
}

.ping-pong-3 {
  bottom: 20%;
  left: 20%;
  animation-delay: 4s;
}

.trophy-1 {
  top: 50%;
  right: 10%;
  animation-delay: 1s;
}

.star-1 {
  top: 20%;
  left: 50%;
  animation-delay: 3s;
}

.star-2 {
  bottom: 30%;
  right: 30%;
  animation-delay: 5s;
}

.geometric-shape {
  position: absolute;
  border-radius: 50%;
  opacity: 0.05;
  background: linear-gradient(45deg, #667eea, #764ba2);
  animation: rotate 20s linear infinite;
}

.shape-1 {
  width: 200px;
  height: 200px;
  top: 15%;
  right: 20%;
}

.shape-2 {
  width: 150px;
  height: 150px;
  bottom: 25%;
  left: 15%;
  animation-delay: 5s;
}

.shape-3 {
  width: 100px;
  height: 100px;
  top: 60%;
  left: 45%;
  animation-delay: 10s;
}

.artistic-text {
  position: absolute;
  font-weight: bold;
  opacity: 0.03;
  pointer-events: none;
  color: #333;
}

.main-title {
  font-size: 8rem;
  top: 20%;
  left: 50%;
  transform: translateX(-50%);
}

.sub-title {
  font-size: 3rem;
  top: 35%;
  left: 50%;
  transform: translateX(-50%);
}

/* 动画 */
@keyframes float {
  0%,
  100% {
    transform: translateY(0px);
  }

  50% {
    transform: translateY(-20px);
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

/* 主要内容 */
.default-dashboard {
  position: relative;
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1;
}

.welcome-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 40px 20px;
  text-align: center;
  z-index: 1;
  position: relative;
}

.welcome-content {
  background: rgba(255, 255, 255, 0.9);
  border-radius: 20px;
  padding: 60px 40px;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.1);
  backdrop-filter: blur(10px);
}

.welcome-title {
  font-size: 48px;
  font-weight: 700;
  margin: 0 0 16px 0;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.welcome-subtitle {
  font-size: 20px;
  color: #666;
  margin: 0 0 60px 0;
}

.feature-cards {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 30px;
  margin-bottom: 60px;
}

.feature-card {
  background: white;
  border-radius: 16px;
  padding: 30px 20px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  transition: all 0.3s ease;
}

.feature-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.12);
}

.feature-icon {
  font-size: 48px;
  margin-bottom: 20px;
}

.feature-card h3 {
  font-size: 20px;
  font-weight: 600;
  margin: 0 0 12px 0;
  color: #333;
}

.feature-card p {
  font-size: 14px;
  color: #666;
  line-height: 1.6;
  margin: 0;
}

.action-buttons {
  display: flex;
  gap: 20px;
  justify-content: center;
}

/* 响应式 */
@media (max-width: 768px) {
  .welcome-title {
    font-size: 32px;
  }

  .welcome-subtitle {
    font-size: 16px;
  }

  .welcome-content {
    padding: 40px 20px;
  }

  .feature-cards {
    grid-template-columns: 1fr;
    gap: 20px;
  }

  .action-buttons {
    flex-direction: column;
    align-items: center;
  }

  .main-title {
    font-size: 4rem;
  }

  .sub-title {
    font-size: 1.5rem;
  }
}
</style>
