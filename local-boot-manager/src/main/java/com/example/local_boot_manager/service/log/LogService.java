package com.example.local_boot_manager.service.log;

import static com.example.local_boot_manager.config.ManagedServiceConstants.LOCAL_BOOT_MANAGER;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class LogService {

    private final Map<String, Process> logWatchProcesses = new ConcurrentHashMap<>();

    private final LogWebSocketHandler webSocketHandler;
    private final LogStoreService logStoreService;


    public void streamProcessLogs(String serviceName) {
        stopWatching(serviceName);

        Thread.startVirtualThread(() -> {
            File logFile = logStoreService.getLogFile(serviceName);
            try {
                Thread.sleep(1000);
                ProcessBuilder pb = new ProcessBuilder(
                        "powershell.exe",
                        "-NoProfile",
                        "-Command",
                        "Get-Content '" + logFile.getAbsolutePath() + "' -Wait -Tail 0"
                );
                Process logProcess = pb.start();
                logWatchProcesses.put(serviceName, logProcess);
                readStreamAndBroadcast(serviceName, logProcess.getInputStream());
            } catch (Exception e) {
                log.error("{} 로그 스트림 복구 실패", serviceName, e);
            }
        });
    }

    public void stopWatching(String serviceName) {
        Process logProcess = logWatchProcesses.remove(serviceName);
        if (logProcess != null && logProcess.isAlive()) {
            logProcess.destroyForcibly();
        }
    }

    private void readStreamAndBroadcast(String serviceName, InputStream inputStream) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "MS949"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                webSocketHandler.broadcastLog(serviceName, line);
            }
        } finally {
            logWatchProcesses.remove(serviceName);
        }
    }

    public void captureDashboardLog() {
        Thread.startVirtualThread(() -> {
            ch.qos.logback.classic.Logger rootLogger =
                    (ch.qos.logback.classic.Logger) org.slf4j.LoggerFactory.getLogger(
                            org.slf4j.Logger.ROOT_LOGGER_NAME);

            ch.qos.logback.core.AppenderBase<ch.qos.logback.classic.spi.ILoggingEvent> customAppender =
                    new ch.qos.logback.core.AppenderBase<>() {
                        private final ch.qos.logback.classic.encoder.PatternLayoutEncoder encoder = new ch.qos.logback.classic.encoder.PatternLayoutEncoder();

                        @Override
                        public void start() {
                            encoder.setContext(context);
                            encoder.setPattern("%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n");
                            encoder.start();
                            super.start();
                        }

                        @Override
                        protected void append(ch.qos.logback.classic.spi.ILoggingEvent eventObject) {
                            if (!isStarted()) {
                                return;
                            }
                            String formattedMsg = encoder.getLayout().doLayout(eventObject);
                            if (formattedMsg != null && !formattedMsg.contains("ws://") && !formattedMsg.contains(
                                    LOCAL_BOOT_MANAGER)) {
                                webSocketHandler.broadcastLog(LOCAL_BOOT_MANAGER, formattedMsg.trim());
                            }
                        }
                    };

            customAppender.setContext(rootLogger.getLoggerContext());
            customAppender.start();
            rootLogger.addAppender(customAppender);
        });
    }
}
