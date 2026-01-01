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
 * UserProfileService
 *
 * @author Bryan Long
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserProfileService {

    private final UserProfileMapper userProfileMapper;

    public UserProfile save(UserProfile record) {
        int inserted = userProfileMapper.insert(record);

        if (inserted < 0) {
            throw new BusinessException("创建用户信息失败");
        }
        return record;
    }

    public UserProfile findUserProfileByUserId(Long userId) {
        UserProfile profile = userProfileMapper.selectByUserId(userId);

        if (profile == null) {
            throw new ResourceNotFoundException("用户信息不存在");
        }
        return profile;
    }

    public UserProfile findUserProfileByRealName(String realName) {
        UserProfile profile = userProfileMapper.selectByRealName(realName);

        if (profile == null) {
            throw new ResourceNotFoundException("用户信息不存在");
        }
        return profile;
    }

    public UserProfile updateUserProfile(Long userId, UserProfileUpdateDTO dto) {
        UserProfile profile = this.findUserProfileByUserId(userId);

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
        return profile;
    }
}
