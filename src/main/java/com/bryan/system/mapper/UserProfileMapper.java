package com.bryan.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bryan.system.domain.entity.UserProfile;
import org.apache.ibatis.annotations.Mapper;

/**
 * UserProfileMapper
 *
 * @author Bryan Long
 */
@Mapper
public interface UserProfileMapper extends BaseMapper<UserProfile> {
}
