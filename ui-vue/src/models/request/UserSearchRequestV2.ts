/**
 * @module src/models/request/user/UserSearchRequest.ts
 * @description 定义搜索用户请求的数据结构。
 */
export interface UserSearchRequest {
    username?: string;
    phone?: string;
    email?: string;
    status?: number;
    roles?: string;
    lastLoginAt?: string;
    lastLoginIp?: string;
    passwordResetAt?: string;        // 密码重置时间
    loginFailCount?: number;         // 登录失败次数
    lockedAt?: string;               // 账号锁定时间
    deleted?: number;                // 逻辑删除标志
    version?: number;                // 乐观锁版本号
    createdAt?: string;
    createTimeStart?: string;
    createTimeEnd?: string;
    updatedAt?: string;
    updateTimeStart?: string;
    updateTimeEnd?: string;
    createdBy?: string;
    updatedBy?: string;
}