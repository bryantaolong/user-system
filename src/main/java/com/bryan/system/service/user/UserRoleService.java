package com.bryan.system.service.user;

import com.bryan.system.domain.entity.user.UserRole;
import com.bryan.system.mapper.user.UserRoleMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

/**
 * UserRoleService
 *
 * @author Bryan Long
 */
@Service
@RequiredArgsConstructor
public class UserRoleService {

    private final UserRoleMapper userRoleMapper;

    public List<UserRole> listAll() {
        return userRoleMapper.selectAll();
    }

    public List<UserRole> findByIds(Collection<Long> ids) {
        return userRoleMapper.selectByIdList(ids);
    }
}