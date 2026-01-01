package com.bryan.system.controller.user;

import com.bryan.system.domain.response.Result;
import com.bryan.system.domain.vo.user.UserRoleOptionVO;
import com.bryan.system.service.user.UserRoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * UserRoleController
 *
 * @author Bryan Long
 */
@Validated
@RestController
@RequestMapping("/api/user-roles")
@RequiredArgsConstructor
public class UserRoleController {

    private final UserRoleService userRoleService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Result<List<UserRoleOptionVO>> listRoles() {
        return Result.success(userRoleService.listAll().stream()
                .map(r -> new UserRoleOptionVO(r.getId(), r.getRoleName()))
                .toList());
    }
}
