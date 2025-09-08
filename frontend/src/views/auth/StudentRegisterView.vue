<template>
  <div class="register-container">
    <el-card class="register-card">
      <template #header>
        <div class="card-header">
          <span>👨‍🎓 学生注册</span>
        </div>
      </template>

      <el-form
        ref="registerFormRef"
        :model="registerForm"
        :rules="registerRules"
        label-width="100px"
        label-position="right"
        size="large"
      >
        <el-form-item label="用户名" prop="username">
          <el-input v-model="registerForm.username" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input
            v-model="registerForm.password"
            type="password"
            placeholder="8-16位，含字母、数字和特殊字符"
            show-password
          />
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input
            v-model="registerForm.confirmPassword"
            type="password"
            placeholder="请再次输入密码"
            show-password
          />
        </el-form-item>
        <el-form-item label="真实姓名" prop="realName">
          <el-input v-model="registerForm.realName" placeholder="请输入真实姓名" />
        </el-form-item>
        <el-form-item label="所属校区" prop="campusId">
          <el-select
            v-model="registerForm.campusId"
            placeholder="请选择校区"
            style="width: 100%"
            filterable
          >
            <el-option
              v-for="campus in campusList"
              :key="campus.id"
              :label="campus.name"
              :value="campus.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="电话号码" prop="phone">
          <el-input v-model="registerForm.phone" placeholder="请输入电话号码" />
        </el-form-item>

        <el-form-item label="性别" prop="gender">
          <el-radio-group v-model="registerForm.gender">
            <el-radio label="MALE">男</el-radio>
            <el-radio label="FEMALE">女</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="年龄" prop="age">
          <el-input-number v-model="registerForm.age" :min="6" :max="60" placeholder="请输入年龄" />
        </el-form-item>
        <el-form-item label="电子邮箱" prop="email">
          <el-input v-model="registerForm.email" placeholder="请输入电子邮箱" />
        </el-form-item>

        <el-form-item>
          <el-button
            type="primary"
            class="register-button"
            :loading="isLoading"
            @click="handleRegister"
            >立即注册</el-button
          >
        </el-form-item>
      </el-form>

      <div class="login-link">
        已有账号？<el-link type="primary" @click="router.push('/login')">立即登录</el-link>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import api from '@/utils/api'

const router = useRouter()
const registerFormRef = ref(null)
const isLoading = ref(false)
const campusList = ref([]) // 用于存储动态获取的校区列表

// 注册表单数据
const registerForm = ref({
  username: '',
  password: '',
  confirmPassword: '',
  realName: '',
  campusId: '',
  phone: '',
  gender: null, // 默认为null，让用户主动选择
  age: null,
  email: ''
})

// 自定义密码验证规则
const validatePassword = (rule, value, callback) => {
  const regex = /^(?=.*[A-Za-z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{8,16}$/
  if (!value) {
    callback(new Error('请输入密码'))
  } else if (!regex.test(value)) {
    callback(new Error('密码必须是8-16位，且包含字母、数字和特殊字符'))
  } else {
    callback()
  }
}

// 确认密码验证规则
const validateConfirmPassword = (rule, value, callback) => {
  if (!value) {
    callback(new Error('请再次输入密码'))
  } else if (value !== registerForm.value.password) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

// 表单验证规则
const registerRules = {
  // 必填项
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 4, message: '用户名长度不能少于4位', trigger: 'blur' }
  ],
  password: [{ required: true, validator: validatePassword, trigger: 'blur' }],
  confirmPassword: [{ required: true, validator: validateConfirmPassword, trigger: 'blur' }],
  realName: [{ required: true, message: '请输入真实姓名', trigger: 'blur' }],
  campusId: [{ required: true, message: '请选择校区', trigger: 'change' }],
  phone: [
    { required: true, message: '请输入电话号码', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入有效的手机号码', trigger: 'blur' }
  ],
  // 选填项
  email: [{ type: 'email', message: '请输入有效的邮箱地址', trigger: ['blur', 'change'] }]
}

// 获取校区列表
const fetchCampusList = async () => {
  try {
    // 假设获取校区的API是 '/campus/list'
    const response = await api.get('/campus/list')
    campusList.value = response.data
  } catch (error) {
    ElMessage.error('校区列表加载失败')
    // 加载失败时提供一些默认选项
    campusList.value = [
      { id: '1', name: '中心校区 (加载失败)' },
      { id: '2', name: '分校区 (加载失败)' }
    ]
  }
}

// 页面加载时执行
onMounted(() => {
  fetchCampusList()
})

// 处理注册提交
const handleRegister = async () => {
  if (!registerFormRef.value) return
  await registerFormRef.value.validate(async (valid) => {
    if (valid) {
      isLoading.value = true
      try {
        // 提交前移除确认密码字段
        const { confirmPassword, ...payload } = registerForm.value
        await api.post('/register/student', payload)
        ElMessage.success('注册成功！即将跳转到登录页面...')
        setTimeout(() => {
          router.push('/login')
        }, 1500)
      } catch (error) {
        ElMessage.error(error.response?.data?.message || '注册失败，请稍后再试')
      } finally {
        isLoading.value = false
      }
    } else {
      ElMessage.warning('请检查表单信息是否填写完整')
    }
  })
}
</script>

<style scoped>
.register-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
  padding: 24px;
}

.register-card {
  width: 100%;
  max-width: 500px;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.card-header {
  text-align: center;
  font-size: 22px;
  font-weight: 600;
  color: #333;
}

.register-button {
  width: 100%;
}

.login-link {
  margin-top: 16px;
  text-align: center;
  font-size: 14px;
}
</style>