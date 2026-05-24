package com.example.local_boot_manager.service.other;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class PidStoreService {

    private static final String PID_EXT = ".pid";

    private final Path pidDir = Paths.get("pids").toAbsolutePath().normalize();

    public PidStoreService() {
        try {
            Files.createDirectories(pidDir);
        } catch (IOException e) {
            log.error("PID 관리 폴더 생성 실패", e);
        }
    }

    public void savePid(String serviceName, long pid) {
        try {
            Files.writeString(pidDir.resolve(serviceName + PID_EXT), String.valueOf(pid));
        } catch (IOException e) {
            log.error("{} PID 파일 저장 실패", serviceName, e);
        }
    }

    public long getSavedPid(String serviceName) {
        Path path = pidDir.resolve(serviceName + PID_EXT);
        if (!Files.exists(path)) {
            return -1;
        }
        try {
            return Long.parseLong(Files.readString(path).trim());
        } catch (Exception e) {
            return -1;
        }
    }

    public void deletePid(String serviceName) {
        try {
            Files.deleteIfExists(pidDir.resolve(serviceName + PID_EXT));
        } catch (IOException ignored) {
        }
    }

    public boolean isOsProcessAlive(long pid) {
        return ProcessHandle.of(pid).map(ProcessHandle::isAlive).orElse(false);
    }
}
