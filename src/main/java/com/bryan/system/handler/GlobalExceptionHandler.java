package com.bryan.system.handler;

import com.bryan.system.domain.enums.HttpStatus;
import com.bryan.system.domain.response.Result;
import com.bryan.system.exception.BusinessException;
import com.bryan.system.exception.ResourceNotFoundException;
import com.bryan.system.exception.UnauthorizedException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

/**
 * 全局异常处理器
 * 统一捕获所有控制器层异常，转换为标准 Result 响应，避免敏感信息泄露。
 *
 * @author Bryan Long
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理运行时异常兜底
     * 生产环境仅返回友好提示，不暴露堆栈
     *
     * @param request 当前请求
     * @param e       异常
     * @return 统一错误响应
     */
    @ExceptionHandler(RuntimeException.class)
    public Result<String> handleRuntimeException(HttpServletRequest request, RuntimeException e) {
        log.error("请求URL: {}, 业务异常类型: {}",
                request.getRequestURL(), e.getClass().getSimpleName());
        return Result.error(HttpStatus.INTERNAL_ERROR, "服务繁忙，请稍后重试");
    }

    /**
     * 处理参数校验异常
     * 提取全部字段错误信息并拼接返回
     *
     * @param e 校验异常
     * @return 统一错误响应
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<String> handleValidationException(MethodArgumentNotValidException e) {
        String errorMsg = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));
        log.warn("参数校验失败: {}", errorMsg);
        return Result.error(HttpStatus.BAD_REQUEST, errorMsg);
    }

    /**
     * 处理资源不存在异常
     *
     * @param e 自定义 404 异常
     * @return 统一错误响应
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public Result<String> handleNotFoundException(ResourceNotFoundException e) {
        log.warn("资源不存在: {}", e.getMessage());
        return Result.error(HttpStatus.NOT_FOUND);
    }

    /**
     * 处理业务逻辑异常
     *
     * @param e 业务异常
     * @return 统一错误响应
     */
    @ExceptionHandler(BusinessException.class)
    public Result<String> handleBusinessException(BusinessException e) {
        log.error("业务异常: {}", e.getMessage(), e);
        return Result.error(HttpStatus.INTERNAL_ERROR, e.getMessage());
    }

    /**
     * 处理未授权异常
     *
     * @param e 授权异常
     * @return 统一错误响应
     */
    @ExceptionHandler(UnauthorizedException.class)
    public Result<String> handleUnauthorizedException(UnauthorizedException e) {
        log.warn("未授权访问: {}", e.getMessage());
        return Result.error(HttpStatus.UNAUTHORIZED, e.getMessage());
    }
}
