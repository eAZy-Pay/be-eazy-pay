package com.eazy.pay.controller;

import com.eazy.pay.dto.CardDTO;
import com.eazy.pay.dto.SimpleUserCardDTO;
import com.eazy.pay.model.Card;
import com.eazy.pay.model.PayBenefit;
import com.eazy.pay.model.UserCard;
import com.eazy.pay.service.CardBenefitService;
import com.eazy.pay.service.CardService;
import com.eazy.pay.service.PayBenefitService;
import com.eazy.pay.service.UserCardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
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
    @Autowired
    private PayBenefitService payBenefitService;

    @GetMapping
    public List<CardDTO> getCards(
            // Optional을 사용하여 값이 없을 때를 대비
            @RequestParam(value = "category_id", required = false) Optional<Long> categoryId,
            @RequestParam(value = "name", required = false) Optional<String> name) {
        // isPresent()를 사용하여 값이 있는지 확인
        if (categoryId.isPresent()) {
            return cardBenefitService.getCardsByCategoryId(categoryId.get());
        } else if (name.isPresent()) {
            return cardService.getCardsLikeName(name.get());
        } else {
            return cardBenefitService.getAllCards();  // 모든 카드를 반환, 나중에 수정 필요
        }
    }

    @PostMapping("/simple-benefit-dashboard")
    public SimpleUserCardDTO getMainBenner(@RequestBody Long userId){
        //최대 4개 카드 선택 (아직 정렬 x)
        List<UserCard> userCards = userCardService.getUserCardsByUserIdWithLimit4(userId);

        int benefitAmount = 0;
        List<Card> cards = new ArrayList<>();
        for(UserCard uc : userCards){
            Card card = uc.getCard();
            cards.add(card);

            String cardNum = uc.getNum();
            //카드들의 최근N개월 모든 거래내역 -> 합계가 혜택 최대값을 넘으면 break.
            List<PayBenefit> payBenefitsFor3  = payBenefitService.getPayBenefitsForNMonthByCardNum(cardNum, 3);
            for (PayBenefit pb : payBenefitsFor3){
                benefitAmount += pb.getBenefitAmount();
                if(benefitAmount >= card.getBenefitLimit()){ benefitAmount = card.getBenefitLimit(); break;}
            }

        }
        return SimpleUserCardDTO.builder()
                .benefitAmount(benefitAmount)
                .cards(cards)
                .build();
    }

}
