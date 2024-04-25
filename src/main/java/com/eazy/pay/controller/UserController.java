package com.eazy.pay.controller;

import com.eazy.pay.dto.UserCardDTO;
import com.eazy.pay.service.UserCardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserCardService userCardService;

    @PostMapping("/card")
    public ResponseEntity<Map<String, String>> saveUserCard(@RequestBody UserCardDTO userCardDTO) {
        Map<String, String> responseMap = new HashMap<>();

        try {
            userCardService.createUserCard(userCardDTO);
            responseMap.put("message", "카드가 성공적으로 신청되었습니다.");
            return ResponseEntity.status(HttpStatus.CREATED).body(responseMap);
        } catch (DataAccessException dae) { // 데이터베이스 관련 예외 처리
            responseMap.put("error", "데이터베이스 오류가 발생했습니다.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseMap);
        } catch (Exception ex) { // 일반적인 예외 처리
            responseMap.put("error", "서버 내부 오류가 발생했습니다. 나중에 다시 시도해주세요.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseMap);
        }
    }
    @PatchMapping("/card/{userCardId}")
    public ResponseEntity<Map<String, String>> disableUserCard(@PathVariable Long userCardId) {
        Map<String, String> responseMap = new HashMap<>();
        try {
            userCardService.disableUserCard(userCardId);
            responseMap.put("message", "카드가 성공적으로 비활성화되었습니다.");
            return ResponseEntity.status(HttpStatus.OK).body(responseMap);
        } catch (DataAccessException dae) { // 데이터베이스 관련 예외 처리
            responseMap.put("error", "데이터베이스 오류가 발생했습니다.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseMap);
        } catch (Exception ex) { // 일반적인 예외 처리
            responseMap.put("error", "서버 내부 오류가 발생했습니다. 나중에 다시 시도해주세요.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseMap);
        }
    }

    // 사용자가 해당 카드를 소유하고 있고 카드가 활성화 상태인지 확인
    @GetMapping("/cards/check")
    public ResponseEntity<Map<String, String>> checkUserCard(@RequestParam("userId") Long userId, @RequestParam("cardId") Long cardId) {
        if (userId == null || cardId == null || userId <= 0 || cardId <= 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    Map.of("error", "사용자 ID와 카드 ID는 필수 입력값이며, 0보다 커야 합니다.")
            );
        }

        Map<String, String> responseMap = new HashMap<>();
        try {
            if (userCardService.checkUserCard(userId, cardId)) {
                responseMap.put("message", "해당 카드를 이미 소유하고 있습니다.");
                return ResponseEntity.status(HttpStatus.OK).body(responseMap);
            } else {
                responseMap.put("message", "해당 카드를 소유하고 있지 않습니다.");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseMap);
            }
        } catch (DataAccessException dae) { // 데이터베이스 관련 예외 처리
            responseMap.put("error", "데이터베이스 오류가 발생했습니다.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseMap);
        } catch (Exception ex) { // 일반적인 예외 처리
            responseMap.put("error", "서버 내부 오류가 발생했습니다. 나중에 다시 시도해주세요.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseMap);
        }
    }

}
