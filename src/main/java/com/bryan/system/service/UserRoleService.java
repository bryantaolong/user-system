package com.bryan.system.service;

import com.bryan.system.domain.dto.UserRoleOptionDTO;
import com.bryan.system.domain.entity.UserRole;
import com.bryan.system.mapper.UserRoleMapper;
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

    public List<UserRoleOptionDTO> listAll() {
        return userRoleMapper.selectAll()
                .stream()
                .map(r -> new UserRoleOptionDTO(r.getId(), r.getRoleName()))
                .toList();
    }

    public List<UserRole> findByIds(Collection<Long> ids) {
        return userRoleMapper.selectByIdList(ids);
    }
}