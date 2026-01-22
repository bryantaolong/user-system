package com.bryan.system.service.system;

import com.bryan.system.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.zip.GZIPInputStream;

/**
 * 日志服务：提供读取应用日志内容的能力，供后台管理端使用。
 */
@Slf4j
@Service
public class LogService {

    /**
     * 日志文件路径，从 Spring Boot logging.file.name 读取，默认为 logs/platform.log。
     */
    @Value("${logging.file.name:logs/platform.log}")
    private String logFileName;

    /**
     * 获取最近的日志内容（按行），从文件末尾开始最多返回指定条数。
     *
     * @param maxLines 需要返回的最大行数
     * @return 日志行列表（按时间正序，从旧到新）
     */
    public List<String> getLatestLogs(int maxLines) {
        return getLatestLogs(null, maxLines);
    }

    /**
     * 获取指定日志文件最近的日志内容（按行）。
     *
     * @param fileName 日志文件名（可选），为空时使用默认日志文件
     * @param maxLines 需要返回的最大行数
     * @return 日志行列表（按时间正序，从旧到新）
     */
    public List<String> getLatestLogs(String fileName, int maxLines) {
        int limit = Math.max(1, Math.min(maxLines, 2000));
        Path path = resolveLogPath(fileName);

        if (!Files.exists(path)) {
            log.warn("Log file not found at path: {}", path.toAbsolutePath());
            throw new BusinessException("日志文件不存在，请检查日志配置。");
        }

        try {
            List<String> allLines = readAllLines(path);
            if (allLines.isEmpty()) {
                return Collections.emptyList();
            }
            int fromIndex = Math.max(0, allLines.size() - limit);
            return allLines.subList(fromIndex, allLines.size());
        } catch (IOException e) {
            log.error("Failed to read log file: {}", path.toAbsolutePath(), e);
            throw new BusinessException("读取日志文件失败，请稍后重试。", e);
        }
    }
    
    /**
     * 读取日志文件的所有行，支持普通文本和 gzip 压缩文件。
     */
    private List<String> readAllLines(Path path) throws IOException {
        String fileName = path.getFileName().toString();
        if (fileName.endsWith(".gz")) {
            List<String> lines = new ArrayList<>();
            try (InputStream in = Files.newInputStream(path);
                 GZIPInputStream gzip = new GZIPInputStream(in);
                 InputStreamReader isr = new InputStreamReader(gzip, StandardCharsets.UTF_8);
                 BufferedReader reader = new BufferedReader(isr)) {
                String line;
                while ((line = reader.readLine()) != null) {
                    lines.add(line);
                }
            }
            return lines;
        }
        return Files.readAllLines(path, StandardCharsets.UTF_8);
    }
    
    /**
     * 获取可用的日志文件列表（仅返回文件名）。
     */
    public List<String> listLogFiles() {
        Path defaultLogPath = resolveDefaultLogPath();
        Path logDir = getLogsDirectory();

        if (!Files.exists(logDir) || !Files.isDirectory(logDir)) {
            return List.of(defaultLogPath.getFileName().toString());
        }

        try (var stream = Files.list(logDir)) {
            List<String> files = stream
                    .filter(Files::isRegularFile)
                    .map(path -> path.getFileName().toString())
                    .filter(name -> name.endsWith(".log") || name.endsWith(".gz"))
                    .sorted()
                    .toList();
            if (files.isEmpty()) {
                return List.of(defaultLogPath.getFileName().toString());
            }
            return files;
        } catch (IOException e) {
            log.warn("Failed to list log files in directory: {}", logDir.toAbsolutePath(), e);
            return List.of(defaultLogPath.getFileName().toString());
        }
    }

    private Path resolveLogPath(String fileName) {
        if (fileName == null || fileName.isBlank()) {
            return resolveDefaultLogPath();
        }
        Path logDir = getLogsDirectory();
        Path path = logDir.resolve(fileName).normalize();
        if (!path.startsWith(logDir)) {
            throw new BusinessException("非法的日志文件路径");
        }
        return path;
    }

    private Path resolveDefaultLogPath() {
        Path path = Paths.get(logFileName);
        if (!path.isAbsolute()) {
            Path userDir = Paths.get(System.getProperty("user.dir"));
            path = userDir.resolve(path).normalize();
        }
        return path;
    }

    private Path getLogsDirectory() {
        Path defaultLogPath = resolveDefaultLogPath();
        Path parent = defaultLogPath.getParent();
        if (parent == null) {
            return Paths.get(System.getProperty("user.dir"));
        }
        return parent;
    }
}