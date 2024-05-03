package com.eazy.pay.controller;

import com.eazy.pay.dto.RecommendResponseDTO;
import com.eazy.pay.dto.UserCardDTO;
import com.eazy.pay.dto.UserCardHistoryDTO;
import com.eazy.pay.model.User;
import com.eazy.pay.dto.ValidUserCardDTO;
import com.eazy.pay.model.UserCard;
import com.eazy.pay.service.RecommendationService;
import com.eazy.pay.service.UserCardHistoryService;
import com.eazy.pay.service.UserCardService;
import com.eazy.pay.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserCardService userCardService;

    @Autowired
    private RecommendationService recommendationService;

    @Autowired
    private UserCardHistoryService userCardHistoryService;

    @Autowired
    private UserService userService;


    @GetMapping
    public ResponseEntity<User> getUserInfo(@RequestParam("user_id") Long userId) {
        Optional<User> userOptional = userService.getUserById(userId);
        return userOptional
                .map(user -> ResponseEntity.ok().body(user))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/card")
    public ResponseEntity<List<UserCard>> getUserCard(@RequestParam("user_id") Long userId) {
        List<UserCard> userCards = userCardService.getUserCardsByUserId(userId);
        return ResponseEntity.ok(userCards);
    }

    @DeleteMapping("/card")
    public ResponseEntity<String> deleteUserCard(@RequestParam("userCardId") Long userCardId) {
        userCardService.deleteUserCard(userCardId);
        return ResponseEntity.ok("카드가 성공적으로 삭제되었습니다.");
    }

    @GetMapping("/select-card")
    public UserCard selectUserCard(@RequestParam("uid") Long uid) {
        return userCardService.getUserCardByUid(uid);
    }

    @PostMapping("/card")
    public ResponseEntity<Map<String, String>> saveUserCard(@RequestBody UserCardDTO userCardDTO) {
        Map<String, String> responseMap = new HashMap<>();

        try {
            UserCard userCard = userCardService.createUserCard(userCardDTO);
            Date date = new Date(System.currentTimeMillis());
            Date firstDayOfMonth = Date.valueOf(date.toLocalDate().withDayOfMonth(1));
            Date lastMonthDate = Date.valueOf(date.toLocalDate().minusMonths(1).withDayOfMonth(1));

//            // 당월, 전월의 실적 채움
//            userCardHistoryService.saveUserCardHistory(UserCardHistoryDTO.builder()
//                    .userCardId(userCard.getUid())
//                    .yearAndMonth(firstDayOfMonth)
//                    .isFulfilled(true)
//                    .build());
//
//            userCardHistoryService.saveUserCardHistory(UserCardHistoryDTO.builder()
//                    .userCardId(userCard.getUid())
//                    .yearAndMonth(lastMonthDate)
//                    .isFulfilled(true)
//                    .build());

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

    @GetMapping("/card/fulfilled")
    public boolean getUserCardHistoriesFulfilled(@RequestParam("userCardId") Long userCardId) {
        return userCardHistoryService.getUserCardHistoryFulfilled(userCardId);
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

    @PatchMapping("card/toggle-valid/{userCardId}")
    public ResponseEntity<Map<String,String>> toggleUserCardValidity(@PathVariable("userCardId") Long userCardId) {
        userCardService.toggleCardValidity(userCardId);

        // JSON 형식으로 응답을 반환
        Map<String, String> response = new HashMap<>();
        response.put("message", "Card validity toggled successfully.");
        return ResponseEntity.ok(response);
    }

    @PatchMapping("card/toggle-link/{userCardId}")
    public ResponseEntity<Map<String,String>> toggleUserCardLink(@PathVariable("userCardId") Long userCardId) {
        userCardService.toggleCardLink(userCardId);

        // JSON 형식으로 응답을 반환
        Map<String, String> response = new HashMap<>();
        response.put("message", "Card Link toggled successfully.");
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/card/toggle-payment-limit/{userCardId}")
    public ResponseEntity<?> toggleUserCardPaymentLimit(
            @PathVariable("userCardId") Long userCardId,
            @RequestBody Map<String, Integer> requestBody
    ) {
        try {
            int paymentLimit = requestBody.get("paymentLimit"); // 본문에서 paymentLimit 추출
            userCardService.togglePaymentLimit(userCardId, paymentLimit);

            // JSON 형식으로 성공 응답 반환
            return ResponseEntity.ok(Map.of("message", "Card payment limit updated successfully."));
        } catch (IllegalArgumentException e) {
            // 요청한 한도 변경이 허용되지 않을 때
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (RuntimeException e) {
            // 카드를 찾을 수 없거나 다른 오류
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Card not found"));
        }
    }



    // 사용자가 해당 카드를 소유하고 있고 카드가 활성화 상태인지 확인
    @GetMapping("/cards/check")
    public ResponseEntity<Map<String, String>> checkUserCard(@RequestParam("user_id") Long userId,
            @RequestParam("card_id") Long cardId) {
        if (userId == null || cardId == null || userId <= 0 || cardId <= 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    Map.of("error", "사용자 ID와 카드 ID는 필수 입력값이며, 0보다 커야 합니다."));
        }

        Map<String, String> responseMap = new HashMap<>();
        try {
            if (userCardService.checkUserCard(userId, cardId)) {
                responseMap.put("message", "해당 카드를 이미 소유하고 있습니다.");
                return ResponseEntity.status(HttpStatus.OK).body(responseMap);
            } else {
                responseMap.put("message", "해당 카드를 소유하고 있지 않습니다.");
                return ResponseEntity.status(255).body(responseMap);
            }
        } catch (DataAccessException dae) { // 데이터베이스 관련 예외 처리
            responseMap.put("error", "데이터베이스 오류가 발생했습니다.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseMap);
        } catch (Exception ex) { // 일반적인 예외 처리
            responseMap.put("error", "서버 내부 오류가 발생했습니다. 나중에 다시 시도해주세요.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseMap);
        }
    }


    @GetMapping("/card-usage-summary")
    public ResponseEntity<Map<String, Object>> getUserCardUsageSummary(@RequestParam("user_id") Long userId) {
        Map<String, Object> responseMap = new HashMap<>();
        try {
            responseMap.put("data", userCardService.getCardUsageSummary(userId));
            return ResponseEntity.status(HttpStatus.OK).body(responseMap);
        } catch (DataAccessException dae) { // 데이터베이스 관련 예외 처리
            responseMap.put("error", "데이터베이스 오류가 발생했습니다.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseMap);
        } catch (Exception ex) { // 일반적인 예외 처리
            responseMap.put("error", "서버 내부 오류가 발생했습니다. 나중에 다시 시도해주세요.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseMap);
        }
    }

    @GetMapping("/recommendation/card")
    public RecommendResponseDTO getRecommendationCard(@RequestParam(value = "user_id", required = false) Long userId) {
        if(userId == null || userId <= 0) {
            return recommendationService.getRecommendation();
        }
        return recommendationService.getRecommendation(userId);
    }

    @GetMapping("/sorted-valid-cards") // TEST
    public List<ValidUserCardDTO> getValidUserCards(@RequestParam("user_id") Long userUid){
        List<ValidUserCardDTO> validUserCards = userCardService.getValidUserCardsByUserId(userUid);
//         TODO: validUserCards를 결제 알고리즘에 따라 순위를 매겨 정렬한 결과 리스트를 리턴하기
//         TODO: ValidUserCardDTO에 예상 혜택 필드 추가
        return validUserCards;
    }

    @GetMapping("/notification")
    public ResponseEntity<Map<String, Object>> getNotification(@RequestParam("user_id") Long userId,
                                                               @RequestParam(value = "active_read", required = false) Optional<Boolean> activeRead
    ) {
        Map<String, Object> responseMap = new HashMap<>();
        try {
            if (activeRead.isPresent()) {
                responseMap.put("data", userService.getNotifications(userId, activeRead.get()));
            } else {
                responseMap.put("data", userService.getNotifications(userId));
            }
            return ResponseEntity.status(HttpStatus.OK).body(responseMap);
        } catch (DataAccessException dae) { // 데이터베이스 관련 예외 처리
            responseMap.put("error", "데이터베이스 오류가 발생했습니다.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseMap);
        } catch (Exception ex) { // 일반적인 예외 처리
            responseMap.put("error", "서버 내부 오류가 발생했습니다. 나중에 다시 시도해주세요.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseMap);
        }
    }
}
