package com.eazy.pay.service;

import com.eazy.pay.dao.CategoryRepository;
import com.eazy.pay.dao.PaymentHistoryRepository;
import com.eazy.pay.dao.UserCardHistoryRepository;
import com.eazy.pay.dao.UserCardRepository;
import com.eazy.pay.dto.CompletedPaymentDTO;
import com.eazy.pay.dto.PayRequestDTO;
import com.eazy.pay.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.time.LocalDate;
import java.util.*;

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

    public Object pay(PayRequestDTO requestdto){

        Long userId = requestdto.getUserId();
        Long categoryId = requestdto.getCategoryId();
        Integer price =requestdto.getPrice();
        String storeCode = requestdto.getStoreCode();
        String storeName =requestdto.getStoreName();

        // 결제에 사용할 카드 선택
        UserCard selectedCard = selectCardToPay(userId, categoryId, price);

        if(selectedCard != null){
            Card card = selectedCard.getCard();

            //카드에 대해 월별로 계산된 정보
            UserCardHistory history = userCardHistoryRepository.findByUserCardIdAndDate(selectedCard.getUid(), Date.valueOf(LocalDate.now().minusMonths(1).withDayOfMonth(1)));
            UserCardHistory thisMonthHistory = userCardHistoryRepository.findByUserCardIdAndDate(selectedCard.getUid(), Date.valueOf(LocalDate.now()));

            //즉시할인 금액 계산
            Integer discount = 0;
            if(history!=null){
                if(history.getIs_fulfilled()){ //전월실적 확인
                    //전월실적에 따른 할인율 적용
                    discount = price * (card.getBenefitList().stream()
                                                        .filter(cb -> cb.getCategory().getUid() == categoryId)
                                                        .mapToInt(CardBenefit::getBenefitRate)
                                                        .findFirst()
                                                        .orElse(0)) / 100;
                }
                // 혜택 한도 확인
                if(card.getBenefitLimit() <= (thisMonthHistory!=null ? thisMonthHistory.getBenefitAmount() : 0) + price){
                    //이대로 결제하면 할인한도가 초과될 때
                    //최대 혜택금 - 받은 혜택금이 이번 결제의 할인금이 된다.
                    discount = card.getBenefitLimit() - (thisMonthHistory!=null ? thisMonthHistory.getBenefitAmount() : 0);
                }

            }

            //결제금액 계산
            Integer paymentAmount = price - discount;

            // 카테고리 이름 가져오기
            String categoryName = "해당하는 카테고리가 없습니다.";
            Category category = categoryRepository.findById(categoryId).orElse(null);
            if(category != null){
                categoryName = category.getName();
            }else{
                return "NoAvailableCard";
            }

            //------------------------------------------결제 내역 저장--------------------------------------------------
            paymentHistoryRepository.save(PaymentHistory.builder()
                    .cardNum(selectedCard.getNum())
                    .paymentDate(new Date(System.currentTimeMillis()))
                    .paymentAmount(paymentAmount)
                    .storeCode(storeCode)
                    .storeName(storeName)
                    .categoryName(categoryName)
                    .benefitAmount(discount)
                    .build());

            if(thisMonthHistory != null){ // 이미 저장된 내역이 있을 경우 UserCardHistory 업데이트
                System.out.println(selectedCard.getUid());
                thisMonthHistory.setUseAmount(thisMonthHistory.getUseAmount() + price);
                thisMonthHistory.setBenefitAmount(thisMonthHistory.getBenefitAmount() + discount);
                thisMonthHistory.setIs_fulfilled(card.getPerformance() <= thisMonthHistory.getUseAmount() + paymentAmount ? true : false); //이번 결제로 실적 완성?
                userCardHistoryRepository.save(thisMonthHistory);

            }else{// 이미 저장된 내역이 없을 경우 UserCardHistory 생성
                UserCardHistory uch = UserCardHistory.builder()
                        .userCard(selectedCard)
                        .yearAndMonth(java.sql.Date.valueOf(LocalDate.now().withDayOfMonth(1))) //Date에 sql저장하면 날짜 정확하지 않은 문제 있어서 엔티티 필드를 sql.Date로 변경함.
                        .benefitAmount(discount)
                        .useAmount(price)
                        .is_fulfilled(card.getPerformance() <= price ? true : false)
                        .build();
                userCardHistoryRepository.save(uch);
            }

            // 결제 완료 정보 응답
            return CompletedPaymentDTO.builder()
                    .originalAmount(price)
                    .paidAmount(paymentAmount)
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

        //무실적이거나, "전월" 실적 채운 이번 결제 유효한 카드
        List<UserCard> fulfilledUserCardList = new ArrayList<>();
        fulfilledUserCardList = userCardHistoryRepository.findFulfilledByUserIdAndDate(userId, start, end, java.sql.Date.valueOf(LocalDate.now()), price);

        if (fulfilledUserCardList.size() == 0) {// 전월실적 채운 카드가 없을 때 (할인 되는 카드 x)

            List<UserCard> cardWithoutDiscountList = sortByRemainingPerformance(userId);
            return cardWithoutDiscountList.size() != 0 ? cardWithoutDiscountList.get(0) : null; // 채워야 할 실적이 가장 작은 카드 반환
        }

        //실적을 채워 할인 가능한 카드가 있다면
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

        if(HavingBenefit.size()==0){ //실적채운 카드는 있지만 해당 카테고리의 할인 혜택이 없을 때
            List<UserCard> cardWithoutDiscountList = sortByRemainingPerformance(userId);
            return cardWithoutDiscountList.size() !=0 ? cardWithoutDiscountList.get(0) : null; // 채워야 할 실적이 가장 작은 카드 반환
        }

        //혜택 한도를 고려한 할인 금액이 가장 큰 카드 순서대로 정렬
        List<UserCard> OrderByBenefit = new ArrayList<>(HavingBenefit.keySet());
        OrderByBenefit = sortByRemainingBenefit(OrderByBenefit, price, categoryId);


        for(UserCard uc : OrderByBenefit){
            // 예외처리 추가할 때 : 클라이언트로 전달하고 싶을 때 findFulfilledByUserIdAndDate쿼리 및 아래 부분 수정하면 됩니다.
            // (예 : 카드 한도 초과로 결제 불가능할 때, 카드 유효기간 만료로 결제 불가능할 때 등... -> 상태코드나 메시지를 클라이언트로 전달)
            if(uc.getExpirationDate().before(new java.util.Date())){continue;}
            return uc;
        }
        return null; //결제 가능한 카드가 없습니다.
    }

    //남은 실적에 따른 우선순위 정렬
    private List<UserCard> sortByRemainingPerformance(Long userId) {
        List<UserCard> cardList = userCardRepository.findByUserUid(userId);
        cardList.sort((uc1, uc2) -> {
            UserCardHistory history1 = userCardHistoryRepository.findByUserCardIdAndDate(uc1.getUid(), java.sql.Date.valueOf(LocalDate.now()));
            UserCardHistory history2 = userCardHistoryRepository.findByUserCardIdAndDate(uc2.getUid(), java.sql.Date.valueOf(LocalDate.now()));

            // 채워야 할 실적
            int remaining1 = uc1.getCard().getPerformance() - (history1 != null ? history1.getUseAmount() : 0);
            int remaining2 = uc2.getCard().getPerformance() - (history2 != null ? history2.getUseAmount() : 0);

            return Integer.compare(remaining1, remaining2); // 오름차순-(채워야 할 실적이 작은 것 부터)
        });

        return cardList;
    }

    // 받을 수 있는 혜택금액에 따른 우선순위 정렬
    private List<UserCard> sortByRemainingBenefit(List<UserCard> orderByBenefit, Integer price, Long categoryId) {
         /*
         *** (받을수 있는 혜택 금액 - 할인 금액) 순서로 정렬 ***
         * 받을 수 있는 혜택 금액 = (benefitAmount - useAmount)
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
