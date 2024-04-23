package com.eazy.pay.controller;

import com.eazy.pay.dto.BenefitAndSimpleUserCardsDTO;
import com.eazy.pay.dto.CardDTO;
import com.eazy.pay.dto.CardWithBenefitDTO;
import com.eazy.pay.service.CardBenefitService;
import com.eazy.pay.service.CardService;
import com.eazy.pay.service.UserCardService;
import org.springframework.beans.factory.annotation.Autowired;
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
    public List<CardDTO> getCards(
            // Optional을 사용하여 값이 없을 때를 대비
            @RequestParam(value = "category_id", required = false) Optional<Long> categoryId,
            @RequestParam(value = "name", required = false) Optional<String> name) {

        //orElse: 파라미터로 값을 받는다.
        //orElseGet: 파라미터로 함수형 인터페이스(함수)를 받는다.
        return categoryId.map(cardBenefitService::getCardsByCategoryId) // map()을 사용하여 categoryId 값이 있는 경우에만 실행
                .orElseGet(() -> // categoryId가 없는 경우 name이 있는지 확인
                        name.map(cardService::getCardsLikeName) // name이 있는 경우 해당하는 카드 반환
                                .orElseGet(cardService::getAllCards)); // name이 없는 경우 모든 카드 반환
    }
    @GetMapping("/simple-user-card-benefit-performance")public BenefitAndSimpleUserCardsDTO getBenefitSimple(@RequestParam("user_id") Long userId){    //최대 4개 카드 선택 (아직 정렬 x)
        return userCardService.getSimpleBenefitDashboardByUserId(userId);
    }

    @GetMapping("/{cardId}")
    public CardWithBenefitDTO getCardWithBenefitByCardId(@PathVariable("cardId") Long cardId) {
        return cardService.getCardWithBenefitByCardId(cardId);
    }
}
