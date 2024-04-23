package com.eazy.pay.controller;

import com.eazy.pay.dao.UserRepository;
import com.eazy.pay.dto.UserPinDTO;
import com.eazy.pay.model.User;
import com.eazy.pay.service.PayService;
import com.eazy.pay.service.UserCardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/shopping")
public class ShoppingController {
    @Autowired
    PayService payService;
    @GetMapping
    public Object pay(@RequestParam(value = "user_id") Long userId){
       /* if(payService.pay(userId)) {
            return ResponseEntity.ok().build();
        }*/
        return payService.pay(userId);
    }
}
