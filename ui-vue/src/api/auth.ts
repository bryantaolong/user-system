import request from '@/utils/request'
import type { Result } from '@/models/response/Result'
import type { UserVO } from '@/models/vo//UserVO.ts'
import type { SysUser } from '@/models/entity/SysUser.ts'
import type { LoginRequest } from '@/models/request/auth/LoginRequest.ts'
import type { RegisterRequest } from '@/models/request/auth/RegisterRequest.ts'
import type { ChangePasswordRequest } from '@/models/request/user/ChangePasswordRequest.ts'
import type { UserProfileVO } from '@/models/vo/UserProfileVO.ts'

/**
 * 用户认证API
 */
export const authApi = {
  /**
   * 用户注册
   */
  register(data: RegisterRequest): Promise<Result<SysUser>> {
    return request.post('/api/auth/register', data)
  },

  /**
   * 用户登录
   */
  login(data: LoginRequest): Promise<Result<string>> {
    return request.post('/api/auth/login', data)
  },

  /**
   * 获取当前用户信息
   */
  getCurrentUser(): Promise<Result<UserVO>> {
    return request.get('/api/auth/me')
  },

  /**
   * 退出登录
   */
  logout(): Promise<boolean> {
    return request.get('/api/auth/logout')
  },

  /**
   * 修改密码
   */
  changePassword(data: ChangePasswordRequest): Promise<Result<SysUser>> {
    return request.put('/api/auth/password', data)
  },

  /**
   * 注销账号
   */
  deleteAccount(): Promise<Result<SysUser>> {
    return request.delete('/api/auth')
  },

  /**
   * 验证Token
   */
  validateToken(token: string): Promise<Result<string>> {
    return request.get('/api/auth/validate', { params: { token } })
  },

  /**
   * 获取当前用户详情
   */
  getCurrentUserProfile(): Promise<Result<UserProfileVO>> {
    return request.get('/api/user-profiles/me')
  }
}
