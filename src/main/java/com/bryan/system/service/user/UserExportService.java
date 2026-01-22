package com.bryan.system.service.user;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.handler.CellWriteHandler;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.bryan.system.domain.converter.UserConverter;
import com.bryan.system.domain.entity.user.SysUser;
import com.bryan.system.domain.request.user.UserExportRequest;
import com.bryan.system.domain.vo.user.UserExportVO;
import com.bryan.system.exception.BusinessException;
import com.bryan.system.mapper.user.UserMapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 用户导出业务服务
 * 支持全量导出与按指定字段导出，分批写入防止内存溢出。
 *
 * @author Bryan Long
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserExportService {

    private final UserMapper userMapper;

    /* -------------------- 公开导出入口 -------------------- */

    /**
     * 全量导出：所有字段
     *
     * @param exportRequest 文件名称、状态过滤条件
     * @param response      响应流
     */
    public void exportAllFields(UserExportRequest exportRequest, HttpServletResponse response) {
        try {
            String fileName = Optional.ofNullable(exportRequest.getFileName()).orElse("用户数据全量导出");
            setupResponse(response, fileName);

            ExcelWriter excelWriter = EasyExcel.write(response.getOutputStream())
                    .head(UserExportVO.class)                 // 全部字段
                    .registerWriteHandler(new CustomCellWriteHandler())
                    .build();

            executeBatchExport(excelWriter, EasyExcel.writerSheet("用户列表").build(), exportRequest);
        } catch (IOException e) {
            throw new BusinessException("全量导出失败，请检查系统资源", e);
        }
    }

    /**
     * 按字段导出：仅导出指定列
     *
     * @param exportRequest 字段列表、文件名称、状态过滤条件
     * @param response      响应流
     */
    public void exportUsersByFields(UserExportRequest exportRequest, HttpServletResponse response) {
        try {
            validateFieldNames(exportRequest.getFields());
            String fileName = Optional.ofNullable(exportRequest.getFileName()).orElse("用户数据导出");
            setupResponse(response, fileName);

            ExcelWriter excelWriter = EasyExcel.write(response.getOutputStream())
                    .head(UserExportVO.class)
                    .includeColumnFieldNames(exportRequest.getFields()) // 动态列
                    .registerWriteHandler(new CustomCellWriteHandler())
                    .build();

            executeBatchExport(excelWriter, EasyExcel.writerSheet("用户列表").build(), exportRequest);
        } catch (IOException e) {
            throw new BusinessException("用户数据导出失败，请稍后重试", e);
        }
    }

    /* -------------------- 私有辅助 -------------------- */

    /**
     * 字段名校验：确保请求字段在 VO 中真实存在
     */
    private void validateFieldNames(List<String> fields) {
        if (CollectionUtils.isEmpty(fields)) {
            throw new IllegalArgumentException("导出字段列表不能为空");
        }
        Set<String> valid = Arrays.stream(UserExportVO.class.getDeclaredFields())
                .filter(f -> f.isAnnotationPresent(com.alibaba.excel.annotation.ExcelProperty.class))
                .map(Field::getName)
                .collect(Collectors.toSet());

        fields.forEach(f -> {
            if (!valid.contains(f)) {
                throw new IllegalArgumentException(
                        String.format("无效导出字段: %s （可用字段: %s）", f, valid));
            }
        });
    }

    /**
     * 分批查询 + 写入，防止内存溢出
     */
    private void executeBatchExport(ExcelWriter excelWriter,
                                    WriteSheet writeSheet,
                                    UserExportRequest exportRequest) {
        int pageNum  = 1;
        int pageSize = 1000;
        int total    = 0;

        while (true) {
            int offset = (pageNum - 1) * pageSize;
            List<SysUser> records = userMapper.selectPage(offset, pageSize, null, exportRequest);
            if (CollectionUtils.isEmpty(records)) {
                break;
            }
            List<UserExportVO> vos = records.stream()
                    .map(UserConverter::toExportVO)
                    .toList();
            excelWriter.write(vos, writeSheet);
            total += vos.size();
            log.info("已导出 {} 条数据", total);
            pageNum++;
        }
        excelWriter.finish();
        log.info("导出完成，总计 {} 条数据", total);
    }

    /**
     * 设置响应头：文件名、编码、Content-Type
     */
    private void setupResponse(HttpServletResponse response, String fileName) throws IOException {
        String encoded = URLEncoder.encode(fileName, StandardCharsets.UTF_8).replace("+", "%20");
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + encoded + ".xlsx");
    }

    /**
     * 自定义单元格样式：表头浅绿背景 + 内容居中
     */
    private static class CustomCellWriteHandler implements CellWriteHandler {
        @Override
        public void afterCellCreate(com.alibaba.excel.write.metadata.holder.WriteSheetHolder writeSheetHolder,
                                    com.alibaba.excel.write.metadata.holder.WriteTableHolder writeTableHolder,
                                    Cell cell,
                                    com.alibaba.excel.metadata.Head head,
                                    Integer relativeRowIndex,
                                    Boolean isHead) {
            Workbook wb = cell.getSheet().getWorkbook();
            if (isHead) {
                CellStyle style = wb.createCellStyle();
                style.setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex());
                style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                style.setAlignment(HorizontalAlignment.CENTER);
                cell.setCellStyle(style);
            } else {
                CellStyle style = wb.createCellStyle();
                style.setAlignment(HorizontalAlignment.CENTER);
                cell.setCellStyle(style);
            }
        }
    }
}