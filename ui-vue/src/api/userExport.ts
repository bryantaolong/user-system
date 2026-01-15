// src/api/userExport.ts
import request from '@/utils/request';
import type { UserExportRequest } from '@/models/request/user/UserExportRequest';
import type {Result} from "@/models/response/Result.ts";

/**
 * 导出所有用户数据（包含所有字段）
 * @param fileName 导出文件名
 * @param status 状态过滤（可选）
 */
export function exportAllUsers(fileName?: string, status?: number | null): Promise<void> {
    return request({
        url: '/api/user/export/all',
        method: 'get',
        params: {
            fileName: fileName || '用户数据',
            status: status ?? undefined
        },
        responseType: 'blob'
    }).then(response => {
        const filename = fileName ? `${fileName}.xlsx` : '用户数据.xlsx';
        const url = window.URL.createObjectURL(new Blob([response.data]));
        const link = document.createElement('a');
        link.href = url;
        link.setAttribute('download', filename);
        document.body.appendChild(link);
        link.click();
        document.body.removeChild(link);
        window.URL.revokeObjectURL(url);
    }) as Promise<void>;
}

/**
 * 导出用户数据（支持字段选择）
 * @param data 用户导出请求数据
 */
export function exportUsersByFields(data: UserExportRequest): Promise<void> {
    return request({
        url: '/api/user/export/fields', // ✅ 对应 @PostMapping("/field")
        method: 'post',
        data,
        responseType: 'blob'
    }).then(response => {
        const filename = data.fileName ? `${data.fileName}.xlsx` : '用户数据.xlsx';
        const url = window.URL.createObjectURL(new Blob([response.data]));
        const link = document.createElement('a');
        link.href = url;
        link.setAttribute('download', filename);
        document.body.appendChild(link);
        link.click();
        document.body.removeChild(link);
        window.URL.revokeObjectURL(url);
    }) as Promise<void>;
}

/**
 * 获取可导出的字段列表（字段名 => 中文名）
 * @returns 例如：{ "username": "用户名", "email": "邮箱", ... }
 */
export function getExportFields(): Promise<Result<Record<string, string>>> {
    return request({
        url: '/api/user/export/fields',
        method: 'get'
    });
}

