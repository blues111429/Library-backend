package org.example.backend.controller;

import org.example.backend.dto.response.Result;
import org.example.backend.dto.response.dashboard.CategoryPercentageResponse;
import org.example.backend.dto.response.dashboard.OverViewResponse;
import org.example.backend.dto.response.dashboard.ThirtyDaysBorrowResponse;
import org.example.backend.service.DashBoardService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
public class DashBoardController {

    private final DashBoardService dashBoardService;
    public DashBoardController(DashBoardService dashBoardService) { this.dashBoardService = dashBoardService; }

    @PostMapping("/overview")
    public Result<OverViewResponse> overView() { return dashBoardService.overView(); }

    @PostMapping("/30DaysBorrow")
    public Result<ThirtyDaysBorrowResponse> thirtyDaysBorrow() { return dashBoardService.thirtyDaysBorrow(); }

    @PostMapping("/categoryPercentage")
    public Result<CategoryPercentageResponse> categoryPercentage() { return dashBoardService.categoryPercentage(); }
}
