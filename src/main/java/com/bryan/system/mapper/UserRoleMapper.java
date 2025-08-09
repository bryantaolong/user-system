package com.bryan.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bryan.system.domain.entity.UserRole;
import org.apache.ibatis.annotations.Mapper;

/**
 * UserRoleMapper
 *
 * @author Bryan Long
 */
@Mapper
public interface UserRoleMapper extends BaseMapper<UserRole> {
}
