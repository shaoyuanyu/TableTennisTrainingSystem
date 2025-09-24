<template>
  <div class="profile-view">
    <!-- 背景装饰元素 -->
    <div class="background-decorations">
      <!-- 个人资料相关图标 -->
      <div class="profile-icon user-icon">👤</div>
      <div class="profile-icon edit-icon">✏️</div>
      <div class="profile-icon settings-icon">⚙️</div>
      <div class="profile-icon id-icon">🆔</div>

      <!-- 个性化装饰 -->
      <div class="personal-decoration star-icon">⭐</div>
      <div class="personal-decoration heart-icon">💖</div>
      <div class="personal-decoration camera-icon">📷</div>

      <!-- 艺术字装饰 -->
      <div class="art-text">
        <div class="art-text-main">PROFILE</div>
        <div class="art-text-sub">个人资料</div>
      </div>

      <!-- 个性几何装饰 -->
      <div class="profile-decoration pro-1"></div>
      <div class="profile-decoration pro-2"></div>
      <div class="profile-decoration pro-3"></div>
    </div>

    <el-row :gutter="24">
      <el-col :span="8">
        <!-- 用户头像和基本信息 -->
        <el-card class="profile-card main-profile-card">
          <div class="avatar-section">
            <el-avatar :size="100" :src="userInfo.avatar">
              <el-icon size="50">
                <User />
              </el-icon>
            </el-avatar>
            <el-upload :show-file-list="false" :before-upload="beforeUpload" :on-success="handleAvatarSuccess"
              action="/api/upload/avatar" class="avatar-upload">
              <IconButton size="sm" variant="glass" :circle="true">
                <el-icon>
                  <Camera />
                </el-icon>
              </IconButton>
            </el-upload>
          </div>
          <div class="user-info">
            <h3>{{ userInfo.name || '未设置姓名' }}</h3>
            <p class="user-role">{{ getRoleText() }}</p>
            <div class="user-details">
              <div class="detail-item">
                <span class="label">用户名:</span>
                <span class="value">{{ userInfo.username }}</span>
              </div>
              <div class="detail-item">
                <span class="label">手机号:</span>
                <span class="value">{{ userInfo.phone }}</span>
              </div>
              <div class="detail-item">
                <span class="label">邮箱:</span>
                <span class="value">{{ userInfo.email || '未设置' }}</span>
              </div>
              <div class="detail-item" v-if="userInfo.campusName">
                <span class="label">校区:</span>
                <span class="value">{{ userInfo.campusName }}</span>
              </div>
            </div>
          </div>
        </el-card>
        <!-- 动态功能菜单卡片 -->
        <el-card class="profile-card" style="margin-top: 16px">
          <template #header>
            <span>功能菜单</span>
          </template>
          <el-menu class="profile-menu" :default-active="''" router>
            <el-menu-item v-if="isStudent" index="/student/schedule">
              <el-icon>
                <Clock />
              </el-icon>
              我的课表
            </el-menu-item>
            <el-menu-item v-if="isStudent" index="/student/recharge">
              <el-icon>
                <Wallet />
              </el-icon>
              账户管理
            </el-menu-item>
            <el-menu-item v-if="isStudent" index="/student/matches">
              <el-icon>
                <Medal />
              </el-icon>
              我的比赛
            </el-menu-item>
            <el-menu-item v-if="isStudent" index="/student/evaluation">
              <el-icon>
                <EditPen />
              </el-icon>
              训练评价
            </el-menu-item>
            <el-menu-item v-if="isCoach" index="/coach/schedule">
              <el-icon>
                <Clock />
              </el-icon>
              我的课表
            </el-menu-item>
            <el-menu-item v-if="isCoach" index="/coach/evaluation">
              <el-icon>
                <EditPen />
              </el-icon>
              学员评价
            </el-menu-item>
            <el-menu-item v-if="isCampusAdmin" index="/campus/students">
              <el-icon>
                <User />
              </el-icon>
              学员管理
            </el-menu-item>
            <el-menu-item v-if="isCampusAdmin" index="/campus/coaches">
              <el-icon>
                <Avatar />
              </el-icon>
              教练管理
            </el-menu-item>
            <el-menu-item v-if="isSuperAdmin" index="/admin/campus">
              <el-icon>
                <OfficeBuilding />
              </el-icon>
              校区管理
            </el-menu-item>
            <el-menu-item v-if="isSuperAdmin" index="/admin/service">
              <el-icon>
                <CreditCard />
              </el-icon>
              服务状态
            </el-menu-item>
            <!-- 通用功能 -->
            <el-menu-item index="/messages">
              <el-icon>
                <Document />
              </el-icon>
              消息通知
            </el-menu-item>
          </el-menu>
        </el-card>
      </el-col>
      <el-col :span="16">
        <!-- 个人信息编辑 -->
        <el-card class="profile-card">
          <template #header>
            <span>个人信息</span>
          </template>
          <el-form ref="profileFormRef" :model="profileForm" :rules="profileRules" label-width="100px" size="large">
            <el-row :gutter="16">
              <el-col :span="12">
                <el-form-item label="真实姓名" prop="name">
                  <el-input v-model="profileForm.name" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="性别" prop="gender">
                  <el-select v-model="profileForm.gender" style="width: 100%">
                    <el-option label="男" value="male" />
                    <el-option label="女" value="female" />
                  </el-select>
                </el-form-item>
              </el-col>
            </el-row>
            <el-row :gutter="16">
              <el-col :span="12">
                <el-form-item label="年龄" prop="age">
                  <el-input-number v-model="profileForm.age" :min="1" :max="120" style="width: 100%" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="手机号码" prop="phone">
                  <el-input v-model="profileForm.phone" />
                </el-form-item>
              </el-col>
            </el-row>
            <el-form-item label="邮箱地址" prop="email">
              <el-input v-model="profileForm.email" />
            </el-form-item>
            <el-form-item>
              <PrimaryButton @click="updateProfile" :loading="updating">保存修改</PrimaryButton>
              <OutlineButton @click="resetForm">重置</OutlineButton>
            </el-form-item>
          </el-form>
        </el-card>
        <!-- 修改密码 -->
        <el-card class="profile-card">
          <template #header>
            <span>修改密码</span>
          </template>
          <el-form ref="passwordFormRef" :model="passwordForm" :rules="passwordRules" label-width="100px" size="large">
            <el-form-item label="当前密码" prop="oldPassword">
              <el-input v-model="passwordForm.oldPassword" type="password" show-password />
            </el-form-item>
            <el-form-item label="新密码" prop="newPassword">
              <el-input v-model="passwordForm.newPassword" type="password" show-password />
            </el-form-item>
            <el-form-item label="确认密码" prop="confirmPassword">
              <el-input v-model="passwordForm.confirmPassword" type="password" show-password />
            </el-form-item>
            <el-form-item>
              <PrimaryButton @click="changePassword" :loading="changingPassword">修改密码</PrimaryButton>
            </el-form-item>
          </el-form>
        </el-card>
        <!-- 操作日志卡片 -->
        <el-card class="profile-card" style="margin-top: 16px">
          <template #header>
            <span>操作日志</span>
          </template>
          <el-table :data="operationLogs" style="width: 100%">
            <el-table-column prop="time" label="时间" width="160" />
            <el-table-column prop="action" label="操作" />
            <el-table-column prop="result" label="结果" width="100" />
          </el-table>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import {computed, onMounted, reactive, ref} from 'vue'
import {useUserStore} from '@/stores/user'
import {ElMessage} from 'element-plus'
import {IconButton, OutlineButton, PrimaryButton} from '@/components/buttons'
import {
  Avatar,
  Camera,
  Clock,
  CreditCard,
  Document,
  EditPen,
  Medal,
  OfficeBuilding,
  User,
  Wallet,
} from '@element-plus/icons-vue'

// 角色判断
const isStudent = computed(() => userInfo.value.role === 'student')
const isCoach = computed(() => userInfo.value.role === 'coach')
const isCampusAdmin = computed(() => userInfo.value.role === 'campus_admin')
const isSuperAdmin = computed(() => userInfo.value.role === 'super_admin')

// 操作日志（示例数据，实际应从后端获取）
const operationLogs = ref([
  { time: '2025-09-09 10:01', action: '修改个人信息', result: '成功' },
  { time: '2025-09-08 15:23', action: '预约课程', result: '成功' },
  { time: '2025-09-07 09:10', action: '修改密码', result: '成功' },
])

const userStore = useUserStore()

// 表单引用
const profileFormRef = ref()
const passwordFormRef = ref()

// 加载状态
const updating = ref(false)
const changingPassword = ref(false)

// 用户信息
const userInfo = computed(() => userStore.userInfo)

// 个人信息表单
const profileForm = reactive({
  name: '',
  gender: '',
  age: null,
  phone: '',
  email: '',
})

// 密码表单
const passwordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: '',
})

// 表单验证规则
const profileRules = {
  name: [{ required: true, message: '请输入真实姓名', trigger: 'blur' }],
  phone: [
    { required: true, message: '请输入手机号码', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' },
  ],
  email: [{ type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }],
}

const passwordRules = {
  oldPassword: [{ required: true, message: '请输入当前密码', trigger: 'blur' }],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 8, max: 16, message: '密码长度在 8 到 16 个字符', trigger: 'blur' },
    {
      pattern: /^(?=.*[a-zA-Z])(?=.*\d)(?=.*[\W_]).{8,16}$/,
      message: '密码必须包含字母、数字和特殊字符',
      trigger: 'blur',
    },
  ],
  confirmPassword: [
    { required: true, message: '请确认新密码', trigger: 'blur' },
    {
      validator: (rule, value, callback) => {
        if (value !== passwordForm.newPassword) {
          callback(new Error('两次输入密码不一致'))
        } else {
          callback()
        }
      },
      trigger: 'blur',
    },
  ],
}

// 获取角色文本
const getRoleText = () => {
  const roleMap = {
    super_admin: '超级管理员',
    campus_admin: '校区管理员',
    student: '学员',
    coach: '教练',
  }
  return roleMap[userInfo.value.role] || '未知角色'
}

// 初始化表单数据
const initFormData = () => {
  profileForm.name = userInfo.value.name || ''
  profileForm.gender = userInfo.value.gender || ''
  profileForm.age = userInfo.value.age || null
  profileForm.phone = userInfo.value.phone || ''
  profileForm.email = userInfo.value.email || ''
}

// 上传头像前检查
const beforeUpload = (file) => {
  const isImage = file.type === 'image/jpeg' || file.type === 'image/png'
  const isLt2M = file.size / 1024 / 1024 < 2

  if (!isImage) {
    ElMessage.error('头像只能是 JPG 或 PNG 格式!')
    return false
  }
  if (!isLt2M) {
    ElMessage.error('头像大小不能超过 2MB!')
    return false
  }
  return true
}

// 头像上传成功
const handleAvatarSuccess = () => {
  ElMessage.success('头像更新成功')
  // 这里应该更新用户信息
}

// 更新个人信息
const updateProfile = async () => {
  if (!profileFormRef.value) return

  try {
    const valid = await profileFormRef.value.validate()
    if (!valid) return

    updating.value = true

    const success = await userStore.updateUserInfo(profileForm)
    if (success) {
      ElMessage.success('个人信息更新成功')
    }
  } catch {
    ElMessage.error('更新失败，请重试')
  } finally {
    updating.value = false
  }
}

// 重置表单
const resetForm = () => {
  initFormData()
}

// 修改密码
const changePassword = async () => {
  if (!passwordFormRef.value) return

  try {
    const valid = await passwordFormRef.value.validate()
    if (!valid) return

    changingPassword.value = true

    const success = await userStore.changePassword({
      oldPassword: passwordForm.oldPassword,
      newPassword: passwordForm.newPassword,
    })

    if (success) {
      ElMessage.success('密码修改成功')
      passwordForm.oldPassword = ''
      passwordForm.newPassword = ''
      passwordForm.confirmPassword = ''
    }
  } catch {
    ElMessage.error('密码修改失败，请重试')
  } finally {
    changingPassword.value = false
  }
}

onMounted(() => {
  initFormData()
})
</script>

<style scoped>
.profile-view {
  background: linear-gradient(135deg,
      #667eea 0%,
      #764ba2 25%,
      #f093fb 50%,
      #f5576c 75%,
      #4facfe 100%);
  min-height: 100vh;
  padding: 24px;
  position: relative;
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
  z-index: 1;
}

/* 个人资料图标装饰 */
.profile-icon {
  position: absolute;
  font-size: 58px;
  opacity: 0.08;
  animation: float 8s ease-in-out infinite;
}

.user-icon {
  top: 18%;
  left: 10%;
  animation-delay: 0s;
}

.edit-icon {
  top: 65%;
  right: 8%;
  animation-delay: 2s;
}

.settings-icon {
  bottom: 30%;
  left: 6%;
  animation-delay: 4s;
}

.id-icon {
  top: 45%;
  left: 4%;
  animation-delay: 6s;
}

/* 个性化装饰 */
.personal-decoration {
  position: absolute;
  font-size: 65px;
  opacity: 0.06;
  animation: rotate 22s linear infinite;
}

.star-icon {
  top: 25%;
  right: 12%;
  animation-delay: 1s;
}

.heart-icon {
  bottom: 20%;
  right: 15%;
  animation-delay: 3s;
}

.camera-icon {
  top: 70%;
  left: 18%;
  animation-delay: 5s;
}

/* 艺术字装饰 */
.art-text {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  text-align: center;
  opacity: 0.12;
  z-index: 1;
  pointer-events: none;
  user-select: none;
}

.art-text-main {
  font-size: 115px;
  font-weight: 900;
  color: rgba(255, 255, 255, 0.7);
  letter-spacing: 12px;
  text-shadow: 3px 3px 6px rgba(0, 0, 0, 0.3);
  font-family: 'Arial Black', sans-serif;
  -webkit-text-stroke: 1px rgba(255, 255, 255, 0.2);
}

.art-text-sub {
  font-size: 45px;
  font-weight: 600;
  color: rgba(255, 255, 255, 0.6);
  letter-spacing: 8px;
  margin-top: -18px;
  text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.2);
}

/* 个性几何装饰 */
.profile-decoration {
  position: absolute;
  background: linear-gradient(45deg, rgba(255, 255, 255, 0.08), rgba(255, 255, 255, 0.04));
  border-radius: 20% 80% 80% 20% / 20% 20% 80% 80%;
}

.pro-1 {
  top: 35%;
  left: 3%;
  width: 110px;
  height: 110px;
  animation: morphFloat 18s ease-in-out infinite;
}

.pro-2 {
  bottom: 35%;
  right: 8%;
  width: 90px;
  height: 90px;
  animation: morphFloat 22s ease-in-out infinite reverse;
}

.pro-3 {
  top: 60%;
  right: 4%;
  width: 70px;
  height: 70px;
  animation: pulse 12s ease-in-out infinite;
}

:deep(.el-row) {
  position: relative;
  z-index: 10;
}

.profile-card {
  margin-bottom: 24px;
  border: none;
  border-radius: 16px;
  backdrop-filter: blur(15px);
  background: rgba(255, 255, 255, 0.95);
  box-shadow:
    0 8px 32px rgba(0, 0, 0, 0.12),
    0 0 0 1px rgba(255, 255, 255, 0.2) inset;
  transition: all 0.3s ease;
}

.profile-card:hover {
  transform: translateY(-3px);
  box-shadow:
    0 12px 40px rgba(0, 0, 0, 0.18),
    0 0 0 1px rgba(255, 255, 255, 0.3) inset;
}

.main-profile-card {
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.95), rgba(102, 126, 234, 0.1));
}

.avatar-section {
  text-align: center;
  margin-bottom: 24px;
  padding: 20px;
  background: rgba(255, 255, 255, 0.5);
  backdrop-filter: blur(10px);
  border-radius: 12px;
  border: 1px solid rgba(255, 255, 255, 0.3);
}

.avatar-upload {
  margin-top: 12px;
}

.user-info h3 {
  margin: 16px 0 8px 0;
  font-size: 22px;
  font-weight: 700;
  color: #333;
}

.user-role {
  color: #667eea;
  margin: 0 0 20px 0;
  font-weight: 600;
  font-size: 16px;
}

.user-details {
  text-align: left;
}

.detail-item {
  display: flex;
  justify-content: space-between;
  margin-bottom: 16px;
  padding: 12px 16px;
  background: rgba(255, 255, 255, 0.6);
  backdrop-filter: blur(8px);
  border-radius: 8px;
  border: 1px solid rgba(255, 255, 255, 0.2);
  transition: all 0.3s ease;
}

.detail-item:hover {
  background: rgba(255, 255, 255, 0.8);
  transform: translateX(2px);
}

.label {
  color: #666;
  font-size: 14px;
  font-weight: 500;
}

.value {
  color: #333;
  font-weight: 600;
}

.edit-form {
  background: rgba(255, 255, 255, 0.6);
  backdrop-filter: blur(10px);
  border-radius: 12px;
  padding: 24px;
  border: 1px solid rgba(255, 255, 255, 0.3);
}

.form-actions {
  text-align: right;
  margin-top: 24px;
  padding-top: 16px;
  border-top: 1px solid rgba(0, 0, 0, 0.05);
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

@keyframes rotate {
  from {
    transform: rotate(0deg);
  }

  to {
    transform: rotate(360deg);
  }
}

@keyframes morphFloat {

  0%,
  100% {
    transform: translateY(0px);
    border-radius: 20% 80% 80% 20% / 20% 20% 80% 80%;
  }

  25% {
    transform: translateY(-12px);
    border-radius: 60% 40% 30% 70% / 60% 30% 70% 40%;
  }

  50% {
    transform: translateY(-6px);
    border-radius: 40% 60% 70% 30% / 40% 70% 30% 60%;
  }

  75% {
    transform: translateY(-10px);
    border-radius: 70% 30% 40% 60% / 30% 60% 40% 70%;
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
    transform: scale(1.2);
  }
}

/* 响应式设计 */
@media (max-width: 768px) {
  .profile-view {
    padding: 16px;
  }

  .art-text-main {
    font-size: 75px;
  }

  .art-text-sub {
    font-size: 30px;
  }

  .profile-icon,
  .personal-decoration {
    font-size: 40px;
  }
}
</style>
