<template>
  <div class="register-container">
    <!-- 背景装饰元素 -->
    <div class="bg-decorations">
      <div class="table-tennis-table"></div>
      <div class="ping-pong-ball ball-1"></div>
      <div class="ping-pong-ball ball-2"></div>
      <div class="ping-pong-ball ball-3"></div>
      <div class="racket racket-1"></div>
      <div class="racket racket-2"></div>
      <div class="art-text">
        <div class="art-text-main">STUDENT</div>
        <div class="art-text-sub">学员注册</div>
      </div>
      <div class="geometric-shapes">
        <div class="triangle triangle-1"></div>
        <div class="circle circle-1"></div>
        <div class="square square-1"></div>
      </div>
    </div>

    <div class="register-box">
      <div class="register-header">
        <div class="header-icon">
          <div class="student-icon">👨‍🎓</div>
        </div>
        <h1 class="register-title">学员注册</h1>
        <p class="register-subtitle">加入我们，开启乒乓球学习之旅</p>
      </div>

      <el-form
        ref="registerFormRef"
        :model="registerForm"
        :rules="registerRules"
        class="register-form"
        label-position="top"
        size="large"
      >
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="用户名" prop="username">
              <el-input v-model="registerForm.username" placeholder="请输入用户名" clearable />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="真实姓名" prop="realName">
              <el-input v-model="registerForm.realName" placeholder="请输入真实姓名" clearable />
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="手机号码" prop="phone">
          <el-input v-model="registerForm.phone" placeholder="请输入手机号码" clearable />
        </el-form-item>

        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="密码" prop="password">
              <el-input
                v-model="registerForm.password"
                type="password"
                placeholder="请输入密码"
                show-password
                clearable
                @input="handlePasswordChange"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="确认密码" prop="confirmPassword">
              <el-input
                v-model="registerForm.confirmPassword"
                type="password"
                placeholder="请确认密码"
                show-password
                clearable
              />
            </el-form-item>
          </el-col>
        </el-row>

        <div class="password-tips">
          <el-text size="small" type="info"> 密码要求：8-16位，包含字母、数字和特殊字符 </el-text>
        </div>

        <el-row :gutter="16">
          <el-col :span="8">
            <el-form-item label="性别" prop="gender">
              <el-select v-model="registerForm.gender" placeholder="请选择">
                <el-option label="男" value="男" />
                <el-option label="女" value="女" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="年龄" prop="age">
              <el-input-number
                v-model="registerForm.age"
                :min="6"
                :max="80"
                placeholder="年龄"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="校区" prop="campusId">
              <el-select v-model="registerForm.campusId" placeholder="请选择校区">
                <el-option
                  v-for="campus in campusList"
                  :key="campus.id"
                  :label="campus.name"
                  :value="campus.id"
                />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="邮箱" prop="email">
          <el-input v-model="registerForm.email" placeholder="请输入邮箱地址" clearable />
        </el-form-item>

        <el-form-item prop="agreement">
          <el-checkbox v-model="registerForm.agreement">
            我已阅读并同意
            <el-link type="primary">《用户协议》</el-link>
            和
            <el-link type="primary">《隐私政策》</el-link>
          </el-checkbox>
        </el-form-item>

        <el-form-item>
          <el-button
            type="primary"
            class="register-button btn-modern btn-primary btn-large"
            :loading="loading"
            @click="handleRegister"
          >
            注册
          </el-button>
        </el-form-item>
      </el-form>

      <div class="register-footer">
        <p>已有账户？</p>
        <el-link type="primary" @click="goToLogin">立即登录</el-link>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { ElMessage } from 'element-plus'
import { getCampusList } from '@/api/auth'

const router = useRouter()
const userStore = useUserStore()

// 表单引用
const registerFormRef = ref()

// 加载状态
const loading = ref(false)

// 校区列表
const campusList = ref([])

// 注册表单数据
const registerForm = reactive({
  username: '',
  realName: '',
  phone: '',
  password: '',
  confirmPassword: '',
  gender: '',
  age: null,
  campusId: '',
  email: '',
  agreement: false,
})

// 密码验证函数
const validatePassword = (rule, value, callback) => {
  if (!value) {
    callback(new Error('请输入密码'))
  } else if (value.length < 8 || value.length > 16) {
    callback(new Error('密码长度在 8 到 16 个字符'))
  } else if (!/^(?=.*[a-zA-Z])(?=.*\d)(?=.*[\W_]).{8,16}$/.test(value)) {
    callback(new Error('密码必须包含字母、数字和特殊字符'))
  } else {
    callback()
  }
}

// 确认密码验证函数
const validateConfirmPassword = (rule, value, callback) => {
  if (!value) {
    callback(new Error('请确认密码'))
  } else if (value !== registerForm.password) {
    callback(new Error('两次输入密码不一致'))
  } else {
    callback()
  }
}

// 密码改变时重新验证确认密码
const handlePasswordChange = () => {
  if (registerForm.confirmPassword && registerFormRef.value) {
    registerFormRef.value.validateField('confirmPassword')
  }
}

// 手机号验证函数
const validatePhone = (rule, value, callback) => {
  if (!value) {
    callback(new Error('请输入手机号码'))
  } else if (!/^1[3-9]\d{9}$/.test(value)) {
    callback(new Error('请输入正确的手机号码'))
  } else {
    callback()
  }
}

// 表单验证规则
const registerRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度在 3 到 20 个字符', trigger: 'blur' },
    { pattern: /^[a-zA-Z0-9_]+$/, message: '用户名只能包含字母、数字和下划线', trigger: 'blur' },
  ],
  realName: [
    { required: true, message: '请输入真实姓名', trigger: 'blur' },
    { min: 2, max: 10, message: '姓名长度在 2 到 10 个字符', trigger: 'blur' },
  ],
  phone: [{ required: true, validator: validatePhone, trigger: 'blur' }],
  password: [{ required: true, validator: validatePassword, trigger: 'blur' }],
  confirmPassword: [{ required: true, validator: validateConfirmPassword, trigger: 'blur' }],
  campusId: [{ required: true, message: '请选择校区', trigger: 'change' }],
  email: [
    { required: true, message: '请输入邮箱地址', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' },
  ],
  agreement: [
    {
      validator: (rule, value, callback) => {
        if (!value) {
          callback(new Error('请阅读并同意用户协议和隐私政策'))
        } else {
          callback()
        }
      },
      trigger: ['change', 'blur'],
    },
  ],
}

// 获取校区列表
const fetchCampusList = async () => {
  try {
    const data = await getCampusList()
    campusList.value = data || []
  } catch (error) {
    console.error('获取校区列表失败:', error)
    ElMessage.error('获取校区列表失败')
  }
}

// 处理注册
const handleRegister = async () => {
  if (!registerFormRef.value) return

  try {
    const valid = await registerFormRef.value.validate()
    if (!valid) return

    // 额外检查用户协议是否勾选
    if (!registerForm.agreement) {
      ElMessage.warning('请先阅读并同意用户协议和隐私政策')
      return
    }

    loading.value = true

    // 根据后端API规范构造注册数据
    const registerData = {
      username: registerForm.username.trim(),
      plainPassword: registerForm.password,
      realName: registerForm.realName.trim(),
      gender: registerForm.gender,
      age: parseInt(registerForm.age),
      phoneNumber: registerForm.phone.trim(),
      email: registerForm.email.trim(),
      campusId: parseInt(registerForm.campusId),
      role: 'STUDENT',
      status: 'ACTIVE',
      studentInfo: {
        balance: 100.0,
        maxCoach: 3,
        currentCoach: 0,
      },
    }

    console.log('发送到后端的注册数据:', JSON.stringify(registerData, null, 2))

    await userStore.register(registerData)

    ElMessage.success('注册成功！注册即生效，请登录')
    router.push('/login')
  } catch (error) {
    console.error('注册错误:', error)
    ElMessage.error(error.message || '注册失败，请稍后重试')
  } finally {
    loading.value = false
  }
}

// 前往登录页面
const goToLogin = () => {
  router.push('/login')
}

// 组件挂载时获取校区列表
onMounted(() => {
  fetchCampusList()
})
</script>

<style scoped>
.register-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(
    135deg,
    #a8e6cf 0%,
    #88d8c0 25%,
    #7fcdcd 50%,
    #7d6caa 75%,
    #8b5a96 100%
  );
  position: relative;
  padding: var(--spacing-xl);
  overflow: hidden;
}

/* 背景装饰元素 */
.background-decorations {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  pointer-events: none;
  overflow: hidden;
}

/* 桌球台装饰 */
.table-tennis-table {
  position: absolute;
  top: 60%;
  right: -10%;
  width: 400px;
  height: 200px;
  background: #4a6741;
  border: 3px solid #fff;
  border-radius: 10px;
  opacity: 0.1;
  transform: perspective(300px) rotateX(45deg) rotateY(-15deg);
  animation: float 8s ease-in-out infinite;
}

.table-tennis-table::before {
  content: '';
  position: absolute;
  top: 50%;
  left: 0;
  right: 0;
  height: 2px;
  background: #fff;
  transform: translateY(-50%);
}

.table-tennis-table::after {
  content: '';
  position: absolute;
  top: 0;
  bottom: 0;
  left: 50%;
  width: 2px;
  background: #fff;
  transform: translateX(-50%);
}

/* 学员图标装饰 */
.student-icon {
  position: absolute;
  top: 15%;
  left: 10%;
  font-size: 120px;
  opacity: 0.08;
  animation: rotate 20s linear infinite;
  color: #fff;
}

/* 几何图形装饰 */
.geometric-shape {
  position: absolute;
  opacity: 0.1;
}

.geometric-shape:nth-child(1) {
  top: 20%;
  right: 15%;
  width: 80px;
  height: 80px;
  background: #fff;
  border-radius: 50%;
  animation: float 6s ease-in-out infinite;
}

.geometric-shape:nth-child(2) {
  bottom: 25%;
  left: 8%;
  width: 60px;
  height: 60px;
  background: #fff;
  transform: rotate(45deg);
  animation: float 7s ease-in-out infinite reverse;
}

.geometric-shape:nth-child(3) {
  top: 70%;
  left: 20%;
  width: 0;
  height: 0;
  border-left: 40px solid transparent;
  border-right: 40px solid transparent;
  border-bottom: 70px solid #fff;
  animation: float 5s ease-in-out infinite;
}

/* 艺术字装饰 */
.art-text {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  text-align: center;
  opacity: 0.15;
  z-index: 1;
  pointer-events: none;
  user-select: none;
}

.art-text-main {
  font-size: 120px;
  font-weight: 900;
  color: rgba(255, 255, 255, 0.8);
  letter-spacing: 10px;
  text-shadow: 3px 3px 6px rgba(0, 0, 0, 0.4);
  font-family: 'Arial Black', sans-serif;
  -webkit-text-stroke: 1px rgba(255, 255, 255, 0.3);
}

.art-text-sub {
  font-size: 48px;
  font-weight: 600;
  color: rgba(255, 255, 255, 0.7);
  letter-spacing: 8px;
  margin-top: -20px;
  text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.3);
  -webkit-text-stroke: 0.5px rgba(255, 255, 255, 0.2);
}

.artistic-text {
  position: absolute;
  font-family: 'Arial Black', Arial, sans-serif;
  font-weight: 900;
  color: rgba(255, 255, 255, 0.05);
  user-select: none;
  z-index: 1;
}

.artistic-text.student {
  top: 25%;
  right: 5%;
  font-size: 60px;
  transform: rotate(-15deg);
  animation: shimmer 4s ease-in-out infinite;
}

.artistic-text.register {
  bottom: 15%;
  left: 5%;
  font-size: 48px;
  transform: rotate(10deg);
  animation: shimmer 4s ease-in-out infinite 2s;
}

/* 书本图标装饰 */
.book-decoration {
  position: absolute;
  bottom: 30%;
  right: 25%;
  width: 60px;
  height: 45px;
  background: rgba(255, 255, 255, 0.08);
  border-radius: 3px;
  animation: float 6s ease-in-out infinite 1s;
}

.book-decoration::before {
  content: '';
  position: absolute;
  top: 5px;
  left: 5px;
  right: 5px;
  bottom: 5px;
  border: 2px solid rgba(255, 255, 255, 0.1);
  border-radius: 2px;
}

/* 注册框样式 */
.register-box {
  background: var(--white-alpha-95);
  backdrop-filter: var(--blur-xl);
  border: 1px solid var(--white-alpha-20);
  padding: var(--spacing-3xl);
  border-radius: var(--radius-2xl);
  box-shadow: var(--shadow-xl);
  width: 100%;
  max-width: 600px;
  position: relative;
  z-index: 10;
  animation: slideInUp 0.8s ease-out;
}

.register-header {
  text-align: center;
  margin-bottom: var(--spacing-3xl);
  position: relative;
}

.register-title {
  font-size: var(--font-size-4xl);
  font-weight: var(--font-weight-bold);
  background: linear-gradient(135deg, #667eea, #764ba2);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  margin: 0 0 var(--spacing-sm) 0;
  animation: fadeInDown 0.8s ease-out 0.2s both;
}

.register-subtitle {
  font-size: var(--font-size-lg);
  color: #666;
  margin: 0;
  animation: fadeInDown 0.8s ease-out 0.4s both;
}

.register-form {
  margin-bottom: var(--spacing-xl);
  animation: fadeInUp 0.8s ease-out 0.6s both;
}

.password-tips {
  margin-top: calc(-1 * var(--spacing-lg));
  margin-bottom: var(--spacing-xl);
  animation: fadeInUp 0.8s ease-out 0.8s both;
}

/* 动画定义 */
@keyframes float {
  0%,
  100% {
    transform: translateY(0px) rotate(0deg);
  }

  50% {
    transform: translateY(-20px) rotate(5deg);
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

@keyframes shimmer {
  0%,
  100% {
    opacity: 0.05;
  }

  50% {
    opacity: 0.12;
  }
}

@keyframes slideInUp {
  from {
    transform: translateY(50px);
    opacity: 0;
  }

  to {
    transform: translateY(0);
    opacity: 1;
  }
}

@keyframes fadeInDown {
  from {
    transform: translateY(-20px);
    opacity: 0;
  }

  to {
    transform: translateY(0);
    opacity: 1;
  }
}

@keyframes fadeInUp {
  from {
    transform: translateY(20px);
    opacity: 0;
  }

  to {
    transform: translateY(0);
    opacity: 1;
  }
}

.register-button {
  width: 100%;
  height: 44px;
  font-size: var(--font-size-lg);
  font-weight: var(--font-weight-medium);
}

.register-footer {
  text-align: center;
  padding-top: var(--spacing-xl);
  border-top: 1px solid #e8e8e8;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: var(--spacing-sm);
}

.register-footer p {
  margin: 0;
  font-size: var(--font-size-md);
  color: #666;
}

@media (max-width: 768px) {
  .register-box {
    padding: var(--spacing-3xl) var(--spacing-xl);
  }

  .register-title {
    font-size: var(--font-size-3xl);
  }
}
</style>
