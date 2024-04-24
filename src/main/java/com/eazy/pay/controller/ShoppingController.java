package com.eazy.pay.controller;

import com.eazy.pay.dao.UserRepository;
import com.eazy.pay.dto.CompletedPaymentDTO;
import com.eazy.pay.dto.PayRequestDTO;
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
    public Object pay(@RequestParam(value = "user_id") Long userId, @RequestParam(value = "category_id") Long categoryId, @RequestParam(value = "price") Integer price){
        Object dto = payService.pay(userId, categoryId, price, "storeCode", "storeName");
        if(dto.equals("NoAvailableCard")){
            return ResponseEntity.status(500).body("NoAvailableCard");
        }
        return ResponseEntity.ok(dto);
    }

    @PostMapping("/order")
    public ResponseEntity payrequest(@RequestBody PayRequestDTO dto){
        Object response = payService.pay(dto.getUserId(), dto.getCategoryId(), dto.getPrice(), dto.getStoreCode(), dto.getStoreCode());
        if(response.equals("NoAvailableCard")){
            return ResponseEntity.status(500).body("NoAvailableCard");
        }
        return ResponseEntity.ok(response);
    }
}
