package com.bryan.system.mapper;

import com.bryan.system.domain.entity.SysUser;
import com.bryan.system.domain.enums.UserStatusEnum;
import com.bryan.system.domain.request.UserExportRequest;
import com.bryan.system.domain.request.UserSearchRequest;
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

    List<SysUser> selectPage(@Param("offset") int offset,
                             @Param("pageSize") int pageSize,
                             @Param("req") UserSearchRequest search,
                             @Param("export") UserExportRequest export);

    SysUser selectByStatus(@Param("status") UserStatusEnum status);

    int update(SysUser user);

    long count(@Param("req") UserSearchRequest search,
               @Param("export") UserExportRequest export);
}
