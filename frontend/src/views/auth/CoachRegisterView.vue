<template>
  <div class="coach-register-container">
    <!-- 装饰背景 -->
    <div class="decorative-background">
      <div class="floating-element element-1">🏓</div>
      <div class="floating-element element-2">🏆</div>
      <div class="floating-element element-3">⭐</div>
      <div class="floating-element element-4">👨‍🏫</div>
      <div class="gradient-circle circle-1"></div>
      <div class="gradient-circle circle-2"></div>
    </div>

    <div class="register-content">
      <!-- 顶部标题区 -->
      <div class="register-header">
        <h1 class="main-title">教练员注册</h1>
        <p class="sub-title">Coach Registration</p>
        <div class="title-divider"></div>
        <p class="description">提交入职申请，开启您的教练生涯</p>
      </div>

      <!-- 注册表单 -->
      <el-form
        ref="registerFormRef"
        :model="registerForm"
        :rules="registerRules"
        class="coach-register-form"
        label-position="top"
        size="large"
      >
        <!-- 基本信息部分 -->
        <div class="form-section">
          <div class="section-header">
            <div class="section-icon">👤</div>
            <h3>基本信息</h3>
          </div>
          
          <el-row :gutter="20">
            <el-col :md="12" :sm="24">
              <el-form-item label="用户名" prop="username">
                <el-input
                  v-model="registerForm.username"
                  placeholder="请输入3-20位用户名"
                  clearable
                  prefix-icon="User"
                />
              </el-form-item>
            </el-col>
            <el-col :md="12" :sm="24">
              <el-form-item label="真实姓名" prop="realName">
                <el-input
                  v-model="registerForm.realName"
                  placeholder="请输入真实姓名"
                  clearable
                  prefix-icon="Edit"
                />
              </el-form-item>
            </el-col>
          </el-row>

          <el-form-item label="手机号码" prop="phone">
            <el-input
              v-model="registerForm.phone"
              placeholder="请输入11位手机号码"
              clearable
              prefix-icon="Iphone"
            />
          </el-form-item>

          <el-row :gutter="20">
            <el-col :md="12" :sm="24">
              <el-form-item label="密码" prop="password">
                <el-input
                  v-model="registerForm.password"
                  type="password"
                  placeholder="8-16位含字母、数字、特殊字符"
                  show-password
                  clearable
                />
              </el-form-item>
            </el-col>
            <el-col :md="12" :sm="24">
              <el-form-item label="确认密码" prop="confirmPassword">
                <el-input
                  v-model="registerForm.confirmPassword"
                  type="password"
                  placeholder="请再次输入密码"
                  show-password
                  clearable
                />
              </el-form-item>
            </el-col>
          </el-row>

          <div class="password-requirements">
            <el-text type="info" size="small">
              🔒 密码要求：8-16位，必须包含字母、数字和特殊字符
            </el-text>
          </div>
        </div>

        <!-- 个人信息部分 -->
        <div class="form-section">
          <div class="section-header">
            <div class="section-icon">📋</div>
            <h3>个人信息</h3>
          </div>

          <el-row :gutter="20">
            <el-col :md="8" :sm="24">
              <el-form-item label="性别" prop="gender">
                <el-select v-model="registerForm.gender" placeholder="请选择性别">
                  <el-option label="男" value="male" />
                  <el-option label="女" value="female" />
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :md="8" :sm="24">
              <el-form-item label="年龄" prop="age">
                <el-input-number
                  v-model="registerForm.age"
                  :min="18"
                  :max="65"
                  placeholder="请输入年龄"
                  controls-position="right"
                  style="width: 100%"
                />
              </el-form-item>
            </el-col>
            <el-col :md="8" :sm="24">
              <el-form-item label="校区" prop="campusId">
                <el-select
                  v-model="registerForm.campusId"
                  placeholder="请选择校区"
                  loading-text="加载中..."
                  :loading="loadingCampuses"
                >
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
            <el-input
              v-model="registerForm.email"
              placeholder="请输入邮箱地址"
              clearable
              prefix-icon="Message"
            />
          </el-form-item>
        </div>

        <!-- 教练资料部分 -->
        <div class="form-section">
          <div class="section-header">
            <div class="section-icon">📸</div>
            <h3>教练资料</h3>
          </div>

          <el-form-item label="教练照片" prop="avatar">
            <div class="avatar-upload-container">
              <el-upload
                ref="uploadRef"
                class="avatar-uploader"
                :show-file-list="false"
                :before-upload="beforeAvatarUpload"
                :on-success="handleAvatarSuccess"
                :on-error="handleAvatarError"
                :action="uploadApi"
                :headers="uploadHeaders"
                :disabled="uploading"
              >
                <div class="avatar-upload-area">
                  <img
                    v-if="registerForm.avatar"
                    :src="registerForm.avatar"
                    class="avatar-preview"
                  />
                  <div v-else class="avatar-placeholder">
                    <el-icon size="24" class="upload-icon">
                      <Plus />
                    </el-icon>
                    <p>点击上传照片</p>
                  </div>
                  <div v-if="uploading" class="uploading-overlay">
                    <el-icon class="loading-icon"><Loading /></el-icon>
                  </div>
                </div>
              </el-upload>
              <div class="upload-tips">
                <p>支持 JPG、PNG 格式，文件大小不超过 2MB</p>
                <p>建议尺寸：200×200像素，正方形照片</p>
              </div>
            </div>
          </el-form-item>

          <el-form-item label="比赛成绩描述" prop="achievements">
            <el-input
              v-model="registerForm.achievements"
              type="textarea"
              :rows="5"
              placeholder="请详细描述您的乒乓球比赛经历、获得的成绩和教学经验（不少于50字）"
              maxlength="500"
              show-word-limit
              resize="none"
            />
            <div class="achievement-tips">
              <el-text type="info" size="small">
                💡 温馨提示：详细的比赛成绩描述，有助于学员了解您的专业水平
              </el-text>
            </div>
          </el-form-item>
        </div>

        <!-- 协议同意部分 -->
        <div class="agreement-section">
          <el-form-item prop="agreement">
            <el-checkbox v-model="registerForm.agreement">
              我已阅读并同意
              <el-link type="primary" @click="showAgreement('coach')">《教练员协议》</el-link>
              和
              <el-link type="primary" @click="showAgreement('privacy')">《隐私政策》</el-link>
            </el-checkbox>
          </el-form-item>
        </div>

        <!-- 提交按钮 -->
        <div class="submit-section">
          <el-button
            type="primary"
            class="register-btn"
            :loading="submitting"
            @click="handleSubmit"
          >
            {{ submitting ? '提交中...' : '提交申请' }}
          </el-button>
          
          <div class="audit-notice">
            <el-alert
              title="提示：提交申请后需校区管理员审核后方可使用"
              type="info"
              :closable="false"
              show-icon
            />
          </div>
        </div>
      </el-form>

      <!-- 底部导航 -->
      <div class="register-footer">
        <p>已有账户？</p>
        <el-link type="primary" :underline="false" @click="navigateToLogin">
          <el-button text type="primary">立即登录</el-button>
        </el-link>
      </div>
    </div>

    <!-- 协议对话框 -->
    <el-dialog
      v-model="agreementDialog.visible"
      :title="agreementDialog.title"
      width="70%"
      top="5vh"
    >
      <div class="agreement-content">
        <p v-if="agreementDialog.loading">加载中...</p>
        <div v-else v-html="agreementDialog.content"></div>
      </div>
      <template #footer>
        <el-button @click="agreementDialog.visible = false">取消</el-button>
        <el-button type="primary" @click="agreeToAgreement">同意并继续</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Loading } from '@element-plus/icons-vue'

// 路由和状态管理
const router = useRouter()

// 表单引用
const registerFormRef = ref()
const uploadRef = ref()

// 加载状态
const submitting = ref(false)
const uploading = ref(false)
const loadingCampuses = ref(false)

// API端点配置（预留变量，实际使用时从环境配置获取）
const API_BASE = import.meta.env.VITE_API_BASE || '/api'
const uploadApi = `${API_BASE}/upload/avatar`
const campusesApi = `${API_BASE}/campuses`
const registerApi = `${API_BASE}/auth/coach/register`
const agreementApi = `${API_BASE}/agreements`

// 上传 headers（预留认证token）
const uploadHeaders = computed(() => ({
  Authorization: `Bearer ${localStorage.getItem('token') || ''}`
}))

// 校区列表（从API获取）
const campusList = ref([])

// 协议对话框状态
const agreementDialog = reactive({
  visible: false,
  title: '',
  content: '',
  loading: false,
  type: ''
})

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
  agreement: false
})

// 表单验证规则
const registerRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度在3-20个字符', trigger: 'blur' },
    { 
      pattern: /^[a-zA-Z0-9_]+$/, 
      message: '用户名只能包含字母、数字和下划线', 
      trigger: 'blur' 
    }
  ],
  realName: [
    { required: true, message: '请输入真实姓名', trigger: 'blur' },
    { min: 2, max: 10, message: '姓名长度在2-10个字符', trigger: 'blur' }
  ],
  phone: [
    { required: true, message: '请输入手机号码', trigger: 'blur' },
    { 
      pattern: /^1[3-9]\d{9}$/, 
      message: '请输入正确的手机号码格式', 
      trigger: 'blur' 
    }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 8, max: 16, message: '密码长度在8-16个字符', trigger: 'blur' },
    { 
      pattern: /^(?=.*[a-zA-Z])(?=.*\d)(?=.*[\W_]).{8,16}$/,
      message: '必须包含字母、数字和特殊字符',
      trigger: 'blur'
    }
  ],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    {
      validator: (rule, value, callback) => {
        if (value !== registerForm.password) {
          callback(new Error('两次输入的密码不一致'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ],
  gender: [
    { required: true, message: '请选择性别', trigger: 'change' }
  ],
  age: [
    { required: true, message: '请输入年龄', trigger: 'blur' },
    { type: 'number', min: 18, max: 65, message: '年龄必须在18-65岁之间', trigger: 'blur' }
  ],
  campusId: [
    { required: true, message: '请选择校区', trigger: 'change' }
  ],
  email: [
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
  ],
  avatar: [
    { required: true, message: '请上传教练照片', trigger: 'change' }
  ],
  achievements: [
    { required: true, message: '\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0请输入比赛成绩描述', trigger: 'blur' },
    { min: 50, message: '描述不少于50个字', trigger: 'blur' }
  ],
  agreement: [
    {
      validator: (rule, value, callback) => {
        if (!value) {
          callback(new Error('\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0请认真阅读教练员协议和隐私政策'))
        } else {
          callback()
        }
      },
      trigger: 'change'
    }
  ]
}

// 获取校区列表
const fetchCampuses = async () => {
  try {
    loadingCampuses.value = true
    // 模拟API调用，实际使用时替换为真实的API调用
    // const response = await fetch(campusesApi)
    // const data = await response.json()
    // campusList.value = data
    
    // 临时测试数据
    campusList.value = [
      { id: 1, name: '北京总部校区' },
      { id: 2, name: '上海浦东校区' },
      { id: 3, name: '广州天河校区' },
      { id: 4, name: '深圳南山校区' },
      { id: 5, name: '杭州西湖校区' }
    ]
  } catch (error) {
    ElMessage.error('获取校区列表失败')
    console.error('Fetch campuses error:', error)
  } finally {
    loadingCampuses.value = false
  }
}

// 上传前验证
const beforeAvatarUpload = (file) => {
  const isJPGOrPNG = file.type === 'image/jpeg' || file.type === 'image/png'
  const isLt2M = file.size / 1024 / 1024 < 2

  if (!isJPGOrPNG) {
    ElMessage.error('头像必须是 JPG 或 PNG 格式!')
    return false
  }
  if (!isLt2M) {
    ElMessage.error('头像大小不能超过 2MB!')
    return false
  }
  
  uploading.value = true
  return true
}

// 上传成功处理
const handleAvatarSuccess = (response) => {
  uploading.value = false
  // 根据实际API响应结构调整
  registerForm.avatar = response.data?.url || response.url
  ElMessage.success('头像上传成功')
}

// 上传失败处理
const handleAvatarError = (error) => {
  uploading.value = false
  ElMessage.error('头像上传失败')
  console.error('Upload error:', error)
}

// 显示协议内容
const showAgreement = async (type) => {
  agreementDialog.type = type
  agreementDialog.title = type === 'coach' ? '教练员协议' : '隐私政策'
  agreementDialog.loading = true
  agreementDialog.visible = true

  try {
    // 模拟API调用获取协议内容
    // const response = await fetch(`${agreementApi}/${type}`)
    // agreementDialog.content = await response.text()
    
    // 临时内容
    agreementDialog.content = type === 'coach' 
      ? '<p>这里是教练员协议的具体内容...</p>'
      : '<p>这里是隐私政策的具体内容...</p>'
  } catch (error) {
    ElMessage.error('获取协议内容失败')
    console.error('Fetch agreement error:', error)
  } finally {
    agreementDialog.loading = false
  }
}

// 同意协议
const agreeToAgreement = () => {
  registerForm.agreement = true
  agreementDialog.visible = false
  ElMessage.success('已同意协议')
}

// 表单提交
const handleSubmit = async () => {
  if (!registerFormRef.value) return

  try {
    // 表单验证
    const valid = await registerFormRef.value.validate()
    if (!valid) return

    submitting.value = true

    // 准备提交数据
    const submitData = {
      username: registerForm.username,
      realName: registerForm.realName,
      phone: registerForm.phone,
      password: registerForm.password,
      gender: registerForm.gender,
      age: registerForm.age,
      campusId: registerForm.campusId,
      email: registerForm.email,
      avatar: registerForm.avatar,
      achievements: registerForm.achievements
    }

    // 模拟API调用
    // const response = await fetch(registerApi, {
    //   method: 'POST',
    //   headers: {
    //     'Content-Type': 'application/json'
    //   },
    //   body: JSON.stringify(submitData)
    // })
    
    // if (response.ok) {
    //   const result = await response.json()
    //   ElMessage.success('注册申请提交成功！请等待管理员审核')
    //   router.push('/login')
    // } else {
    //   throw new Error('注册失败')
    // }

    // 模拟成功
    setTimeout(() => {
      ElMessage.success('注册申请提交成功！请等待管理员审核')
      router.push('/login')
    }, 1000)

  } catch (error) {
    ElMessage.error(error.message || '提交失败，请稍后重试')
    console.error('Submit error:', error)
  } finally {
    submitting.value = false
  }
}

// 导航到登录页
const navigateToLogin = () => {
  router.push('/login')
}

// 组件挂载时获取校区列表
onMounted(() => {
  fetchCampuses()
})
</script>

<style scoped>
.coach-register-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 20px;
  position: relative;
  overflow: hidden;
}

.decorative-background {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  pointer-events: none;
}

.floating-element {
  position: absolute;
  font-size: 3rem;
  opacity: 0.1;
  animation: float 6s ease-in-out infinite;
}

.element-1 { top: 10%; left: 5%; animation-delay: 0s; }
.element-2 { top: 20%; right: 10%; animation-delay: 2s; }
.element-3 { bottom: 30%; left: 15%; animation-delay: 4s; }
.element-4 { bottom: 15%; right: 5%; animation-delay: 1s; }

.gradient-circle {
  position: absolute;
  border-radius: 50%;
  opacity: 0.1;
  background: linear-gradient(135deg, #ff6b6b, #feca57);
}

.circle-1 {
  width: 300px;
  height: 300px;
  top: -100px;
  right: -100px;
}

.circle-2 {
  width: 200px;
  height: 200px;
  bottom: -50px;
  left: -50px;
}

.register-content {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(20px);
  border-radius: 20px;
  padding: 40px;
  width: 100%;
  max-width: 800px;
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.1);
  position: relative;
  z-index: 10;
}

.register-header {
  text-align: center;
  margin-bottom: 40px;
}

.main-title {
  font-size: 2.5rem;
  font-weight: 700;
  background: linear-gradient(135deg, #667eea, #764ba2);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  margin-bottom: 10px;
}

.sub-title {
  font-size: 1.2rem;
  color: #666;
  margin-bottom: 15px;
}

.title-divider {
  width: 60px;
  height: 3px;
  background: linear-gradient(135deg, #667eea, #764ba2);
  margin: 0 auto 15px;
  border-radius: 2px;
}

.description {
  color: #888;
  font-size: 0.95rem;
}

.coach-register-form {
  margin-bottom: 30px;
}

.form-section {
  margin-bottom: 30px;
  padding: 25px;
  background: #f8f9fa;
  border-radius: 12px;
  border-left: 4px solid #667eea;
}

.section-header {
  display: flex;
  align-items: center;
  margin-bottom: 20px;
}

.section-icon {
  font-size: 1.5rem;
  margin-right: 12px;
}

.section-header h3 {
  margin: 0;
  color: #2c3e50;
  font-weight: 600;
}

.password-requirements,
.achievement-tips {
  margin-top: 8px;
  padding: 12px;
  background: #e8f4ff;
  border-radius: 8px;
  border-left: 3px solid #1890ff;
  width: 100%;
}

.avatar-upload-container {
  display: flex;
  align-items: flex-start;
  gap: 20px;
}

.avatar-upload-area {
  width: 120px;
  height: 120px;
  border: 2px dashed #dcdfe6;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.3s ease;
}

.avatar-upload-area:hover {
  border-color: #409eff;
  transform: scale(1.05);
}

.avatar-preview {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 50%;
}

.avatar-placeholder {
  text-align: center;
  color: #8c939d;
}

.upload-icon {
  margin-bottom: 8px;
}

.uploading-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(255, 255, 255, 0.9);
  display: flex;
  align-items: center;
  justify-content: center;
}

.loading-icon {
  animation: rotate 2s linear infinite;
}

.upload-tips {
  flex: 1;
  color: #666;
  font-size: 0.85rem;
}

.upload-tips p {
  margin: 4px 0;
}

.agreement-section {
  margin: 25px 0;
  padding: 20px;
  background: #f0f8ff;
  border-radius: 8px;
}

.submit-section {
  margin-top: 30px;
}

.register-btn {
  width: 100%;
  height: 50px;
  font-size: 1.1rem;
  font-weight: 600;
  background: linear-gradient(135deg, #667eea, #764ba2);
  border: none;
  border-radius: 8px;
  transition: all 0.3s ease;
}

.register-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(102, 126, 234, 0.4);
}

.audit-notice {
  margin-top: 20px;
}

.register-footer {
  text-align: center;
  padding-top: 20px;
  border-top: 1px solid #eee;
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 10px;
}

.agreement-content {
  max-height: 60vh;
  overflow-y: auto;
  padding: 10px;
}

@keyframes float {
  0%, 100% {
    transform: translateY(0) rotate(0deg);
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

/* 响应式设计 */
@media (max-width: 768px) {
  .register-content {
    padding: 25px;
    margin: 10px;
  }
  
  .main-title {
    font-size: 2rem;
  }
  
  .form-section {
    padding: 20px;
  }
  
  .avatar-upload-container {
    flex-direction: column;
    align-items: center;
  }
  
  .upload-tips {
    text-align: center;
    margin-top: 15px;
  }
}

@media (max-width: 480px) {
  .register-content {
    padding: 20px;
  }
  
  .main-title {
    font-size: 1.8rem;
  }
  
  .form-section {
    padding: 15px;
  }
}
</style>