package com.example.local_boot_manager.service.log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Slf4j
@Service
public class LogWebSocketHandler extends TextWebSocketHandler {

    private final List<WebSocketSession> sessions = new CopyOnWriteArrayList<>();
    private final Path logDir = Paths.get("logs").toAbsolutePath().normalize();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        sessions.add(session);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        String payload = message.getPayload();
        if (payload.startsWith("FETCH_HISTORY:")) {
            String serviceName = payload.replace("FETCH_HISTORY:", "").trim();
            File logFile = logDir.resolve(serviceName + ".log").toFile();
            if (!logFile.exists()) {
                return;
            }

            try (BufferedReader fileReader = new BufferedReader(
                    new InputStreamReader(new FileInputStream(logFile), "MS949"))) {
                List<String> history = new ArrayList<>();
                String line;
                while ((line = fileReader.readLine()) != null) {
                    history.add(line);
                    if (history.size() > 150) {
                        history.remove(0);
                    }
                }
                for (String histLine : history) {
                    session.sendMessage(new TextMessage("[" + serviceName + "] " + histLine));
                }
            } catch (Exception e) {
                log.error("{} 과거 로그 반환 실패", serviceName, e);
            }
        }
    }

    public void broadcastLog(String serviceName, String logLine) {
        TextMessage message = new TextMessage("[" + serviceName + "] " + logLine);
        for (WebSocketSession session : sessions) {
            if (session.isOpen()) {
                try {
                    session.sendMessage(message);
                } catch (IOException ignored) {
                }
            }
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        sessions.remove(session);
    }
}
