import request from '@/utils/request'
import type { Result } from '@/models/response/Result'
import type { UserRoleOptionVO } from '@/models/response/user/UserRoleOptionVO'

/**
 * 用户角色管理API
 */
export const userRoleApi = {
  /**
   * 获取全部角色下拉选项
   */
  listRoles(): Promise<Result<UserRoleOptionVO[]>> {
    return request.get('/api/user-roles')
  }
}
