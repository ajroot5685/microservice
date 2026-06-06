package com.example.local_boot_manager.service.other;

import static com.example.local_boot_manager.config.ManagedServiceConstants.CONFIG_SERVICE;
import static com.example.local_boot_manager.config.ManagedServiceConstants.LOCAL_BOOT_MANAGER;
import static com.example.local_boot_manager.config.ManagedServiceConstants.SERVICE_DISCOVERY;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class HealthCheckService {

    private static final String CONTAINER_NAME = "user_db";
    private static final Map<String, String> SERVICE_LOCAL_MAP = Map.of(
            SERVICE_DISCOVERY, "http://localhost:8761",
            CONFIG_SERVICE, "http://localhost:8888"
    );

    private static final String RUNNING = "RUNNING";
    private static final String DOWN = "DOWN";
    private static final String STARTING = "STARTING";

    private final DiscoveryClient discoveryClient;
    private final WebClient webClient = WebClient.builder().build();

    private final PidStoreService pidStoreService;

    public Mono<String> checkInfraHealth() {
        return Mono.fromCallable(() -> {
            try {
                ProcessBuilder pb = new ProcessBuilder("cmd.exe", "/c",
                        "docker ps --filter name=" + CONTAINER_NAME + " --format \"{{.Status}}\"");
                Process process = pb.start();
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                    String line = reader.readLine();
                    if (line != null && line.toLowerCase().contains("up")) {
                        return RUNNING;
                    }
                }
            } catch (Exception e) {
                log.error("도커 상태 조회 실패", e);
            }
            return DOWN;
        }).onErrorReturn(DOWN);
    }

    public Mono<String> checkServiceHealth(String serviceName) {
        if (LOCAL_BOOT_MANAGER.equals(serviceName)) {
            return Mono.just(RUNNING);
        }

        long pid = pidStoreService.getSavedPid(serviceName);
        if (pid == -1 || !pidStoreService.isOsProcessAlive(pid)) {
            return Mono.just(DOWN);
        }

        List<ServiceInstance> instances = discoveryClient.getInstances(serviceName.toUpperCase());
        if (!instances.isEmpty()) {
            String dynamicServiceUri = instances.get(0).getUri().toString();
            return webClient.get()
                    .uri(dynamicServiceUri + "/actuator/health")
                    .retrieve()
                    .bodyToMono(Map.class)
                    .map(body -> "UP".equals(body.get("status")) ? RUNNING : STARTING)
                    .onErrorReturn(STARTING);
        }

        if (SERVICE_LOCAL_MAP.containsKey(serviceName)) {
            return webClient.get()
                    .uri(SERVICE_LOCAL_MAP.get(serviceName) + "/actuator/health")
                    .retrieve()
                    .bodyToMono(Map.class)
                    .map(body -> "UP".equals(body.get("status")) ? RUNNING : STARTING)
                    .onErrorReturn(STARTING);
        }

        log.info("헬스 체크 중, {}", serviceName);
        return Mono.just(DOWN);
    }
}
