package com.eazy.pay.controller;


import com.eazy.pay.dto.MonthlyFor6ResponseDTO;
import com.eazy.pay.dto.PaymentHistoryDTO;
import com.eazy.pay.dto.PaymentStatsDTO;
import com.eazy.pay.model.UsageStatistic;
import com.eazy.pay.service.MonthlyFor6Service;
import com.eazy.pay.service.PaymentHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Date;
import java.util.List;

@RestController
@RequestMapping("/api/payment-history")
public class PaymentHistoryController {

    @Autowired
    private MonthlyFor6Service monthlyFor6Service;

    @Autowired
    private PaymentHistoryService paymentHistoryService;

    @GetMapping
    public ResponseEntity<List<PaymentHistoryDTO>> getRecentPaymentHistory(
            @RequestParam("user_id") Long userId,
            @RequestParam("year") int year,
            @RequestParam("month") int month,
            @RequestParam("offset") int offset,
            @RequestParam("limit") int limit
    ) {
        PageRequest pageable = PageRequest.of(offset / limit, limit);
        Page<PaymentHistoryDTO> paymentHistoryPage = paymentHistoryService.getPaymentHistoryByUserId(userId, year, month, pageable);

        return ResponseEntity.ok(paymentHistoryPage.getContent());
    }

    @GetMapping("/count-and-amount")
    public PaymentStatsDTO getPaymentHistoryAndAmount(
            @RequestParam("user_id") Long userId,
            @RequestParam("year") int year,
            @RequestParam("month") int month
    ) {
        return paymentHistoryService.getPaymentHistoryAndAmountForMonth(userId, year, month);
    }

    @GetMapping("/monthly-for-6")
    public List<MonthlyFor6ResponseDTO> getMonthlyFor6ByUserIdAndDate(@RequestParam(value = "user_id", required = false) Long userId,
                                                                        @RequestParam(value = "date", required = false) Date date,
                                                                      @RequestParam(value = "age", required = false) Integer age

    ) {
        Date useDate = date == null ? new Date(System.currentTimeMillis()) : date;
        Date firstDayOfMonth = Date.valueOf(useDate.toLocalDate().withDayOfMonth(1));

        if(age != null && age >= 0){
            return monthlyFor6Service.getMonthlyFor6ByAgeAndDate(age, firstDayOfMonth);
        }

        if(userId == null || userId <= 0){
            return monthlyFor6Service.getMonthlyFor6ByAgeAndDate(0, firstDayOfMonth);
        }

        return monthlyFor6Service.getMonthlyFor6ByUserIdAndDate(userId, firstDayOfMonth);
    }

    @GetMapping("/usage-statistics")
    public List<UsageStatistic> getUsageStaticsByUserIdAndDate(@RequestParam("user_id") Long userId, @RequestParam(value = "date", required = false) Date date) {
        Date useDate = date == null ? new Date(System.currentTimeMillis()) : date;
        Date firstDayOfMonth = Date.valueOf(useDate.toLocalDate().withDayOfMonth(1));
        return paymentHistoryService.getUsageStaticsByUserIdAndDate(userId, firstDayOfMonth);
    }
}
