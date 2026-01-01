export type Gender = 0 | 1; // 0: female, 1: male

export interface UserProfile {
  userId: number;
  realName?: string;
  gender?: Gender;
  birthday?: string;
  avatar?: string;
  deleted: number;
  version: number;
  createdAt: string;
  updatedAt: string;
  createdBy: string;
  updatedBy: string;
}
