package com.bryan.system.service.user;

import com.bryan.system.domain.dto.user.UserUpdateDTO;
import com.bryan.system.domain.entity.user.SysUser;
import com.bryan.system.domain.entity.user.UserRole;
import com.bryan.system.domain.enums.user.UserStatusEnum;
import com.bryan.system.domain.request.user.ChangeRoleRequest;
import com.bryan.system.domain.response.PageResult;
import com.bryan.system.exception.BusinessException;
import com.bryan.system.exception.ResourceNotFoundException;
import com.bryan.system.domain.request.user.UserSearchRequest;
import com.bryan.system.mapper.user.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 用户服务实现类，处理用户注册、登录、信息管理、导出等业务逻辑。
 *
 * @author Bryan Long
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final UserRoleService userRoleService;

    /**
     * 获取所有用户列表（不分页）。
     *
     * @return 包含所有用户的分页对象（Page）。
     */
    public PageResult<SysUser> findAllUsers(int pageNum,
                                           int pageSize) {
        int offset = (pageNum - 1) * pageSize;
        List<SysUser> rows = userMapper.selectPage(
                offset,
                pageSize,
                null,
                null);
        long total = userMapper.count(null, null);
        return PageResult.of(rows, pageNum, pageSize, total);
    }

    /**
     * 根据用户ID获取用户信息。
     *
     * @param userId 用户 ID
     * @return 用户实体对象
     * @throws ResourceNotFoundException 用户不存在时抛出
     */
    public SysUser findUserById(Long userId) {
        return Optional.ofNullable(userMapper.selectById(userId))
                .orElseThrow(() -> new ResourceNotFoundException("用户不存在"));
    }

    /**
     * 根据用户名获取用户信息。
     *
     * @param username 用户名
     * @return 用户实体对象
     */
    public SysUser findUserByUsername(String username) {
        return userMapper.selectByUsername(username);
    }

    /**
     * 通用用户搜索，支持多条件模糊查询和分页。
     *
     * @param searchRequest 搜索请求
     * @return 符合查询条件的分页对象（Page）
     */
    public PageResult<SysUser> searchUsers(UserSearchRequest searchRequest,
                                           int pageNum,
                                           int pageSize) {
        int offset = (pageNum - 1) * pageSize;
        List<SysUser> rows = userMapper.selectPage(
                offset,
                pageSize,
                searchRequest,
                null);
        long total = userMapper.count(searchRequest, null);
        return PageResult.of(rows, pageNum, pageSize, total);
    }

    public SysUser save(SysUser sysUser) {
        if (sysUser.getId() == null) {
            userMapper.insert(sysUser);
        } else {
            userMapper.update(sysUser);
        }
        return sysUser;
    }

    /**
     * 更新用户基础信息（用户名和邮箱）。
     *
     * @param userId                     用户 ID
     * @param dto                        用户更新 DTO
     * @return                           更新后的用户对象
     * @throws ResourceNotFoundException 用户不存在时抛出
     * @throws BusinessException         用户名重复时抛出
     */
    public SysUser updateUser(Long userId, UserUpdateDTO dto) {
        SysUser user = findUserById(userId);

        // 用户名不能重复
        if (dto.getUsername() != null &&
                !dto.getUsername().equals(user.getUsername())) {
            SysUser sameName = userMapper.selectByUsername(dto.getUsername());
            if (sameName != null && !sameName.getId().equals(userId)) {
                throw new BusinessException("用户名已存在");
            }
            user.setUsername(dto.getUsername());
        }
        if (dto.getPhone() != null) user.setPhone(dto.getPhone());
        if (dto.getEmail() != null) user.setEmail(dto.getEmail());

        userMapper.update(user);
        log.info("用户ID: {} 的信息更新成功", userId);
        return user;
    }

    /**
     * 修改用户角色。
     *
     * @param userId 用户 ID
     * @param req  新角色字符串（多个角色用逗号分隔）
     * @return 更新后的用户对象
     * @throws ResourceNotFoundException 用户不存在时抛出
     */
    @Transactional
    public SysUser changeRoleByIds(Long userId, ChangeRoleRequest req) {
        List<Long> ids = req.getRoleIds();
        List<UserRole> roles = userRoleService.findByIds(ids);

        if (roles.size() != ids.size()) {
            Set<Long> exist = roles.stream()
                    .map(UserRole::getId)
                    .collect(Collectors.toSet());
            ids.removeAll(exist);
            throw new IllegalArgumentException("角色不存在：" + ids);
        }
        String roleNames = roles.stream()
                .map(UserRole::getRoleName)
                .collect(Collectors.joining(","));
        SysUser user = findUserById(userId);
        user.setRoles(roleNames);
        userMapper.update(user);
        return user;
    }

    /**
     * 重置用户密码（管理员）。
     *
     * @param userId      用户 ID
     * @param newPassword 新密码（明文）
     * @return 更新后的用户对象
     * @throws ResourceNotFoundException 用户不存在时抛出
     * @throws BusinessException         旧密码验证失败时抛出
     */
    public SysUser resetPassword(Long userId, String newPassword) {
        SysUser user = this.findUserById(userId);
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setPasswordResetAt(LocalDateTime.now());
        userMapper.update(user);
        log.info("用户ID: {} 的密码强制修改成功", userId);
        return user;
    }

    /**
     * 封禁指定用户。
     *
     * @param userId 用户ID
     * @return 更新后的用户对象
     * @throws ResourceNotFoundException 用户不存在时抛出
     */
    public SysUser blockUser(Long userId) {
        SysUser user = this.findUserById(userId);
        user.setStatus(UserStatusEnum.BANNED);
        userMapper.update(user);
        log.info("用户ID: {} 封禁成功", userId);
        return user;
    }

    /**
     * 解封指定用户。
     *
     * @param userId 用户ID
     * @return 更新后的用户对象
     * @throws ResourceNotFoundException 用户不存在时抛出
     */
    public SysUser unblockUser(Long userId) {
        SysUser user = this.findUserById(userId);
        user.setStatus(UserStatusEnum.NORMAL);
        userMapper.update(user);
        log.info("用户ID: {} 解封成功", userId);
        return user;
    }

    /**
     * 删除用户（逻辑删除）。
     *
     * @param userId 用户ID
     * @return 被删除的用户对象
     * @throws ResourceNotFoundException 用户不存在时抛出
     */
    public Long deleteUser(Long userId) {
        userMapper.deleteById(userId);
        log.info("用户ID: {} 删除成功 (逻辑删除)", userId);
        return userId;
    }
}
