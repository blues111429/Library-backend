package org.example.backend.dto.response.dashboard;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ThirtyDaysBorrowResponse {
    private List<DailyBorrowCount> borrowCounts;
}
