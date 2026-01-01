import type { Gender } from '../entity/UserProfile';

export interface UserUpdateRequest {
  username?: string;
  phone?: string;
  email?: string;
  realName?: string;
  gender?: Gender;
  birthday?: string;
  avatar?: string;
}
