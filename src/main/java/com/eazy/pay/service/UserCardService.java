package com.eazy.pay.service;

import com.eazy.pay.dao.PayBenefitRepository;
import com.eazy.pay.dto.SimpleUserCardDTO;
import com.eazy.pay.model.Card;
import com.eazy.pay.model.PayBenefit;
import com.eazy.pay.model.User;
import com.eazy.pay.model.UserCard;
import com.eazy.pay.dao.UserCardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserCardService {

    @Autowired
    private UserCardRepository userCardRepository;
    @Autowired
    private PayBenefitRepository payBenefitRepository;

    public List<UserCard> getAllUserCards() {
        return userCardRepository.findAll();
    }

    // UserId로 자신의 보유 카드 모두가져오기
    public List<UserCard> getUserCardsByUserId(Long userId) {return userCardRepository.findByUserId(userId);}


    // 자신의 모든 보유 카드 혜택을 합치고, 4개의 카드상품 정보 가져오기
    public SimpleUserCardDTO getSimpleBenefitByUserId(Long userId) {

        List<UserCard> userCards = userCardRepository.findByUserId(userId); //자신의 모든 보유 카드 가져오기 (혜택 순서로 정렬 구현 X)
        int benefitAmount = 0;
        List<Card> cards = new ArrayList<>();

        for(UserCard uc : userCards){ //자신의 모든 보유 카드 마다
            Card card = uc.getCard(); //카드 상품 정보 가져오기

            if(cards.size() < 4) {// 4개 이하일때만
                cards.add(card); //카드 상품 정보 추가
            }

            String cardNum = uc.getNum(); //카드번호 가져오기
            //해당 보유 카드의 최근3개월 모든 거래내역
            List<PayBenefit> payBenefitsFor3  =
                    payBenefitRepository.findByCardNumAndDateWithinDate(cardNum,
                            Timestamp.valueOf(LocalDate.now().minusMonths(3).atStartOfDay())
                    ).orElse(null);

            for (PayBenefit pb : payBenefitsFor3){
                benefitAmount += pb.getBenefitAmount(); //해당 내역을 총 혜택으로 합산
                if(benefitAmount >= card.getBenefitLimit()){ benefitAmount = card.getBenefitLimit(); break;}
                // 합계가 해당 카드의 혜택 최대값을 넘으면 break.
            }

        }
        return SimpleUserCardDTO.builder()
                .benefitAmount(benefitAmount)
                .cards(cards)
                .build();
    }
}
