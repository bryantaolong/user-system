<template>
  <el-dialog
    v-model="visible"
    :title="dialogTitle"
    width="600px"
    @close="handleClose"
  >
    <el-form
      ref="userFormRef"
      :model="localUserForm"
      :rules="userRules"
      label-width="100px"
      class="user-form"
    >
      <el-form-item label="用户名" prop="username">
        <el-input v-model="localUserForm.username" />
      </el-form-item>
      <el-form-item label="手机号" prop="phone">
        <el-input v-model="localUserForm.phone" />
      </el-form-item>
      <el-form-item label="邮箱" prop="email">
        <el-input v-model="localUserForm.email" />
      </el-form-item>
      <el-form-item v-if="dialogType === 'add'" label="密码" prop="password">
        <el-input v-model="localUserForm.password" type="password" show-password />
      </el-form-item>
      <el-form-item label="角色" prop="roles">
        <el-select v-model="localUserForm.roles" multiple placeholder="请选择角色">
          <el-option label="普通用户" value="ROLE_USER" />
          <el-option label="管理员" value="ROLE_ADMIN" />
        </el-select>
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="handleCancel">取消</el-button>
      <el-button type="primary" :loading="submitting" @click="handleSubmit">
        确定
      </el-button>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, computed, watch, nextTick } from 'vue'
import type { FormInstance, FormRules } from 'element-plus'
import type { UserUpdateRequest } from '@/models/request/user/UserUpdateRequest.ts'

export interface UserFormData extends UserUpdateRequest {
  password?: string
  roles?: string[]
}

const props = defineProps<{
  modelValue: boolean
  dialogType: 'add' | 'edit'
  userForm: UserFormData
  submitting?: boolean
}>()

const emit = defineEmits<{
  'update:modelValue': [value: boolean]
  submit: [formData: UserFormData]
  close: []
  'update:userForm': [formData: UserFormData]
}>()

const visible = computed({
  get: () => props.modelValue,
  set: (value) => emit('update:modelValue', value)
})

const dialogTitle = computed(() =>
  props.dialogType === 'add' ? '新增用户' : '编辑用户'
)

const userFormRef = ref<FormInstance>()

// ✅ 初始化 localUserForm，确保所有字段有默认值
const localUserForm = ref<UserFormData>({
  username: props.userForm.username || '',
  phone: props.userForm.phone || '',
  email: props.userForm.email || '',
  roles: props.userForm.roles ? [...props.userForm.roles] : [],
  password: props.userForm.password || ''
})

// ✅ 仅监听 visible 变化，在打开时同步最新 props 数据
watch(visible, async (isVisible) => {
  if (isVisible) {
    // 同步最新数据，同时防止 undefined 覆盖
    localUserForm.value = {
      username: props.userForm.username || '',
      phone: props.userForm.phone || '',
      email: props.userForm.email || '',
      roles: props.userForm.roles ? [...props.userForm.roles] : [],
      password: props.userForm.password || ''
    }

    await nextTick()
    userFormRef.value?.clearValidate()
  }
}, { immediate: true })

// 动态验证规则
const userRules = computed<FormRules>(() => {
  const rules: FormRules = {
    username: [
      { required: true, message: '请输入用户名', trigger: 'blur' },
      { min: 2, max: 20, message: '用户名长度应在2-20个字符之间', trigger: 'blur' }
    ],
    phone: [{ pattern: /^1[3-9]\d{9}$/, message: '电话号码格式不正确', trigger: 'blur' }],
    email: [{ type: 'email', message: '邮箱格式不正确', trigger: 'blur' }]
  }

  if (props.dialogType === 'add') {
    rules.password = [
      { required: true, message: '请输入密码', trigger: 'blur' },
      { min: 6, message: '密码至少6位', trigger: 'blur' }
    ]
  }

  return rules
})

const handleSubmit = async () => {
  if (!userFormRef.value) return

  userFormRef.value.clearValidate()

  // ✅ 不再从 props 补值，因为 localUserForm 已可靠
  await nextTick()

  await userFormRef.value.validate(async (valid: boolean) => {
    if (!valid) return
    emit('submit', { ...localUserForm.value })
  })
}

const handleCancel = () => {
  visible.value = false
}

const handleClose = () => {
  userFormRef.value?.resetFields()
  userFormRef.value?.clearValidate()
  // 重置为当前 props 值（安全）
  localUserForm.value = {
    username: props.userForm.username || '',
    phone: props.userForm.phone || '',
    email: props.userForm.email || '',
    roles: props.userForm.roles ? [...props.userForm.roles] : [],
    password: props.userForm.password || ''
  }
  emit('close')
}

defineExpose({
  userFormRef
})
</script>

<style scoped>
.user-form {
  padding: 20px 20px 0 20px;
}
</style>