package com.eazy.pay.controller;

import com.eazy.pay.dao.UserRepository;
import com.eazy.pay.dto.CompletedPaymentDTO;
import com.eazy.pay.dto.PayRequestDTO;
import com.eazy.pay.dto.UserPinDTO;
import com.eazy.pay.model.User;
import com.eazy.pay.service.PayService;
import com.eazy.pay.service.UserCardService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/api/shopping")
public class ShoppingController {
    @Autowired
    PayService payService;

    @PostMapping("/order")
    public ResponseEntity payrequest(@RequestBody PayRequestDTO dto){
        Object response = payService.pay(dto);
        if(response.equals("NoAvailableCard")){
            log.info(dto.getUserId() + " has no available card");
            return ResponseEntity.status(500).body("NoAvailableCard");
        }
        // 개발자가 설정한 로그 - response 객체를 CompletedPaymentDTO로 변환
        CompletedPaymentDTO responseDTO = (CompletedPaymentDTO) response;
        log.info("UserUid+ " + dto.getUserId() + " paid ₩" + responseDTO.getOriginalAmount()
                + " with " + responseDTO.getCardName() + " card at " + dto.getStoreName()
                + " and will be paid back ₩" + responseDTO.getDiscount() + " next month.");
        return ResponseEntity.ok(response);
    }
}
