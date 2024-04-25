package com.eazy.pay.controller;

import com.eazy.pay.dto.BenefitAndSimpleUserCardsDTO;
import com.eazy.pay.dto.CardDTO;
import com.eazy.pay.dto.CardWithBenefitDTO;
import com.eazy.pay.service.CardBenefitService;
import com.eazy.pay.service.CardService;
import com.eazy.pay.service.UserCardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/cards")
public class CardController {
    @Autowired
    private CardBenefitService cardBenefitService;
    @Autowired
    private  CardService cardService;
    @Autowired
    private UserCardService userCardService;

    @GetMapping
    public Page<CardDTO> getCards(
            // Optional을 사용하여 값이 없을 때를 대비
            @RequestParam(value = "category_id", required = false) Optional<Long> categoryId,
            @RequestParam(value = "name", required = false) Optional<String> name,
            // PageableDefault를 사용하여 기본값 설정, size는 한 페이지에 보여줄 개수
            @PageableDefault(size = 10) Pageable pageable
            ) {

        //orElse: 파라미터로 값을 받는다.
        //orElseGet: 파라미터로 함수형 인터페이스(함수)를 받는다.
        return categoryId.map(id -> cardBenefitService.getCardsByCategoryId(id, pageable)) // categoryId가 있는 경우 해당하는 카드 반환
                .orElseGet(() -> // categoryId가 없는 경우 name이 있는지 확인
                        name.map(n -> cardService.getCardsLikeName(n, pageable)) // name이 있는 경우 해당하는 카드 반환
                                .orElseGet(() -> cardService.getAllCards(pageable))); // name이 없는 경우 모든 카드 반환
    }
    @GetMapping("/simple-user-card-benefit-performance")
    public BenefitAndSimpleUserCardsDTO getBenefitSimple(
            @RequestParam("user_id") Long userId,
            @RequestParam("month") int month
    ) {
        return userCardService.getSimpleBenefitDashboardByUserId(userId, month);
    }


    @GetMapping("/{cardId}")
    public CardWithBenefitDTO getCardWithBenefitByCardId(@PathVariable("cardId") Long cardId) {
        return cardService.getCardWithBenefitByCardId(cardId);
    }

    @PostMapping
    public CardDTO createCard(@RequestBody CardDTO cardDTO) {
        return cardService.createCard(cardDTO);
    }

    @DeleteMapping("/{cardId}")
    public ResponseEntity<String> deleteCard(@PathVariable("cardId") Long cardId) {
        try {
            cardService.deleteCard(cardId);
            return ResponseEntity.ok("카드가 성공적으로 삭제되었습니다.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("삭제할 카드가 없습니다.");
        }
    }

    @PatchMapping("/{cardId}")
    public CardDTO updateCard(@PathVariable("cardId") Long cardId, @RequestBody CardDTO cardDTO) {
        return cardService.updateCard(cardId, cardDTO);
    }

}
