import request from '@/utils/request'
import type { Result } from '@/models/response/Result'
import type { PageResult } from '@/models/response/PageResult'
import type { SysUser } from '@/models/entity/user/SysUser.ts'
import type { UserCreateRequest } from '@/models/request/user/UserCreateRequest'
import type { UserUpdateRequest } from '@/models/request/user/UserUpdateRequest.ts'
import type { ChangePasswordRequest } from '@/models/request/user/ChangePasswordRequest.ts'
import type { UserSearchRequest } from '@/models/request/user/UserSearchRequest.ts'

/**
 * 用户管理API
 */
export const userApi = {
  /**
   * 创建用户（管理员）
   */
  createUser(data: UserCreateRequest): Promise<Result<SysUser>> {
    return request.post('/api/users', data)
  },

  /**
   * 获取用户列表（分页）
   */
  getUserList(pageNum = 1, pageSize = 10): Promise<Result<PageResult<SysUser>>> {
    return request.get('/api/users', {
      params: { pageNum, pageSize }
    })
  },

  /**
   * 根据ID获取用户信息
   */
  getUserById(userId: number): Promise<Result<SysUser>> {
    return request.get(`/api/users/${userId}`)
  },

  /**
   * 根据用户名获取用户信息
   */
  getUserByUsername(username: string): Promise<Result<SysUser>> {
    return request.get(`/api/users/username/${username}`)
  },

  /**
   * 搜索用户
   */
  searchUsers(data: UserSearchRequest, pageNum = 1, pageSize = 10): Promise<Result<PageResult<SysUser>>> {
    return request.post('/api/users/search', data, {
      params: { pageNum, pageSize }
    })
  },

  /**
   * 更新用户信息
   */
  updateUser(userId: number, data: UserUpdateRequest): Promise<Result<SysUser>> {
    return request.put(`/api/users/${userId}`, data)
  },

  /**
   * 修改用户角色（管理员）
   */
  changeUserRoles(userId: number, roleIds: number[]): Promise<Result<SysUser>> {
    return request.put(`/api/users/roles/${userId}`, { roleIds })
  },

  /**
   * 重置用户密码（管理员）
   */
  resetPassword(userId: number, newPassword: string): Promise<Result<SysUser>> {
    const data: ChangePasswordRequest = { newPassword }
    return request.put(`/api/users/password/${userId}`, data)
  },

  /**
   * 封禁用户
   */
  blockUser(userId: number): Promise<Result<SysUser>> {
    return request.put(`/api/users/block/${userId}`)
  },

  /**
   * 解封用户
   */
  unblockUser(userId: number): Promise<Result<SysUser>> {
    return request.put(`/api/users/unblock/${userId}`)
  },

  /**
   * 删除用户（逻辑删除）
   */
  deleteUser(userId: number): Promise<Result<number>> {
    return request.delete(`/api/users/${userId}`)
  }
}

