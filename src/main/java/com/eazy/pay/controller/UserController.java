package com.eazy.pay.controller;

import com.eazy.pay.dto.UserCardDTO;
import com.eazy.pay.service.UserCardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserCardService userCardService;

    @PostMapping("/card")
    public ResponseEntity<String> saveUserCard(@RequestBody UserCardDTO userCardDTO) {

        // 카드 만료일이 없을 경우 5년 뒤로 설정
        if (userCardDTO.getExpirationDate() == null) {
            Date expirationDate = new Date();
            expirationDate.setYear(expirationDate.getYear() + 5);
            userCardDTO.setExpirationDate(expirationDate);
        }

        try {
            userCardService.createUserCard(userCardDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body("사용자의 카드가 성공적으로 신청되었습니다.");
        }catch (DataAccessException dae) { // 데이터베이스 관련 예외 처리
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("데이터베이스 오류가 발생했습니다.");
        }catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("서버 내부 오류가 발생했습니다. 나중에 다시 시도해주세요.");
        }
    }

    @PatchMapping("/card/{userCardId}")
    public ResponseEntity<String> disableUserCard(@PathVariable Long userCardId) {
        try {
            userCardService.disableUserCard(userCardId);
            return ResponseEntity.status(HttpStatus.OK).body("사용자의 카드가 성공적으로 사용 중지되었습니다.");
        }catch (DataAccessException dae) { // 데이터베이스 관련 예외 처리
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("데이터베이스 오류가 발생했습니다.");
        }catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("서버 내부 오류가 발생했습니다. 나중에 다시 시도해주세요.");
        }
    }

}
