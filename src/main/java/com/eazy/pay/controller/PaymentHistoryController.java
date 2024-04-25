package com.eazy.pay.controller;


import com.eazy.pay.dto.MonthlyFor6ResponseDTO;
import com.eazy.pay.dto.PaymentHistoryDTO;
import com.eazy.pay.service.MonthlyFor6Service;
import com.eazy.pay.service.PaymentHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/total")
    public ResponseEntity<Integer> getPaymentHistoryForMonth(
            @RequestParam("year") int year,
            @RequestParam("month") int month
    ) {
        int totalTransactions = paymentHistoryService.getPaymentHistoryForMonth(year, month);
        return ResponseEntity.ok(totalTransactions);
    }

    @GetMapping("/total-amount")
    public ResponseEntity<Integer> getTotalAmountForMonth(
            @RequestParam("year") int year,
            @RequestParam("month") int month
    ) {
        int totalAmount = paymentHistoryService.getTotalAmountForMonth(year, month);
        return ResponseEntity.ok(totalAmount);
    }

    @GetMapping("/monthly-for-6")
    public List<MonthlyFor6ResponseDTO> getMonthlyFor6ByUserId(@RequestParam("user_id") Long userId) {
        return monthlyFor6Service.getMonthlyFor6ByUserId(userId);
    }


}
