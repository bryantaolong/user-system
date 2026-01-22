<!-- src/components/user/ExportUsersDialog.vue -->
<template>
  <el-dialog
    v-model="dialogVisible"
    title="选择导出字段"
    width="500px"
    :before-close="handleClose"
    destroy-on-close
  >
    <el-form label-width="100px">
      <el-form-item label="导出文件名">
        <el-input v-model="exportFileName" placeholder="默认为用户数据"></el-input>
      </el-form-item>
      <el-form-item label="状态筛选">
        <el-select v-model="exportStatusFilter" placeholder="选择状态" clearable>
          <el-option label="正常" :value="0"></el-option>
          <el-option label="封禁" :value="1"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="选择字段">
        <el-checkbox-group v-model="selectedExportFields">
          <el-checkbox v-for="(label, key) in exportFieldsOptions" :key="key" :label="key">
            {{ label }}
          </el-checkbox>
        </el-checkbox-group>
      </el-form-item>
    </el-form>
    <template #footer>
      <span class="dialog-footer">
        <el-button @click="handleClose">取消</el-button>
        <el-button type="primary" @click="confirmExport">导出</el-button>
      </span>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, watch, onMounted } from 'vue';
import { ElMessage } from 'element-plus';
import * as userExportService from '@/api/userExport.ts';
import type { UserExportRequest } from '@/models/request/user/UserExportRequest.ts';

// 定义组件的 props
const props = defineProps<{
  visible: boolean;
}>();

// 定义组件的 emits
const emit = defineEmits(['update:visible', 'exported']);

const dialogVisible = ref(props.visible);
const exportFileName = ref('用户数据');
const exportStatusFilter = ref<number | null>(null);
const selectedExportFields = ref<string[]>([]); // 存储用户选择的导出字段
const exportFieldsOptions = ref<Map<string, string>>(new Map()); // 存储后端返回的可导出字段及中文名

// 监听 props.visible 的变化来控制 dialogVisible
watch(() => props.visible, (newVal) => {
  dialogVisible.value = newVal;
  if (newVal) {
    // 弹窗打开时获取可选字段并默认全选
    fetchExportFields();
  }
});

// 处理弹窗关闭
const handleClose = () => {
  emit('update:visible', false); // 通知父组件关闭弹窗
};

// 获取可导出字段列表
const fetchExportFields = async () => {
  try {
    const res = await userExportService.getExportFields();
    if (res.code === 200 && res.data) {
      exportFieldsOptions.value = new Map(Object.entries(res.data)); // 将对象转换为Map
      selectedExportFields.value = Array.from(exportFieldsOptions.value.keys()); // 默认全选
    } else {
      ElMessage.error(res?.message || '获取导出字段失败！');
    }
  } catch (error: any) {
    console.error('获取导出字段失败:', error);
    ElMessage.error('网络错误，无法获取导出字段！');
  }
};

// 确认选择字段导出
const confirmExport = async () => {
  if (selectedExportFields.value.length === 0) {
    ElMessage.warning('请至少选择一个导出字段！');
    return;
  }
  try {
    const exportRequest: UserExportRequest = {
      fields: selectedExportFields.value,
      fileName: exportFileName.value || '用户数据',
      status: exportStatusFilter.value ?? undefined
    };
    await userExportService.exportUsersByFields(exportRequest); // ✅ 使用选择字段导出的接口
    ElMessage.success('用户数据已开始导出！');
    emit('exported');
    handleClose();
  } catch (error: any) {
    console.error('选择字段导出失败:', error);
    ElMessage.error('导出失败，请重试！');
  }
};


onMounted(() => {
  // 可以在这里预加载字段，如果弹窗不是每次都打开
  // fetchExportFields();
});
</script>

<style scoped>
/* Add component-specific styles here if needed */
</style>
