package com.eazy.pay.service;

import com.eazy.pay.dao.UserCardHistoryRepository;
import com.eazy.pay.dao.UserCardRepository;
import com.eazy.pay.model.CardBenefit;
import com.eazy.pay.model.UserCard;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PayService {
    @Autowired
    UserCardHistoryRepository userCardHistoryRepository;
    @Autowired
    UserCardRepository userCardRepository;
    public Object pay(Long userId){
        //지난달 첫날 (2024-03-1) :이번달 - 1개월의 첫째날
        LocalDate firstDay = LocalDate.now().minusMonths(1).withDayOfMonth(1);
        //지난달 마지막날 (2024-03-31) :이번달 첫째날 - 하루
        LocalDate lastDay = LocalDate.now().withDayOfMonth(1).minusDays(1);

        java.sql.Date start = java.sql.Date.valueOf(firstDay);
        java.sql.Date end = java.sql.Date.valueOf(lastDay);

        //실적 채운 카드
        List<UserCard> fulfilledUserCardList = new ArrayList<>();
        fulfilledUserCardList = userCardHistoryRepository.findFulfilledByUserIdAndDate(userId, start, end);

        //해당 카테고리의 혜택 여부 파악
        //혜택 순서대로 정렬
        List<UserCard> HavingBenefit = new ArrayList<>();
        List<UserCard> OrderByBenefit = new ArrayList<>();
        //TODO: 혜택 순서
        for(UserCard uc : fulfilledUserCardList){
            List<CardBenefit> cardBenefits = uc.getCard().getBenefitList();

            //이거는 '혜택'들을 정렬!
            //하나의 카드는 모두 다른 카테고리 종류의 혜택을 가지고 있을 거라 생각해서 필요 x
            OrderByBenefit = cardBenefits.stream().sorted(
                    Comparator.comparing(CardBenefit::getBenefitRate)
            ).toList();

        }
        //fulfilledUserCardList




        return fulfilledUserCardList;
    }
}
