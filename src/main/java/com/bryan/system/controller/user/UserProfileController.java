package com.bryan.system.controller.user;

import com.bryan.system.domain.converter.UserConverter;
import com.bryan.system.domain.dto.user.UserProfileUpdateDTO;
import com.bryan.system.domain.entity.user.SysUser;
import com.bryan.system.domain.entity.user.UserProfile;
import com.bryan.system.domain.request.user.UserUpdateRequest;
import com.bryan.system.domain.response.Result;
import com.bryan.system.domain.vo.user.UserProfileVO;
import com.bryan.system.service.auth.AuthService;
import com.bryan.system.service.user.UserProfileService;
import com.bryan.system.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 用户资料控制器
 * 提供用户资料的查询、更新等接口。
 *
 * @author Bryan Long
 */
@Validated
@RestController
@RequestMapping("/api/user-profiles")
@RequiredArgsConstructor
public class UserProfileController {

    private final UserProfileService userProfileService;
    private final UserService userService;
    private final AuthService authService;

    /**
     * 根据用户主键查询用户资料
     *
     * @param userId 用户主键
     * @return 用户资料 VO
     */
    @GetMapping("/{userId}")
    public Result<UserProfileVO> getUserProfileByUserId(@PathVariable Long userId) {
        UserProfile profile = userProfileService.getUserProfileByUserId(userId);
        SysUser user = userService.getUserById(userId);
        return Result.success(UserConverter.toUserProfileVO(user, profile));
    }

    /**
     * 根据真实姓名查询用户资料
     *
     * @param realName 真实姓名
     * @return 用户资料 VO
     */
    @GetMapping("/name/{realName}")
    public Result<UserProfileVO> getUserProfileByRealName(@PathVariable String realName) {
        UserProfile profile = userProfileService.findUserProfileByRealName(realName);
        SysUser user = userService.getUserById(profile.getUserId());
        return Result.success(UserConverter.toUserProfileVO(user, profile));
    }

    /**
     * 获取当前登录用户的资料
     *
     * @return 用户资料 VO
     */
    @GetMapping("/me")
    public Result<UserProfileVO> getCurrentUserProfile() {
        Long userId = authService.getCurrentUserId();
        return this.getUserProfileByUserId(userId);
    }

    /**
     * 更新当前用户资料
     *
     * @param req 更新请求参数
     * @return 更新后的用户资料 VO
     */
    @PutMapping
    public Result<UserProfileVO> updateUserProfile(
            @RequestBody UserUpdateRequest req) {
        Long userId = authService.getCurrentUserId();
        UserProfileUpdateDTO dto = UserProfileUpdateDTO.builder()
                .realName(req.getRealName())
                .gender(req.getGender())
                .birthday(req.getBirthday())
                .avatar(req.getAvatar())
                .build();
        UserProfileVO vo = UserConverter.toUserProfileVO(authService.getCurrentUser(),
                userProfileService.updateUserProfile(userId, dto));
        return Result.success(vo);
    }
}