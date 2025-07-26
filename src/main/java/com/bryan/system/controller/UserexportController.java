package com.bryan.system.controller;

import com.bryan.system.model.entity.User;
import com.bryan.system.model.request.UserSearchRequest;
import com.bryan.system.model.response.Result;
import com.bryan.system.service.UserExportService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * UserexportController
 *
 * @author Bryan Long
 * @version 1.0
 * @since 2025/7/26
 */
@RestController
@RequestMapping("/api/user/export")
@RequiredArgsConstructor
class UserexportController {

    private final UserExportService userExportService;

    /**
     * 导出所有用户数据为 Excel 文件。
     * <p>仅管理员可操作。</p>
     *
     * @param response HttpServletResponse
     */
    @GetMapping("/export/all")
    @PreAuthorize("hasRole('ADMIN')")
    public void exportAllUsers(HttpServletResponse response) {
        userExportService.exportAllUsers(response);
    }

    @PostMapping("/export/condition")
    @PreAuthorize("hasRole('ADMIN')")
    public void exportUsersByCondition(@RequestBody UserSearchRequest searchRequest,
                                       HttpServletResponse response) {
        userExportService.exportUsersByCondition(searchRequest, response);
    }
}
