package com.example.local_boot_manager.controller;

import com.example.local_boot_manager.service.ManagerService;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/manage")
@RequiredArgsConstructor
public class DashboardApiController {

    private final ManagerService managerService;

    @PostMapping("/infra/{command}")
    public ResponseEntity<Void> controlInfra(@PathVariable String command) throws IOException {
        managerService.controlInfrastructure(command);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/service/{serviceName}/start")
    public ResponseEntity<Void> startService(@PathVariable String serviceName) {
        managerService.startService(serviceName);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/service/{serviceName}/stop")
    public ResponseEntity<Void> stopService(@PathVariable String serviceName) {
        managerService.stopService(serviceName);
        return ResponseEntity.ok().build();
    }
}
