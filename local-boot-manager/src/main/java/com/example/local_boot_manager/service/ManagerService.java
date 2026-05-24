package com.example.local_boot_manager.service;

import static com.example.local_boot_manager.config.ManagedServiceConstants.APIGATEWAY_SERVICE;
import static com.example.local_boot_manager.config.ManagedServiceConstants.CATALOG_SERVICE;
import static com.example.local_boot_manager.config.ManagedServiceConstants.LOCAL_BOOT_MANAGER;
import static com.example.local_boot_manager.config.ManagedServiceConstants.ORDER_SERVICE;
import static com.example.local_boot_manager.config.ManagedServiceConstants.SERVICE_DISCOVERY;
import static com.example.local_boot_manager.config.ManagedServiceConstants.USER_SERVICE;

import com.example.local_boot_manager.service.log.LogService;
import com.example.local_boot_manager.service.log.LogStoreService;
import com.example.local_boot_manager.service.other.PidStoreService;
import jakarta.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ManagerService {

    private final Path rootPath = Paths.get("..").toAbsolutePath().normalize();
    private final List<String> managedServices = List.of(
            LOCAL_BOOT_MANAGER,
            SERVICE_DISCOVERY,
            APIGATEWAY_SERVICE,
            USER_SERVICE,
            CATALOG_SERVICE,
            ORDER_SERVICE
    );

    private final PidStoreService pidStoreService;
    private final LogService logService;
    private final LogStoreService logStoreService;

    @PostConstruct
    public void init() {
        logService.captureDashboardLog();

        for (String service : managedServices) {
            if (LOCAL_BOOT_MANAGER.equals(service)) {
                continue;
            }

            long pid = pidStoreService.getSavedPid(service);
            if (pid != -1 && pidStoreService.isOsProcessAlive(pid)) {
                log.info("🔗 [프로세스 복구] {} (PID: {})", service, pid);
                logService.streamProcessLogs(service);
            } else {
                pidStoreService.deletePid(service);
            }
        }
    }

    public void controlInfrastructure(String command) throws IOException {
        ProcessBuilder pb = "up".equals(command)
                ? new ProcessBuilder("cmd.exe", "/c", "docker-compose up -d")
                : new ProcessBuilder("cmd.exe", "/c", "docker-compose down");
        pb.directory(rootPath.toFile());
        pb.start();
    }

    public void startService(String serviceName) {
        long existingPid = pidStoreService.getSavedPid(serviceName);
        if (existingPid != -1 && pidStoreService.isOsProcessAlive(existingPid)) {
            return;
        }

        Path servicePath = rootPath.resolve(serviceName);
        File logFile = logStoreService.getLogFile(serviceName);

        Thread.startVirtualThread(() -> {
            try {
                ProcessBuilder pb = new ProcessBuilder("cmd.exe", "/c", "gradlew.bat bootRun");
                pb.directory(servicePath.toFile());
                pb.redirectErrorStream(true);
                pb.redirectOutput(ProcessBuilder.Redirect.appendTo(logFile));

                Process process = pb.start();
                long pid = process.pid();

                pidStoreService.savePid(serviceName, pid);
                log.info("▶ [Local Process] {} 시작됨 (PID: {})", serviceName, pid);

                logService.streamProcessLogs(serviceName);
                process.waitFor();
            } catch (Exception e) {
                log.error("{} 기동 실패", serviceName, e);
            } finally {
                pidStoreService.deletePid(serviceName);
            }
        });
    }

    public void stopService(String serviceName) {
        logService.stopWatching(serviceName);

        long pid = pidStoreService.getSavedPid(serviceName);
        if (pid == -1) {
            return;
        }

        try {
            ProcessHandle.of(pid).ifPresent(handle -> {
                try {
                    handle.descendants().forEach(ProcessHandle::destroy);
                    handle.destroy();

                    new ProcessBuilder("taskkill", "/T", "/PID", String.valueOf(pid)).start();

                    int maxAttempts = 10;
                    int attempts = 0;
                    while (handle.isAlive() && attempts < maxAttempts) {
                        Thread.sleep(1000);
                        attempts++;
                    }

                    if (handle.isAlive()) {
                        handle.descendants().forEach(ProcessHandle::destroyForcibly);
                        handle.destroyForcibly();
                        new ProcessBuilder("taskkill", "/F", "/T", "/PID", String.valueOf(pid)).start();
                        Thread.sleep(500);
                    }
                    log.info("⏹ [Shutdown] {} 종료 완료", serviceName);
                } catch (Exception e) {
                    log.error("{} 중지 중 오류 발생", serviceName, e);
                }
            });
        } finally {
            pidStoreService.deletePid(serviceName);
            logStoreService.deleteLogFile(serviceName);
        }
    }

    public List<String> getDefinedServices() {
        return managedServices;
    }
}