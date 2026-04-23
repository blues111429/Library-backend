package org.example.backend.dto.response.dashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class CategoryPercentageResponse {
    private List<CategoryCount> categoryCounts;
}
