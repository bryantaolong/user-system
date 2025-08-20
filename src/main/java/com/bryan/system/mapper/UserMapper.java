package com.bryan.system.mapper;

import com.bryan.system.domain.entity.SysUser;
import com.bryan.system.domain.enums.SysUserStatusEnum;
import com.bryan.system.domain.request.SysUserExportRequest;
import com.bryan.system.domain.request.SysUserSearchRequest;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * UserMapper
 *
 * @author Bryan Long
 */
@Mapper
public interface UserMapper {

    int insert(SysUser user);

    SysUser selectById(Long id);

    SysUser selectByUsername(String username);

    List<SysUser> selectPage(@Param("offset") long offset,
                             @Param("pageSize") long pageSize,
                             @Param("req") SysUserSearchRequest search,
                             @Param("export") SysUserExportRequest export);

    SysUser selectByStatus(@Param("status") SysUserStatusEnum status);

    int update(SysUser user);

    long count(@Param("req") SysUserSearchRequest search,
               @Param("export") SysUserExportRequest export);
}
