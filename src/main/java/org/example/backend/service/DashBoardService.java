package org.example.backend.service;

import org.example.backend.dto.request.dashboard.OverViewRequest;
import org.example.backend.dto.response.Result;
import org.example.backend.dto.response.dashboard.CategoryPercentageResponse;
import org.example.backend.dto.response.dashboard.OverViewResponse;
import org.example.backend.dto.response.dashboard.ThirtyDaysBorrowResponse;
import org.springframework.stereotype.Service;

@Service
public interface DashBoardService {
    Result<OverViewResponse> overView();

    Result<ThirtyDaysBorrowResponse> thirtyDaysBorrow();

    Result<CategoryPercentageResponse> categoryPercentage();
}
