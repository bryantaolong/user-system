package com.bryan.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bryan.system.domain.entity.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * UserMapper
 *
 * @author Bryan Long
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
}
