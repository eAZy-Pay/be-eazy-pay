package com.eazy.pay.controller;


import com.eazy.pay.dto.MonthlyFor6ResponseDTO;
import com.eazy.pay.dto.PaymentHistoryDTO;
import com.eazy.pay.model.MonthlyFor6;
import com.eazy.pay.service.MonthlyFor6Service;
import com.eazy.pay.service.PaymentHistoryService;
import com.eazy.pay.service.UserCardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payment-history")
public class PaymentHistoryController {

    @Autowired
    private MonthlyFor6Service monthlyFor6Service;

    @Autowired
    private PaymentHistoryService PaymentHistoryService;

    @GetMapping
    public ResponseEntity<List<PaymentHistoryDTO>> getRecentPaymentHistoryByUserId(@RequestParam("user_id") Long userId, @RequestParam("limit") int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        Page<PaymentHistoryDTO> page = PaymentHistoryService.getPaymentHistoryByUserId(userId, pageable);
        return ResponseEntity.ok(page.getContent());
    }

    @GetMapping("/monthly-for-6")
    public List<MonthlyFor6ResponseDTO> getMonthlyFor6ByUserId(@RequestParam("user_id") Long userId) {
        return monthlyFor6Service.getMonthlyFor6ByUserId(userId);
    }


}
