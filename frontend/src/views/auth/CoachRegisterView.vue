<template>
  <div class="register-container">
    <!-- 背景装饰元素 -->
    <div class="background-decorations">
      <!-- 教练哨子装饰 -->
      <div class="whistle-decoration">🔔</div>

      <!-- 乒乓球拍装饰 -->
      <div class="paddle-decoration paddle-1">🏓</div>
      <div class="paddle-decoration paddle-2">🏓</div>

      <!-- 奖杯装饰 -->
      <div class="trophy-decoration">🏆</div>

      <!-- 几何图形装饰 -->
      <div class="geometric-shape"></div>
      <div class="geometric-shape"></div>
      <div class="geometric-shape"></div>

      <!-- 艺术字装饰 -->
      <div class="art-text">
        <div class="art-text-main">COACH</div>
        <div class="art-text-sub">教练注册</div>
      </div>

      <!-- 专业图标装饰 -->
      <div class="professional-badge">⭐</div>
    </div>

    <div class="register-box">
      <div class="register-header">
        <h1 class="register-title">教练注册</h1>
        <p class="register-subtitle">提交入职申请，需校区管理员审核后方可使用</p>
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
                <el-option label="男" value="male" />
                <el-option label="女" value="female" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="年龄" prop="age">
              <el-input-number
                v-model="registerForm.age"
                :min="18"
                :max="65"
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

        <el-form-item label="邮箱（选填）" prop="email">
          <el-input v-model="registerForm.email" placeholder="请输入邮箱地址" clearable />
        </el-form-item>

        <el-form-item label="教练照片" prop="avatar">
          <el-upload
            ref="uploadRef"
            class="avatar-uploader"
            :show-file-list="false"
            :before-upload="beforeUpload"
            :on-success="handleUploadSuccess"
            :on-error="handleUploadError"
            action="/api/upload/avatar"
            :headers="{ Authorization: `Bearer ${userStore.token}` }"
          >
            <img v-if="registerForm.avatar" :src="registerForm.avatar" class="avatar" />
            <el-icon v-else class="avatar-uploader-icon">
              <Plus />
            </el-icon>
            <template #tip>
              <div class="upload-tip">点击上传照片，支持 jpg/png 格式，文件大小不超过 2MB</div>
            </template>
          </el-upload>
        </el-form-item>

        <el-form-item label="比赛成绩描述" prop="achievements">
          <el-input
            v-model="registerForm.achievements"
            type="textarea"
            :rows="4"
            placeholder="请详细描述您的乒乓球比赛经历和获得的成绩，这将有助于学员了解您的教学水平"
            maxlength="500"
            show-word-limit
          />
        </el-form-item>

        <el-form-item>
          <el-checkbox v-model="registerForm.agreement">
            我已阅读并同意
            <el-link type="primary">《教练员协议》</el-link>
            和
            <el-link type="primary">《隐私政策》</el-link>
          </el-checkbox>
        </el-form-item>

        <el-form-item>
          <el-button
            type="primary"
            class="register-button"
            :loading="loading"
            @click="handleRegister"
          >
            提交申请
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
import { Plus } from '@element-plus/icons-vue'
import api from '@/utils/api'

const router = useRouter()
const userStore = useUserStore()

// 表单引用
const registerFormRef = ref()
const uploadRef = ref()

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
  avatar: '',
  achievements: '',
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
  email: [{ type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }],
  achievements: [
    { required: true, message: '请描述您的比赛成绩', trigger: 'blur' },
    { min: 20, max: 500, message: '成绩描述在 20 到 500 个字符', trigger: 'blur' },
  ],
  agreement: [
    {
      validator: (rule, value, callback) => {
        if (!value) {
          callback(new Error('请阅读并同意教练员协议和隐私政策'))
        } else {
          callback()
        }
      },
      trigger: 'change',
    },
  ],
}

// 获取校区列表
const fetchCampusList = async () => {
  try {
    const response = await api.get('/campuses')
    campusList.value = response.data || []
  } catch (error) {
    console.error('获取校区列表失败:', error)
    ElMessage.error('获取校区列表失败')
  }
}

// 上传前检查
const beforeUpload = (file) => {
  const isImage = file.type === 'image/jpeg' || file.type === 'image/png'
  const isLt2M = file.size / 1024 / 1024 < 2

  if (!isImage) {
    ElMessage.error('上传头像图片只能是 JPG 或 PNG 格式!')
    return false
  }
  if (!isLt2M) {
    ElMessage.error('上传头像图片大小不能超过 2MB!')
    return false
  }
  return true
}

// 上传成功回调
const handleUploadSuccess = (response) => {
  registerForm.avatar = response.data.url
  ElMessage.success('照片上传成功')
}

// 上传失败回调
const handleUploadError = () => {
  ElMessage.error('照片上传失败，请重试')
}

// 处理注册
const handleRegister = async () => {
  if (!registerFormRef.value) return

  try {
    const valid = await registerFormRef.value.validate()
    if (!valid) return

    loading.value = true

    const registerData = {
      username: registerForm.username,
      realName: registerForm.realName,
      phone: registerForm.phone,
      password: registerForm.password,
      gender: registerForm.gender,
      age: registerForm.age,
      campusId: registerForm.campusId,
      email: registerForm.email,
      avatar: registerForm.avatar,
      achievements: registerForm.achievements,
      role: 'coach',
    }

    await userStore.register(registerData)

    ElMessage.success('申请提交成功！请等待校区管理员审核')
    router.push('/login')
  } catch (error) {
    console.error('注册错误:', error)
    ElMessage.error(error.message || '提交申请失败，请稍后重试')
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
    #ff6b6b 0%,
    #feca57 25%,
    #48dbfb 50%,
    #0abde3 75%,
    #006ba6 100%
  );
  position: relative;
  padding: 20px;
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

/* 哨子装饰 */
.whistle-decoration {
  position: absolute;
  top: 20%;
  left: 15%;
  font-size: 80px;
  opacity: 0.1;
  animation: swing 4s ease-in-out infinite;
  transform-origin: top center;
}

/* 乒乓球拍装饰 */
.paddle-decoration {
  position: absolute;
  font-size: 100px;
  opacity: 0.08;
  animation: float 6s ease-in-out infinite;
}

.paddle-1 {
  top: 10%;
  right: 20%;
  animation-delay: 0s;
}

.paddle-2 {
  bottom: 20%;
  left: 10%;
  animation-delay: 3s;
  transform: rotate(180deg);
}

/* 奖杯装饰 */
.trophy-decoration {
  position: absolute;
  bottom: 15%;
  right: 15%;
  font-size: 120px;
  opacity: 0.1;
  animation: glow 3s ease-in-out infinite;
}

/* 几何图形装饰 */
.geometric-shape {
  position: absolute;
  opacity: 0.1;
}

.geometric-shape:nth-child(5) {
  top: 30%;
  right: 10%;
  width: 60px;
  height: 60px;
  background: #fff;
  border-radius: 50%;
  animation: float 5s ease-in-out infinite;
}

.geometric-shape:nth-child(6) {
  top: 60%;
  left: 5%;
  width: 80px;
  height: 80px;
  background: #fff;
  transform: rotate(45deg);
  animation: rotate 15s linear infinite;
}

.geometric-shape:nth-child(7) {
  bottom: 40%;
  right: 30%;
  width: 0;
  height: 0;
  border-left: 30px solid transparent;
  border-right: 30px solid transparent;
  border-bottom: 50px solid #fff;
  animation: float 7s ease-in-out infinite reverse;
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

/* 专业徽章装饰 */
.professional-badge {
  position: absolute;
  top: 50%;
  left: 8%;
  font-size: 90px;
  opacity: 0.08;
  animation: pulse 3s ease-in-out infinite;
}

/* 注册框样式 */
.register-box {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(20px);
  border: 1px solid rgba(255, 255, 255, 0.2);
  padding: 40px;
  border-radius: 20px;
  box-shadow:
    0 25px 50px rgba(0, 0, 0, 0.15),
    0 0 0 1px rgba(255, 255, 255, 0.1) inset;
  width: 100%;
  max-width: 700px;
  position: relative;
  z-index: 10;
  animation: slideInUp 0.8s ease-out;
}

.register-header {
  text-align: center;
  margin-bottom: 30px;
  position: relative;
}

.register-title {
  font-size: 32px;
  font-weight: 700;
  background: linear-gradient(135deg, #ff6b6b, #feca57, #48dbfb);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  margin: 0 0 8px 0;
  animation: fadeInDown 0.8s ease-out 0.2s both;
}

.register-subtitle {
  font-size: 15px;
  color: #666;
  margin: 0;
  animation: fadeInDown 0.8s ease-out 0.4s both;
}

.register-form {
  margin-bottom: 20px;
  animation: fadeInUp 0.8s ease-out 0.6s both;
}

.password-tips {
  margin-top: -15px;
  margin-bottom: 20px;
  animation: fadeInUp 0.8s ease-out 0.8s both;
}

.avatar-uploader {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.avatar-uploader :deep(.el-upload) {
  border: 2px dashed #d9d9d9;
  border-radius: 50%;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: all 0.3s;
  width: 120px;
  height: 120px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #f8f9fa, #e9ecef);
}

.avatar-uploader :deep(.el-upload:hover) {
  border-color: #409eff;
  box-shadow: 0 8px 25px rgba(64, 158, 255, 0.3);
  transform: translateY(-2px);
}

.avatar {
  width: 120px;
  height: 120px;
  border-radius: 50%;
  object-fit: cover;
}

.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  transition: color 0.3s;
}

.avatar-uploader :deep(.el-upload:hover) .avatar-uploader-icon {
  color: #409eff;
}

.upload-tip {
  margin-top: 10px;
  font-size: 12px;
  color: #666;
  text-align: center;
}

.register-button {
  width: 100%;
  height: 45px;
  font-size: 16px;
  font-weight: 600;
  background: linear-gradient(135deg, #667eea, #764ba2);
  border: none;
  border-radius: 8px;
  transition: all 0.3s;
}

.register-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(102, 126, 234, 0.4);
}

/* 动画定义 */
@keyframes float {
  0%,
  100% {
    transform: translateY(0px) rotate(0deg);
  }

  50% {
    transform: translateY(-15px) rotate(3deg);
  }
}

@keyframes swing {
  0%,
  100% {
    transform: rotate(0deg);
  }

  25% {
    transform: rotate(10deg);
  }

  75% {
    transform: rotate(-10deg);
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

@keyframes glow {
  0%,
  100% {
    opacity: 0.1;
    transform: scale(1);
  }

  50% {
    opacity: 0.15;
    transform: scale(1.05);
  }
}

@keyframes pulse {
  0%,
  100% {
    opacity: 0.08;
    transform: scale(1);
  }

  50% {
    opacity: 0.15;
    transform: scale(1.1);
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

/* 响应式设计 */
@media (max-width: 768px) {
  .register-container {
    padding: 10px;
  }

  .register-box {
    padding: 20px;
  }

  .register-title {
    font-size: 24px;
  }

  .artistic-text {
    font-size: 40px !important;
  }

  .whistle-decoration,
  .paddle-decoration,
  .trophy-decoration {
    font-size: 60px;
  }
}
</style>
