package com.example.local_boot_manager.service.log;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class LogStoreService {

    private static final String LOG_EXT = ".log";
    private final Path logDir = Paths.get("logs").toAbsolutePath().normalize();

    public LogStoreService() {
        try {
            Files.createDirectories(logDir);
        } catch (IOException e) {
            log.error("로그 관리 폴더 생성 실패", e);
        }
    }

    public File getLogFile(String serviceName) {
        return logDir.resolve(serviceName + LOG_EXT).toFile();
    }

    public void deleteLogFile(String serviceName) {
        try {
            Path path = getLogFile(serviceName).toPath();
            Files.deleteIfExists(path);
        } catch (IOException e) {
            log.warn("⚠️ [{}] 로그 파일 잠금 해제 대기 중 삭제 지연 중", serviceName);
        }
    }
}
