package org.example.backend.dto.response.dashboard;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DailyBorrowCount {
    private String borrowData;
    private Integer borrowCount;

    public DailyBorrowCount(String borrowData, Integer borrowCount) {
        this.borrowData = borrowData;
        this.borrowCount = borrowCount;
    }
}
