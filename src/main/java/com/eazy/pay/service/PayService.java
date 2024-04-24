package com.eazy.pay.service;

import com.eazy.pay.dao.UserCardHistoryRepository;
import com.eazy.pay.dao.UserCardRepository;
import com.eazy.pay.dto.CompletedPaymentDTO;
import com.eazy.pay.model.CardBenefit;
import com.eazy.pay.model.User;
import com.eazy.pay.model.UserCard;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PayService {
    @Autowired
    UserCardHistoryRepository userCardHistoryRepository;
    @Autowired
    UserCardRepository userCardRepository;
    public Object pay(Long userId, Long categoryId, Integer price){
        UserCard selectedCard = selectCardToPay(userId, categoryId, price);

        if(selectedCard != null){
            Integer discount = price * (selectedCard.getCard().getBenefitList().stream()
                                                .filter(cb -> cb.getCategory().getUid() == categoryId)
                                                .mapToInt(CardBenefit::getBenefitRate)
                                                .findFirst()
                                                .orElse(0)) / 100;


            return CompletedPaymentDTO.builder()
                    .originalAmount(price)
                    .paidAmount(price - discount)
                    .discount(discount)
                    .cardImage(selectedCard.getCard().getImage())
                    .cardName(selectedCard.getCard().getName())
                    .build();
        }
        return "NoAvailableCard";
    }

    private UserCard selectCardToPay(Long userId, Long categoryId, Integer price){
        //지난달 첫날 (2024-03-1) :이번달 - 1개월의 첫째날
        LocalDate firstDay = LocalDate.now().minusMonths(1).withDayOfMonth(1);
        //지난달 마지막날 (2024-03-31) :이번달 첫째날 - 하루
        LocalDate lastDay = LocalDate.now().withDayOfMonth(1).minusDays(1);

        java.sql.Date start = java.sql.Date.valueOf(firstDay);
        java.sql.Date end = java.sql.Date.valueOf(lastDay);

        //실적 채운 한도초과 안될 카드
        List<UserCard> fulfilledUserCardList = new ArrayList<>();
        fulfilledUserCardList = userCardHistoryRepository.findFulfilledByUserIdAndDate(userId, start, end, java.sql.Date.valueOf(LocalDate.now()), price);

        //해당 카테고리의 혜택 여부 파악
        Map<UserCard, Integer> HavingBenefit = new HashMap<>();

        for(UserCard uc : fulfilledUserCardList){
            List<CardBenefit> cardBenefits = uc.getCard().getBenefitList();

            //해당 카테고리의 혜택이 있는 카드만 추출 및 할인율 저장
            for(CardBenefit cb : cardBenefits){
                if(cb.getCategory().getUid() == categoryId){
                    HavingBenefit.put(uc, cb.getBenefitRate());
                    break;//(카드상품 하나당 할인 카테고리 중복 없어서 탐색 끝)
                }
            }
        }

        //혜택 순서대로 내림차순 정렬
        List<UserCard> OrderByBenefit = new ArrayList<>(HavingBenefit.keySet());
        OrderByBenefit.sort(Comparator.comparingInt(HavingBenefit::get).reversed());


        for(UserCard uc : OrderByBenefit){
            // 예외처리 클라이언트로 전달하고 싶을 때 findFulfilledByUserIdAndDate쿼리 및 아래 부분 수정
            // (예 : 카드 한도 초과로 결제 불가능할 때, 카드 유효기간 만료로 결제 불가능할 때 등)
            if(uc.getExpirationDate().before(new java.util.Date())){continue;}
            return uc;
        }
        return null;
    }
}
