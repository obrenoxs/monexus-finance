package com.monexus.finance.dashboard.controller;

import com.monexus.finance.dashboard.dto.response.DashboardResponse;
import com.monexus.finance.dashboard.enums.DashboardPeriod;
import com.monexus.finance.dashboard.service.DashboardService;
import com.monexus.finance.user.security.CustomUserDetails;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping
    public ResponseEntity<DashboardResponse> getDashboard(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestParam(defaultValue = "CURRENT_MONTH")DashboardPeriod period) {
        DashboardResponse response = dashboardService.getDashboard(userDetails.getUser(), period);
        return ResponseEntity.ok(response);
    }
}
