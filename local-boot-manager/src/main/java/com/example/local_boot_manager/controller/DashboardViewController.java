package com.example.local_boot_manager.controller;

import com.example.local_boot_manager.service.ManagerService;
import com.example.local_boot_manager.service.other.HealthCheckService;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class DashboardViewController {

    private final ManagerService managerService;
    private final HealthCheckService healthCheckService;

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("services", fetchCurrentStatus());
        return "index";
    }

    @GetMapping("/fragments/status")
    public String statusFragment(Model model) {
        Map<String, String> servicesStatus = new LinkedHashMap<>();

        for (String service : managerService.getDefinedServices()) {
            String status = healthCheckService.checkServiceHealth(service).block();
            servicesStatus.put(service, status);
        }

        String infraStatus = healthCheckService.checkInfraHealth().block();

        model.addAttribute("services", servicesStatus);
        model.addAttribute("infraStatus", infraStatus);

        return "index :: #status-board";
    }

    private Map<String, String> fetchCurrentStatus() {
        Map<String, String> statusMap = new LinkedHashMap<>();
        List<String> services = managerService.getDefinedServices();

        for (String service : services) {
            String status = healthCheckService.checkServiceHealth(service).block();
            statusMap.put(service, status);
        }
        return statusMap;
    }
}
