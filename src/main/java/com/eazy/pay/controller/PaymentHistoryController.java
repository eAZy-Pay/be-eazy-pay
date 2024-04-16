package com.eazy.pay.controller;


import com.eazy.pay.dto.MonthlyFor6ResponseDTO;
import com.eazy.pay.model.MonthlyFor6;
import com.eazy.pay.service.MonthlyFor6Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payment-history")
public class PaymentHistoryController {

    @Autowired
    private MonthlyFor6Service monthlyFor6Service;

    @GetMapping("/monthly-for-6")
    public List<MonthlyFor6ResponseDTO> getMonthlyFor6ByUserId(@RequestParam("user_id") Long userId) {
        return monthlyFor6Service.getMonthlyFor6ByUserId(userId);
    }
}
