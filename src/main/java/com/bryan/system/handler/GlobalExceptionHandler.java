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
 * 全局异常处理器（替代 WebMvcConfig 中的异常处理逻辑）
 *
 * @author Bryan Long
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理运行时异常
     */
    @ExceptionHandler(RuntimeException.class)
    public Result<String> handleRuntimeException(HttpServletRequest request, RuntimeException e) {
        // 避免在日志中暴露敏感信息，只记录必要信息
        log.error("请求URL: {}, 业务异常类型: {}", 
            request.getRequestURL(), e.getClass().getSimpleName());
        
        // 生产环境不返回详细错误信息
        return Result.error(HttpStatus.INTERNAL_ERROR, "服务繁忙，请稍后重试");
    }

    /**
     * 处理参数校验异常
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
     * 处理 404 异常（需配合Spring Boot的 ErrorController）
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public Result<String> handleNotFoundException(ResourceNotFoundException e) {
        log.warn("资源不存在: {}", e.getMessage());
        return Result.error(HttpStatus.NOT_FOUND);
    }

    /**
     * 处理业务异常（HTTP 500）
     */
    @ExceptionHandler(BusinessException.class)
    public Result<String> handleBusinessException(BusinessException e) {
        log.error("业务异常: {}", e.getMessage(), e);
        return Result.error(HttpStatus.INTERNAL_ERROR, e.getMessage());
    }

    /**
     * 处理未授权异常（HTTP 401）
     * 注意：由于类上有@ResponseStatus，实际会优先使用注解的HTTP状态码
     */
    @ExceptionHandler(UnauthorizedException.class)
    public Result<String> handleUnauthorizedException(UnauthorizedException e) {
        log.warn("未授权访问: {}", e.getMessage());
        return Result.error(HttpStatus.UNAUTHORIZED, e.getMessage());
    }
}
