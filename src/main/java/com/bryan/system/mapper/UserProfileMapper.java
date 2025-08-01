package com.bryan.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bryan.system.model.entity.UserProfile;
import org.apache.ibatis.annotations.Mapper;

/**
 * UserProfileMapper
 *
 * @author Bryan Long
 * @version 1.0
 * @since 2025/8/1
 */
@Mapper
public interface UserProfileMapper extends BaseMapper<UserProfile> {
}
