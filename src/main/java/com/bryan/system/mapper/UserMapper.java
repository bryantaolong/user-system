package com.bryan.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bryan.system.model.entity.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * UserMapper
 *
 * @author Bryan Long
 * @version 1.0
 * @since 2025/6/19 - 19:49
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
}
