<template>
  <div class="user-management">
    <el-card class="header-card">
      <div class="header-content">
        <div class="title-section">
          <h2>用户管理</h2>
          <p class="subtitle">管理系统用户信息</p>
        </div>
        <el-button type="primary" :icon="Plus" @click="handleAddUser">
          新增用户
        </el-button>
      </div>
    </el-card>

    <UserSearchForm
      @search="handleSearch"
      @reset="handleReset"
    />

    <UserTable
      :loading="loading"
      :user-list="userList"
      :total="total"
      :page-num="pageNum"
      :page-size="pageSize"
      @edit="handleEdit"
      @view="handleView"
      @reset-password="handleResetPassword"
      @block="handleBlockUser"
      @unblock="handleUnblockUser"
      @delete="handleDeleteUser"
      @size-change="handleSizeChange"
      @current-change="handleCurrentChange"
    />

    <UserFormDialog
      v-model="dialogVisible"
      :dialog-type="dialogType"
      :user-form="userForm"
      :submitting="submitting"
      @submit="handleSubmit"
      @close="handleDialogClose"
    />

    <UserDetailDialog
      v-model="detailDialogVisible"
      :user="currentUser"
    />
  </div>

  <el-button type="primary" @click="handleExportAllUsers">导出所有用户</el-button>
  <el-button type="success" @click="showExportDialog = true">选择字段导出</el-button>
  <!-- 导出用户弹窗组件 -->
  <ExportUsersDialog
      :visible="showExportDialog"
      @update:visible="showExportDialog = $event"
      @exported="handleExported"
  />
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { userApi } from '@/api/user'
import type { SysUser } from '@/models/entity/SysUser'
import UserSearchForm from '@/components/user/UserSearchForm.vue'
import UserTable from '@/components/user/UserTable.vue'
import UserFormDialog from '@/components/user/UserFormDialog.vue'
import UserDetailDialog from '@/components/user/UserDetailDialog.vue'
import type { UserFormData } from '@/components/user/UserFormDialog.vue'
import type { UserSearchFormData } from '@/components/user/UserSearchForm.vue'
import ExportUsersDialog from "@/components/ExportUsersDialog.vue";
import type {UserSearchRequest} from "@/models/request/user/UserSearchRequestV2.ts";
import * as userExportService from "@/api/userExport"

const loading = ref(false)
const submitting = ref(false)
const userList = ref<SysUser[]>([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)
const showExportDialog = ref(false);

// 搜索表单
const searchForm = ref<UserSearchRequest>({
  username: undefined,
  phone: undefined,
  email: undefined,
  status: undefined,
  roles: undefined,
  lastLoginAt: undefined,
  lastLoginIp: undefined,
  passwordResetAt: undefined,
  deleted: undefined,
  version: undefined,
  createdAt: undefined,
  createTimeStart: undefined,
  createTimeEnd: undefined,
  updatedAt: undefined,
  updateTimeStart: undefined,
  updateTimeEnd: undefined,
  createdBy: undefined,
  updatedBy: undefined,
});

const searchFormData = reactive<UserSearchFormData>({
  username: '',
  phone: '',
  email: '',
  status: ''
})

const dialogVisible = ref(false)
const detailDialogVisible = ref(false)
const dialogType = ref<'add' | 'edit'>('add')

const userForm = reactive<UserFormData>({
  username: '',
  phone: '',
  email: '',
  realName: '',
  gender: '',
  birthday: '',
  avatar: '',
  password: '',
  roles: []
})

const currentUser = ref<SysUser | null>(null)

const loadUsers = async () => {
  loading.value = true
  try {
    // 如果有搜索条件，使用搜索接口
    const hasSearch = Object.values(searchFormData).some(v => v)
    const res = hasSearch
      ? await userApi.searchUsers(searchFormData, pageNum.value, pageSize.value)
      : await userApi.getUserList(pageNum.value, pageSize.value)

    if (res.code === 200) {
      userList.value = res.data.rows
      total.value = res.data.total
    } else {
      ElMessage.error(res.message || '加载用户列表失败')
    }
  } catch (error) {
    ElMessage.error('加载用户列表失败')
    console.error('Load users error:', error)
  } finally {
    loading.value = false
  }
}

const handleSearch = (data: UserSearchFormData) => {
  Object.assign(searchFormData, data)
  pageNum.value = 1
  loadUsers()
}

const handleReset = () => {
  Object.assign(searchFormData, {
    username: '',
    phone: '',
    email: '',
    status: ''
  })
  pageNum.value = 1
  loadUsers()
}

const handleSizeChange = (val: number) => {
  pageSize.value = val
  loadUsers()
}

const handleCurrentChange = (val: number) => {
  pageNum.value = val
  loadUsers()
}

const handleAddUser = () => {
  dialogType.value = 'add'
  Object.assign(userForm, {
    username: '',
    phone: '',
    email: '',
    password: '',
    roles: ['ROLE_USER']
  })
  dialogVisible.value = true
}

const handleEdit = (user: SysUser) => {
  dialogType.value = 'edit'
  currentUser.value = user
  
  // 确保用户名有值，使用当前用户的用户名作为默认值
  userForm.username = user.username || ''
  userForm.phone = user.phone || ''
  userForm.email = user.email || ''
  userForm.roles = user.roles ? user.roles.split(',') : ['ROLE_USER']
  
  dialogVisible.value = true
}

const handleView = async (user: SysUser) => {
  try {
    const res = await userApi.getUserProfileByUserId(user.id)
    if (res.code === 200) {
      currentUser.value = user
      detailDialogVisible.value = true
    }
  } catch (error) {
    ElMessage.error('获取用户详情失败')
  }
}

const handleResetPassword = async (user: SysUser) => {
  try {
    const { value } = await ElMessageBox.prompt('请输入新密码', '重置密码', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      inputPattern: /^.{6,}$/,
      inputErrorMessage: '密码至少6位'
    })

    const res = await userApi.resetPassword(user.id, value)
    if (res.code === 200) {
      ElMessage.success('密码重置成功')
      loadUsers()
    } else {
      ElMessage.error(res.message || '密码重置失败')
    }
  } catch {
    // 取消操作
  }
}

const handleBlockUser = async (user: SysUser) => {
  try {
    await ElMessageBox.confirm(`确定要封禁用户 "${user.username}" 吗？`, '警告', {
      type: 'warning'
    })

    const res = await userApi.blockUser(user.id)
    if (res.code === 200) {
      ElMessage.success('用户已封禁')
      loadUsers()
    } else {
      ElMessage.error(res.message || '封禁失败')
    }
  } catch {
    // 取消操作
  }
}

const handleUnblockUser = async (user: SysUser) => {
  try {
    await ElMessageBox.confirm(`确定要解封用户 "${user.username}" 吗？`, '提示', {
      type: 'info'
    })

    const res = await userApi.unblockUser(user.id)
    if (res.code === 200) {
      ElMessage.success('用户已解封')
      loadUsers()
    } else {
      ElMessage.error(res.message || '解封失败')
    }
  } catch {
    // 取消操作
  }
}

const handleDeleteUser = async (user: SysUser) => {
  try {
    await ElMessageBox.confirm(`确定要删除用户 "${user.username}" 吗？此操作不可恢复！`, '危险操作', {
      type: 'error'
    })

    const res = await userApi.deleteUser(user.id)
    if (res.code === 200) {
      ElMessage.success('用户已删除')
      loadUsers()
    } else {
      ElMessage.error(res.message || '删除失败')
    }
  } catch {
    // 取消操作
  }
}

const handleSubmit = async (formData: UserFormData) => {
  submitting.value = true
  try {
    if (dialogType.value === 'add') {
      // 新增用户逻辑（如果需要）
      ElMessage.warning('新增用户功能待实现')
    } else {
      // 编辑用户
      if (!currentUser.value) {
        ElMessage.error('用户信息不存在')
        return
      }
      const res = await userApi.updateUser(currentUser.value.id, formData)
      if (res.code === 200) {
        ElMessage.success('更新成功')
        dialogVisible.value = false
        loadUsers()
      } else {
        ElMessage.error(res.message || '更新失败')
      }
    }
  } catch (error) {
    ElMessage.error('更新失败')
    console.error('Update user error:', error)
  } finally {
    submitting.value = false
  }
}

const handleDialogClose = () => {
  currentUser.value = null
  // 重置表单数据，确保下次打开时是干净的状态
  userForm.username = ''
  userForm.phone = ''
  userForm.email = ''
  userForm.password = ''
  userForm.roles = []
}

/* ----------------- 导出功能 ----------------- */
const handleExportAllUsers = async () => {
  try {
    const result = await ElMessageBox.prompt('请输入导出文件名:', '导出所有用户', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      inputValue: '所有用户数据',
      inputPattern: /.+/,
      inputErrorMessage: '文件名不能为空',
    });
    const fileName = result.value || '所有用户数据';
    await userExportService.exportAllUsers(fileName, searchForm.value.status);
    ElMessage.success('所有用户数据已开始导出！');
  } catch (error: any) {
    if (error !== 'cancel') {
      console.error('导出所有用户失败:', error);
      ElMessage.error('导出失败，请重试！');
    }
  }
};

const handleExported = () => {
  console.log('用户数据导出操作完成。');
};

onMounted(() => {
  loadUsers()
})
</script>

<style scoped>
.user-management {
  padding: 20px;
}

.header-card {
  margin-bottom: 20px;
  border-radius: 12px;
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.title-section h2 {
  margin: 0 0 8px 0;
  font-size: 24px;
  color: #303133;
}

.subtitle {
  margin: 0;
  font-size: 14px;
  color: #909399;
}
</style>
