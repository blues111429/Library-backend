package org.example.backend.service.impl;

import org.example.backend.dto.request.dashboard.OverViewRequest;
import org.example.backend.dto.response.Result;
import org.example.backend.dto.response.dashboard.*;
import org.example.backend.mapper.DashBoardMapper;
import org.example.backend.service.DashBoardService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class DashBoardServiceImpl implements DashBoardService {

    private static DashBoardMapper dashBoardMapper;
    public DashBoardServiceImpl(DashBoardMapper dashBoardMapper) {
        DashBoardServiceImpl.dashBoardMapper = dashBoardMapper;
    }

    @Override
    public Result<OverViewResponse> overView() {
        Integer borrowedBook = dashBoardMapper.getBorrowedBookCount();
        Integer overDueBook = dashBoardMapper.getOverDueBook();
        Integer registerUser = dashBoardMapper.getRegisterUser();
        Integer totalBook = dashBoardMapper.getTotalBook();
        OverViewResponse response = OverViewResponse.builder()
                                    .borrowed_book(borrowedBook)
                                    .overDue_book(overDueBook)
                                    .register_user(registerUser)
                                    .total_book(totalBook)
                                    .build();
        return Result.success(response);
    }

    @Override
    public Result<ThirtyDaysBorrowResponse> thirtyDaysBorrow() {
        List<DailyBorrowCount> borrowCounts = dashBoardMapper.get30DaysBorrowCount();
        LocalDate today = LocalDate.now();
        List<LocalDate> dateList = new ArrayList<>();
        for(int i=0; i<30; i++) {
            dateList.add(today.minusDays(i));
        }
        Map<String, Integer> borowMap = new HashMap<>();
        for(DailyBorrowCount count : borrowCounts) {
            borowMap.put(count.getBorrowData(), count.getBorrowCount());
        }
        List<DailyBorrowCount> fullBorrowCountList = new ArrayList<>();
        for (LocalDate date : dateList) {
            String formattedDate = date.format(DateTimeFormatter.ISO_LOCAL_DATE);
            Integer borrowCount = borowMap.getOrDefault(formattedDate, 0);
            fullBorrowCountList.add(new DailyBorrowCount(formattedDate, borrowCount));
        }
        ThirtyDaysBorrowResponse response = ThirtyDaysBorrowResponse.builder()
                                            .borrowCounts(fullBorrowCountList)
                                            .build();
        return Result.success(response);
    }

    @Override
    public Result<CategoryPercentageResponse> categoryPercentage() {
        List<CategoryCount> categoryCounts = dashBoardMapper.getCategoryCounts();
        int totalBookCount = dashBoardMapper.getTotalBook();
        Map<String, Integer> categoryMap = new HashMap<>();
        for (CategoryCount categoryCount : categoryCounts) {
            categoryMap.put(categoryCount.getCategory(), categoryCount.getCount());
        }
        List<CategoryCount> fullCategoryList = new ArrayList<>();
        for (String category : categoryMap.keySet()) {
            Integer count = categoryMap.get(category);
            double percentage = (double) count / totalBookCount * 100;
            fullCategoryList.add(new CategoryCount(category, count));
        }

        CategoryPercentageResponse response = CategoryPercentageResponse.builder()
                        .categoryCounts(fullCategoryList)
                        .build();

        return Result.success(response);
    }
}
