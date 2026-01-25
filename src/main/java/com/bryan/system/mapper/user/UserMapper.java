package com.bryan.system.mapper.user;

import com.bryan.system.domain.entity.user.SysUser;
import com.bryan.system.domain.enums.user.UserStatusEnum;
import com.bryan.system.domain.request.user.UserExportRequest;
import com.bryan.system.domain.request.user.UserSearchRequest;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Collection;
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
                             @Param("req") UserSearchRequest search);

    List<SysUser> selectExportPage(@Param("offset") int offset,
                                   @Param("pageSize") int pageSize,
                                   @Param("export") UserExportRequest export);

    List<SysUser> selectByIdList(@Param("ids") Collection<Long> ids);

    SysUser selectByStatus(@Param("status") UserStatusEnum status);

    int update(SysUser user);

    int deleteById(@Param("id") Long id);

    long count(@Param("req") UserSearchRequest search);
}
