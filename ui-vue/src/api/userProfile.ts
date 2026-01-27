import request from '@/utils/request'
import type { Result } from '@/models/response/Result'
import type { UserUpdateRequest } from '@/models/request/user/UserUpdateRequest'
import type { UserProfileVO } from '@/models/vo/UserProfileVO'

/**
 * 用户资料 API
 */
export const userProfileApi = {
  /**
   * 根据用户ID获取用户详情
   */
  getUserProfileByUserId(userId: number): Promise<Result<UserProfileVO>> {
    return request.get(`/api/user-profiles/${userId}`)
  },

  /**
   * 根据真实姓名获取用户详情
   */
  getUserProfileByRealName(realName: string): Promise<Result<UserProfileVO>> {
    return request.get(`/api/user-profiles/name/${realName}`)
  },

  /**
   * 更新当前用户详情
   */
  updateUserProfile(data: UserUpdateRequest): Promise<Result<UserProfileVO>> {
    return request.put('/api/user-profiles', data)
  },

  /**
   * 上传当前用户头像
   */
  uploadAvatar(file: File): Promise<Result<string>> {
    const formData = new FormData()
    formData.append('file', file)
    return request.post('/api/user-profiles/avatar', formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    })
  }
}
