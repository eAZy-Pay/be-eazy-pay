package com.eazy.pay.controller;

import com.eazy.pay.model.UserCardHistory;
import com.eazy.pay.service.UserCardHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user-card-history")
public class UserCardHistoryController {

    @Autowired
    UserCardHistoryService userCardHistoryService;



    @GetMapping("/fulfilled")
    public boolean getUserCardHistoriesFulfilled(@RequestParam("userCardId") Long userCardId) {
        return userCardHistoryService.getUserCardHistoryFulfilled(userCardId);
    }
}
