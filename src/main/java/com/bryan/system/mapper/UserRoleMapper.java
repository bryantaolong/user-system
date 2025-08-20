package com.bryan.system.mapper;

import com.bryan.system.domain.entity.SysUserRole;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Collection;
import java.util.List;

/**
 * UserRoleMapper
 *
 * @author Bryan Long
 */
@Mapper
public interface UserRoleMapper {

    SysUserRole selectOneByIsDefaultTrue();

    List<SysUserRole> selectAll();

    List<SysUserRole> selectByIdList(@Param("ids") Collection<Long> ids);
}
