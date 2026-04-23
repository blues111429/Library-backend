package org.example.backend.dto.response.dashboard;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OverViewResponse {
    private Integer borrowed_book;
    private Integer overDue_book;
    private Integer total_book;
    private Integer register_user;
}
