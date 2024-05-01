package com.eazy.pay.controller;

import com.eazy.pay.dto.CardToPayListRequestDTO;
import com.eazy.pay.dto.CompletedPaymentDTO;
import com.eazy.pay.dto.PayAutoRequestDTO;
import com.eazy.pay.dto.PayUserRequestDTO;
import com.eazy.pay.service.PayService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/pay")
public class PayController {
    @Autowired
    PayService payService;

    @PostMapping("/auto-select-order")
    public ResponseEntity payrequest(@RequestBody PayAutoRequestDTO dto){
        Object response = payService.autoPay(dto);
        if(response.equals("NoAvailableCard")){
            log.info(dto.getUserId() + " has no available card");
            return ResponseEntity.status(500).body("NoAvailableCard");
        }
        // 개발자가 설정한 로그 - response 객체를 CompletedPaymentDTO로 변환
        CompletedPaymentDTO responseDTO = (CompletedPaymentDTO) response;
        log.info("UserUid+ " + dto.getUserId() + " paid ₩" + responseDTO.getPrice()
                + " with " + responseDTO.getCardName() + " card at " + dto.getStoreName()
                + " and will be paid back ₩" + responseDTO.getPayback() + " next month.");
        return ResponseEntity.ok(response);
    }
    @PostMapping("/user-select-order")
    public ResponseEntity payByUserSelectedCard(@RequestBody PayUserRequestDTO dto){
        Object response = payService.userPay(dto);
        if(response.equals("NoAvailableCard")){
            log.info(dto.getUserId() + " has no available card");
            return ResponseEntity.status(500).body("NoAvailableCard");
        }
        // 개발자가 설정한 로그 - response 객체를 CompletedPaymentDTO로 변환
        CompletedPaymentDTO responseDTO = (CompletedPaymentDTO) response;
        log.info("UserUid+ " + dto.getUserId() + " paid ₩" + responseDTO.getPrice()
                + " with " + responseDTO.getCardName() + " card at " + dto.getStoreName()
                + " and will be paid back ₩" + responseDTO.getPayback() + " next month.");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/recommendation-list")
    public ResponseEntity cardlistrequest(@RequestBody CardToPayListRequestDTO requestBody){
        Object response = payService.getCardListToPay(requestBody);

        if(response == null){
            log.info("UserId :" +requestBody.getUserId() + " has no available card list");
            return ResponseEntity.status(500).body("NoAvailableCard");
        }
        return ResponseEntity.ok(response);
    }
}
