package com.eazy.pay.service;

import com.eazy.pay.dao.CategoryRepository;
import com.eazy.pay.dao.PaymentHistoryRepository;
import com.eazy.pay.dao.UserCardHistoryRepository;
import com.eazy.pay.dao.UserCardRepository;
import com.eazy.pay.dto.CompletedPaymentDTO;
import com.eazy.pay.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.time.Instant;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PayService {
    @Autowired
    UserCardHistoryRepository userCardHistoryRepository;
    @Autowired
    UserCardRepository userCardRepository;
    @Autowired
    PaymentHistoryRepository paymentHistoryRepository;
    @Autowired
    CategoryRepository categoryRepository;


    public Object pay(Long userId, Long categoryId, Integer price, String storeCode, String storeName){
        UserCard selectedCard = selectCardToPay(userId, categoryId, price);
        if(selectedCard != null){
            Card card = selectedCard.getCard();
            UserCardHistory history = userCardHistoryRepository.findByUserCardIdAndDate(selectedCard.getUid(), Date.valueOf(LocalDate.now()));

            Integer discount = 0;
            if(history!=null){
                if(history.getIs_fulfilled()){
                    discount = price * (card.getBenefitList().stream()
                                                        .filter(cb -> cb.getCategory().getUid() == categoryId)
                                                        .mapToInt(CardBenefit::getBenefitRate)
                                                        .findFirst()
                                                        .orElse(0)) / 100;
                }
                if(history.getUseAmount() + price > selectedCard.getPaymentLimit()) { //한도초과 확인
                    if(card.getBenefitLimit() <= history.getBenefitAmount() + price){ // 혜택 한도 확인
                        //이번에 결제하면 할인한도 초과되어 결제
                        // 최대 혜택금 - 받은 혜택금이 이번 결제의 할인금이 된다.
                        discount = card.getBenefitLimit() - history.getBenefitAmount();
                    }
                }
            }

            Integer paymentAmount = price - discount;

            String categoryName = "해당하는 카테고리가 없습니다.";
            Category category = categoryRepository.findById(categoryId).orElse(null);
            if(category != null){
                categoryName = category.getName();
            }

            //--------------------------------------------------------------------------------------------
            // 결제 내역 저장
            paymentHistoryRepository.save(PaymentHistory.builder()
                    .cardNum(selectedCard.getNum())
                    .paymentDate(new Date(System.currentTimeMillis()))
                    .paymentAmount(paymentAmount)
                    .storeCode(storeCode)
                    .storeName(storeName)
                    .categoryName(categoryName)
                    .benefitAmount(discount)
                    .build());

            if(history != null){ // 이미 저장된 내역이 있을 경우 UserCardHistory 업데이트
                history.setUseAmount(history.getUseAmount() + price);
                history.setBenefitAmount(history.getBenefitAmount() + discount);
                userCardHistoryRepository.save(history);

            }else{// 이미 저장된 내역이 없을 경우 UserCardHistory ROW 생성
                UserCardHistory uh = UserCardHistory.builder()
                        .userCard(selectedCard)
                        //.yearAndMonth(Date.valueOf(LocalDate.now().withDayOfMonth(1)))
                        .yearAndMonth(java.sql.Date.valueOf(LocalDate.now().withDayOfMonth(1)))
                        .benefitAmount(discount)
                        .useAmount(price)
                        .is_fulfilled(card.getPerformance() <= price ? true : false)
                        .build();
                userCardHistoryRepository.save(uh);
            }

            return CompletedPaymentDTO.builder()
                    .originalAmount(price)
                    .paidAmount(paymentAmount) //누적값으로 변경
                    .discount(discount)
                    .cardImage(card.getImage())
                    .cardName(card.getName())
                    .build();
        }

        return "NoAvailableCard";
    }

    //유저의 카드 중 결제에 사용할 카드 선택
    private UserCard selectCardToPay(Long userId, Long categoryId, Integer price){
        //지난달 첫날 (2024-03-1) :이번달 - 1개월의 첫째날
        LocalDate firstDay = LocalDate.now().minusMonths(1).withDayOfMonth(1);
        //지난달 마지막날 (2024-03-31) :이번달 첫째날 - 하루
        LocalDate lastDay = LocalDate.now().withDayOfMonth(1).minusDays(1);

        java.sql.Date start = java.sql.Date.valueOf(firstDay);
        java.sql.Date end = java.sql.Date.valueOf(lastDay);

        //무실적이거나, 실적 채운 한도초과 안될 유효한 카드
        List<UserCard> fulfilledUserCardList = new ArrayList<>();
        fulfilledUserCardList = userCardHistoryRepository.findFulfilledByUserIdAndDate(userId, start, end, java.sql.Date.valueOf(LocalDate.now()), price);
        if(fulfilledUserCardList != null){//실적 채운 카드가 없을 때
            List<UserCard> uc = userCardRepository.findByUserId(userId);
            //실적이 얼마 남지 않은 카드 순서로 정렬
            uc.sort((uc1, uc2) -> {
                UserCardHistory history1 = userCardHistoryRepository.findByUserCardIdAndDate(uc1.getUid(), java.sql.Date.valueOf(LocalDate.now()));
                UserCardHistory history2 = userCardHistoryRepository.findByUserCardIdAndDate(uc2.getUid(), java.sql.Date.valueOf(LocalDate.now()));

                int remaining1 = uc1.getCard().getPerformance() - (history1 != null ? history1.getUseAmount() : 0);
                int remaining2 = uc2.getCard().getPerformance() - (history2 != null ? history2.getUseAmount() : 0);

                return Integer.compare(remaining1, remaining2); // 오름차순
            });
            fulfilledUserCardList = uc;
        }
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

        //혜택 한도를 고려한 할인 금액이 가장 큰 카드 순서대로 정렬
        List<UserCard> OrderByBenefit = new ArrayList<>(HavingBenefit.keySet());
        OrderByBenefit = sortByRemainingBenefit(OrderByBenefit, price, categoryId);


        for(UserCard uc : OrderByBenefit){
            // 예외처리 클라이언트로 전달하고 싶을 때 findFulfilledByUserIdAndDate쿼리 및 아래 부분 수정
            // (예 : 카드 한도 초과로 결제 불가능할 때, 카드 유효기간 만료로 결제 불가능할 때 등)
            if(uc.getExpirationDate().before(new java.util.Date())){continue;}
            return uc;
        }
        return null;
    }

    public List<UserCard> sortByRemainingBenefit(List<UserCard> orderByBenefit, Integer price, Long categoryId) {
        //2.
        /*
         * 남은 받을 수 있는 혜택금액 < 할인 금액 : 할인한도 초과할 수 없음 = 최대로 할인받지 못하는 경우일 수 있음
         * 남은 받을 수 있는 햬택 금액 > 할인 금액 : 일단 할인 금엑 다 할인 받을 수 있음
         *
         * *** 받을수 있는 혜택 금액 - 할인 금액 순서로 정렬 ***
         * 받을 수 있는 혜택 금액 = benefitAmount - useAmount
         * */


        orderByBenefit.sort((uc1, uc2) -> {
            //카드 1의 할인 금액
            Integer discount1 = price * (uc1.getCard().getBenefitList().stream()
                    .filter(cb -> cb.getCategory().getUid() == categoryId)
                    .mapToInt(CardBenefit::getBenefitRate)
                    .findFirst()
                    .orElse(0)) / 100;

            //카드 2의 할인 금액
            Integer discount2 = price * (uc1.getCard().getBenefitList().stream()
                    .filter(cb -> cb.getCategory().getUid() == categoryId)
                    .mapToInt(CardBenefit::getBenefitRate)
                    .findFirst()
                    .orElse(0)) / 100;

            // 각 카드의 이번 달 사용량 정보
            UserCardHistory history1 = userCardHistoryRepository.findByUserCardIdAndDate(uc1.getUid(), java.sql.Date.valueOf(LocalDate.now()));
            UserCardHistory history2 = userCardHistoryRepository.findByUserCardIdAndDate(uc2.getUid(), java.sql.Date.valueOf(LocalDate.now()));

            // Default values in case of null history
            int remainingBenefit1 = (history1 != null ? history1.getBenefitAmount() - history1.getUseAmount() : 0) - discount1;
            int remainingBenefit2 = (history2 != null ? history2.getBenefitAmount() - history2.getUseAmount() : 0) - discount2;

            return Integer.compare(remainingBenefit2, remainingBenefit1); // 내림차순
        });

        return orderByBenefit;
    }
}
