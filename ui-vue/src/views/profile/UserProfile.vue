<template>
  <div class="user-profile">
    <el-card class="profile-card">
      <template #header>
        <div class="card-header">
          <h2>个人信息</h2>
        </div>
      </template>

      <div class="profile-content">
        <div class="avatar-section">
          <el-upload
            class="avatar-uploader"
            action="/api/upload/avatar"
            :show-file-list="false"
            :on-success="handleAvatarSuccess"
            :before-upload="beforeAvatarUpload"
          >
            <el-avatar v-if="userStore.userProfile?.avatar" :size="120" :src="userStore.userProfile.avatar" />
            <el-avatar v-else :size="120">
              {{ userStore.userInfo?.username?.charAt(0).toUpperCase() }}
            </el-avatar>
            <div class="avatar-overlay">
              <el-icon :size="24"><Camera /></el-icon>
              <p>点击更换</p>
            </div>
          </el-upload>
          <h3>{{ userStore.userInfo?.username }}</h3>
          <el-tag v-if="userStore.isAdmin" type="danger" size="small">管理员</el-tag>
          <el-tag v-else type="info" size="small">普通用户</el-tag>
        </div>

        <el-tabs v-model="activeTab" class="info-tabs">
          <el-tab-pane label="基本信息" name="basic">
            <el-form
              ref="basicFormRef"
              :model="basicForm"
              :rules="basicRules"
              label-width="100px"
              class="info-form"
            >
              <el-form-item label="用户名">
                <el-input :value="userStore.userInfo?.username" disabled />
              </el-form-item>
              <el-form-item label="真实姓名" prop="realName">
                <el-input v-model="basicForm.realName" placeholder="请输入真实姓名" />
              </el-form-item>
              <el-form-item label="性别" prop="gender">
                <el-radio-group v-model="basicForm.gender">
                  <el-radio :label="1">男</el-radio>
                  <el-radio :label="0">女</el-radio>
                </el-radio-group>
              </el-form-item>
              <el-form-item label="生日" prop="birthday">
                <el-date-picker
                  v-model="basicForm.birthday"
                  type="date"
                  placeholder="选择生日"
                  value-format="YYYY-MM-DD"
                />
              </el-form-item>
              <el-form-item label="手机号" prop="phone">
                <el-input v-model="basicForm.phone" placeholder="请输入手机号" />
              </el-form-item>
              <el-form-item label="邮箱" prop="email">
                <el-input v-model="basicForm.email" placeholder="请输入邮箱" />
              </el-form-item>
              <el-form-item>
                <el-button type="primary" :loading="updating" @click="handleUpdateBasic">
                  保存修改
                </el-button>
              </el-form-item>
            </el-form>
          </el-tab-pane>

          <el-tab-pane label="账号安全" name="security">
            <div class="security-section">
              <h3>修改密码</h3>
              <el-form
                ref="passwordFormRef"
                :model="passwordForm"
                :rules="passwordRules"
                label-width="120px"
                class="security-form"
              >
                <el-form-item label="当前密码" prop="oldPassword">
                  <el-input v-model="passwordForm.oldPassword" type="password" show-password />
                </el-form-item>
                <el-form-item label="新密码" prop="newPassword">
                  <el-input v-model="passwordForm.newPassword" type="password" show-password />
                </el-form-item>
                <el-form-item label="确认新密码" prop="confirmPassword">
                  <el-input v-model="passwordForm.confirmPassword" type="password" show-password />
                </el-form-item>
                <el-form-item>
                  <el-button type="primary" :loading="changingPassword" @click="handleChangePassword">
                    修改密码
                  </el-button>
                </el-form-item>
              </el-form>
            </div>

            <el-divider />

            <div class="danger-section">
              <h3>危险操作</h3>
              <el-alert
                title="注销账号是不可逆的操作，请谨慎操作！"
                type="error"
                :closable="false"
                show-icon
              />
              <el-button type="danger" plain style="margin-top: 16px" @click="handleDeleteAccount">
                注销账号
              </el-button>
            </div>
          </el-tab-pane>

          <el-tab-pane label="登录历史" name="login-history">
            <el-table :data="loginHistory" style="width: 100%">
              <el-table-column prop="loginTime" label="登录时间" width="180" />
              <el-table-column prop="ipAddress" label="IP地址" width="140" />
              <el-table-column prop="location" label="登录地点" />
              <el-table-column prop="device" label="设备信息" />
            </el-table>
          </el-tab-pane>
        </el-tabs>
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { FormInstance } from 'element-plus'
import { Camera } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'
import { userApi } from '@/api/user'
import { authApi } from '@/api/auth'
import type { Gender } from '@/models/entity/UserProfile'

const userStore = useUserStore()
const activeTab = ref('basic')
const basicFormRef = ref<FormInstance>()
const passwordFormRef = ref<FormInstance>()

const updating = ref(false)
const changingPassword = ref(false)

const basicForm = reactive({
  realName: '',
  gender: 1 as Gender,
  birthday: '',
  phone: '',
  email: ''
})

const passwordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const basicRules = {
  realName: [
    { min: 2, max: 20, message: '真实姓名长度应在2-20个字符之间', trigger: 'blur' }
  ],
  phone: [
    {
      validator: (rule: any, value: string, callback: Function) => {
        // 允许空值（手机号是可选的）
        if (!value || value.trim() === '') {
          callback()
          return
        }
        // 如果有值，则验证格式
        const pattern = /^1[3-9]\d{9}$/
        if (pattern.test(value)) {
          callback()
        } else {
          callback(new Error('电话号码格式不正确'))
        }
      },
      trigger: 'blur'
    }
  ],
  email: [
    { 
      type: 'email', 
      message: '邮箱格式不正确', 
      trigger: 'blur',
      required: false
    }
  ]
}

const validateConfirmPassword = (rule: any, value: string, callback: Function) => {
  if (value !== passwordForm.newPassword) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

const passwordRules = {
  oldPassword: [
    { required: true, message: '请输入当前密码', trigger: 'blur' },
    { min: 6, message: '密码至少6位', trigger: 'blur' }
  ],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, message: '密码至少6位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认新密码', trigger: 'blur' },
    { validator: validateConfirmPassword, trigger: 'blur' }
  ]
}

const loginHistory = ref([
  {
    loginTime: '2024-01-15 14:30:25',
    ipAddress: '192.168.1.100',
    location: '北京市',
    device: 'Chrome on Windows'
  },
  {
    loginTime: '2024-01-14 09:15:42',
    ipAddress: '192.168.1.101',
    location: '北京市',
    device: 'Safari on iPhone'
  }
])

const handleAvatarSuccess = (response: any) => {
  if (response.code === 200) {
    ElMessage.success('头像上传成功')
    userStore.userProfile!.avatar = response.data
  } else {
    ElMessage.error('头像上传失败：' + response.message)
  }
}

const beforeAvatarUpload = (file: File) => {
  const isImage = file.type.startsWith('image/')
  const isLt2M = file.size / 1024 / 1024 < 2

  if (!isImage) {
    ElMessage.error('上传文件必须是图片格式!')
  }
  if (!isLt2M) {
    ElMessage.error('图片大小不能超过 2MB!')
  }
  return isImage && isLt2M
}

const handleUpdateBasic = async () => {
  if (!basicFormRef.value) return

  await basicFormRef.value.validate(async (valid: boolean) => {
    if (!valid) return

    updating.value = true
    try {
      // 分别更新 UserProfile 和 SysUser
      // 1. 更新 UserProfile（realName, gender, birthday, avatar）
      const profileData = {
        realName: basicForm.realName,
        gender: basicForm.gender,
        birthday: basicForm.birthday,
        avatar: userStore.userProfile?.avatar
      }
      const profileResult = await userStore.updateProfile(profileData)
      
      // 2. 更新 SysUser（phone, email）
      if (userStore.userInfo?.id) {
        const userData = {
          phone: basicForm.phone || undefined,
          email: basicForm.email || undefined
        }
        // 只有当 phone 或 email 有值时才更新
        if (userData.phone || userData.email) {
          await userApi.updateUser(userStore.userInfo.id, userData)
        }
      }
      
      // 3. 重新获取用户信息以同步数据
      await userStore.fetchUserInfo()
      
      if (profileResult.success) {
        ElMessage.success('资料更新成功')
      } else {
        ElMessage.error(profileResult.message || '更新失败')
      }
    } catch (error) {
      ElMessage.error('更新失败')
      console.error('Update profile error:', error)
    } finally {
      updating.value = false
    }
  })
}

const handleChangePassword = async () => {
  if (!passwordFormRef.value) return

  await passwordFormRef.value.validate(async (valid: boolean) => {
    if (!valid) return

    changingPassword.value = true
    try {
      const result = await userStore.changePassword(
        passwordForm.oldPassword,
        passwordForm.newPassword
      )
      if (result.success) {
        ElMessage.success('密码修改成功')
        passwordFormRef.value.resetFields()
      } else {
        ElMessage.error(result.message || '修改失败')
      }
    } catch (error) {
      ElMessage.error('修改失败')
      console.error('Change password error:', error)
    } finally {
      changingPassword.value = false
    }
  })
}

const handleDeleteAccount = async () => {
  try {
    await ElMessageBox.confirm(
      '注销账号是不可逆的操作，您的所有数据将被永久删除。确定要继续吗？',
      '危险操作',
      {
        type: 'error',
        confirmButtonText: '我已知晓，继续注销',
        cancelButtonText: '取消'
      }
    )

    const confirmInput = await ElMessageBox.prompt(
      '请输入 "DELETE" 以确认注销操作',
      '确认注销',
      {
        type: 'error',
        confirmButtonText: '确认注销',
        cancelButtonText: '取消'
      }
    )

    if (confirmInput.value === 'DELETE') {
      const result = await userStore.deleteAccount()
      if (result.success) {
        ElMessage.success('账号已注销')
      } else {
        ElMessage.error(result.message || '注销失败')
      }
    } else {
      ElMessage.warning('输入不正确，注销已取消')
    }
  } catch (error) {
    // 取消操作
  }
}

const loadUserData = () => {
  if (userStore.userProfile) {
    Object.assign(basicForm, {
      realName: userStore.userProfile.realName || '',
      gender: userStore.userProfile.gender || 1,
      birthday: userStore.userProfile.birthday || '',
      phone: userStore.userProfile.phone || '',
      email: userStore.userProfile.email || ''
    })
  }
}

onMounted(() => {
  loadUserData()
})
</script>

<style scoped>
.user-profile {
  max-width: 1000px;
  margin: 0 auto;
  padding: 20px;
}

.profile-card {
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.card-header h2 {
  margin: 0;
  color: #303133;
}

.profile-content {
  display: flex;
  gap: 40px;
}

.avatar-section {
  flex: 0 0 200px;
  text-align: center;
  padding: 20px;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
  border-radius: 12px;
}

.avatar-uploader {
  position: relative;
  display: inline-block;
  cursor: pointer;
}

.avatar-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.5);
  border-radius: 50%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: white;
  opacity: 0;
  transition: opacity 0.3s;
}

.avatar-uploader:hover .avatar-overlay {
  opacity: 1;
}

.avatar-overlay p {
  margin: 4px 0 0 0;
  font-size: 12px;
}

.info-tabs {
  flex: 1;
}

.info-form {
  max-width: 500px;
}

.security-section h3 {
  margin-top: 0;
  margin-bottom: 20px;
  color: #303133;
}

.security-form {
  max-width: 500px;
}

.danger-section {
  margin-top: 40px;
  padding-top: 40px;
  border-top: 1px solid #e4e7ed;
}

.danger-section h3 {
  margin-top: 0;
  margin-bottom: 16px;
  color: #f56c6c;
}

:deep(.el-input.is-disabled .el-input__wrapper) {
  background-color: #f5f7fa;
}
</style>
