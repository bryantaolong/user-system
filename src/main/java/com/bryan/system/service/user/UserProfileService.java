package com.bryan.system.service.user;

import com.bryan.system.domain.dto.user.UserProfileUpdateDTO;
import com.bryan.system.domain.entity.user.UserProfile;
import com.bryan.system.exception.BusinessException;
import com.bryan.system.exception.ResourceNotFoundException;
import com.bryan.system.mapper.user.UserProfileMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 用户资料业务服务
 * 提供用户资料的创建、查询、更新能力。
 *
 * @author Bryan Long
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserProfileService {

    private final UserProfileMapper userProfileMapper;

    /**
     * 创建用户资料
     *
     * @param record 用户资料实体
     * @return 已持久化的实体
     * @throws BusinessException 数据库插入失败
     */
    public UserProfile save(UserProfile record) {
        int inserted = userProfileMapper.insert(record);
        if (inserted <= 0) {
            throw new BusinessException("创建用户信息失败");
        }
        log.info("用户信息创建成功，用户ID: {}", record.getUserId());
        return record;
    }

    /**
     * 根据用户主键查询用户资料
     *
     * @param userId 用户主键
     * @return 用户资料实体
     * @throws ResourceNotFoundException 用户资料不存在
     */
    public UserProfile getUserProfileByUserId(Long userId) {
        UserProfile profile = userProfileMapper.selectByUserId(userId);
        if (profile == null) {
            throw new ResourceNotFoundException("用户信息不存在");
        }
        return profile;
    }

    /**
     * 根据真实姓名查询用户资料
     *
     * @param realName 真实姓名
     * @return 用户资料实体
     * @throws ResourceNotFoundException 用户资料不存在
     */
    public UserProfile findUserProfileByRealName(String realName) {
        UserProfile profile = userProfileMapper.selectByRealName(realName);
        if (profile == null) {
            throw new ResourceNotFoundException("用户信息不存在");
        }
        return profile;
    }

    /**
     * 更新用户资料（字段可选）
     *
     * @param userId 用户主键
     * @param dto    待更新字段封装
     * @return 更新后的用户资料实体
     * @throws BusinessException 更新失败
     */
    public UserProfile updateUserProfile(Long userId, UserProfileUpdateDTO dto) {
        UserProfile profile = this.getUserProfileByUserId(userId);

        if (dto.getRealName() != null) {
            profile.setRealName(dto.getRealName());
        }
        if (dto.getGender() != null) {
            profile.setGender(dto.getGender());
        }
        if (dto.getBirthday() != null) {
            profile.setBirthday(dto.getBirthday());
        }
        if (dto.getAvatar() != null) {
            profile.setAvatar(dto.getAvatar());
        }

        int updated = userProfileMapper.update(profile);
        if (updated == 0) {
            throw new BusinessException("用户信息更新失败");
        }
        log.info("用户信息更新成功，用户ID: {}", userId);
        return profile;
    }
}