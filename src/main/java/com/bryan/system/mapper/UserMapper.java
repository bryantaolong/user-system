package com.bryan.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bryan.system.domain.entity.User;
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
public interface UserMapper extends BaseMapper<User> {

//    int insert(User user);

//    User selectById(Long id);

    User selectByUsername(String username);

    List<User> selectPage(@Param("offset") long offset,
                          @Param("pageSize") long pageSize,
                          @Param("req") UserSearchRequest search,
                          @Param("export") UserExportRequest export);

    long count(@Param("req") UserSearchRequest search,
               @Param("export") UserExportRequest export);
}
