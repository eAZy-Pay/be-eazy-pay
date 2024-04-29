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
            Optional<UserCardHistory> lastMonthHistory = userCardHistoryRepository.findByUserCardIdAndDate(selectedCard.getUid(), getLastMonthDate());
            Optional<UserCardHistory> thisMonthHistory = userCardHistoryRepository.findByUserCardIdAndDate(selectedCard.getUid(), getDate());

            //즉시할인 금액 계산
            Integer discount = 0;
            if(lastMonthHistory.isPresent()) {

                UserCardHistory lmh = lastMonthHistory.get();
                // 이번달 동안 받은 혜택 금액의 합 (이번달 내역이 아직 없으면 0)
                Integer thisMonthBenefitAmount =  thisMonthHistory.isPresent()? thisMonthHistory.get().getBenefitAmount() : 0 ;
                // 카드가 가진 혜택 목록
                List<CardBenefit> benefitList = card.getBenefitList();

                if(lmh.getIs_fulfilled()){ //전월 실적달성 확인
                    //전월 실적에 따른 할인율 적용
                    double benefitRate = benefitList.stream()
                            .filter(cb -> cb.getCategory().getUid().equals(categoryId))
                            .map(CardBenefit::getBenefitRate)
                            .findFirst()
                            .orElse(0) / 100.0; // 할인율을 double로 변환
                    discount = (int) (price * benefitRate); // 할인 금액 계산
                }
                // 혜택 한도 확인
                if(card.getBenefitLimit() <= (thisMonthBenefitAmount + discount)){
                    //이대로 결제하면 할인 한도가 초과될 때
                    //최대 혜택금 - 받은 혜택금(혜택한도까지 남은 혜택)이 이번 결제의 페이백이 된다.
                    discount = card.getBenefitLimit() - thisMonthBenefitAmount;
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

            if(thisMonthHistory.isPresent()){ // 이미 저장된 내역이 있을 경우 UserCardHistory 업데이트
                UserCardHistory tmh = thisMonthHistory.get();
                tmh.setUseAmount(tmh.getUseAmount() + price);
                tmh.setBenefitAmount(tmh.getBenefitAmount() + discount);
                tmh.setIs_fulfilled(card.getPerformance() <= tmh.getUseAmount() + paymentAmount ? true : false); //이번 결제로 실적 완성?
                userCardHistoryRepository.save(tmh);

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
    private UserCard selectCardToPay(Long userId, Long categoryId, Integer price) {

        // ??? Repository에서 할 일 vs Service에서 할 일
        // Repository : 데이터베이스에 접근 - 필요한 데이터만 가공 (필터링)
        // Service : 비즈니스 로직을 처리 - 카드의 순서 배정 (정렬)


        /*
         * 1. 실적을 채운 카드 중 해당 결제의 페이백이 가장 높은 카드 선택
         *   1-1. 해당 결제의 카테고리에 해당하는 혜택을 가진
         *   1-2. 실적을 채운 보유 카드 조회
         *         = 보유 카드의(UserCard)-(Card)가 카테고리(Category)에 해당하는 혜택(Benefit)을 가지고있는 (BenefitList) 실적 채운 보유 카드 (userCardHistory)조회
         *   1-3. 혜택 한도를 고려한 할인 금액이 가장 큰 카드 반환
         *
         * 2. 페이백 가능한 카드가 없다면 보유한 모든 카드 중에서 실적이 가장 적게 남은 카드 선택
         *   1-1. 사용자의 모든 보유 카드 조회
         *   1-2. 채워야 할 실적이 가장 작은 카드 반환
         */

        //카드에 대한 혜택이 있는 유저 보유 카드 조회
        List<UserCard> userCardsHaveBenefit = userCardHistoryRepository.findHaveBenefitCardsByUserIdAndCategoryId(userId, categoryId, getDate());
        // 혜택 한도를 고려한 할인 금액이 가장 큰 카드 반환
        if (!userCardsHaveBenefit.isEmpty()) {

            // 카드 유효 체크
            Map<UserCard, String> availabilityChecked = checkavailableCards(userCardsHaveBenefit, price);
            // availabilityChecked의 entry value가 "ABAILABLE"인 카드만 필터링
            userCardsHaveBenefit = availabilityChecked.entrySet().stream()
                    .filter(entry -> entry.getValue().equals("AVAILABLE"))
                    .map(Map.Entry::getKey)
                    .toList();

            if (!userCardsHaveBenefit.isEmpty()) {
                //1. 페이백 받는 금액이 같다면, 실적 달성이 더 얼마 안남은 카드 순위 정렬
                userCardsHaveBenefit = sortUserCardsByRemainingPerformance(userCardsHaveBenefit);
                //2. 혜택 한도를 고려한 페이백 금액이 가장 큰 카드 순위 정렬
                userCardsHaveBenefit = sortByRemainingBenefit(userCardsHaveBenefit, price, categoryId);
                return userCardsHaveBenefit.get(0); // 최우선 순위 카드 반환
            }else{
                userCardsHaveBenefit = sortUserCardsByRemainingPerformance(userCardsHaveBenefit);
                return userCardsHaveBenefit.get(0); // 최우선 순위 카드 반환
            }


        } else { // 실적을 채우고 페이백 가능한 카드가 없다면

            // 혜택 없이 모든 카드 조회.
            List<UserCard> userCards = userCardRepository.findByUserId(userId);
            if (!userCards.isEmpty()) { // 보유한 카드가 있을 경우

                // 카드 유효 체크
                Map<UserCard, String> availabilityChecked = checkavailableCards(userCards, price);
                // availabilityChecked의 entry value가 "ABAILABLE"인 카드만 필터링
                userCards = availabilityChecked.entrySet().stream()
                        .filter(entry -> entry.getValue().equals("AVAILABLE"))
                        .map(Map.Entry::getKey)
                        .toList();

                if (!userCards.isEmpty()) {// 결제 가능 카드 보유
                    // 같이 혜택이 없는데 실적이 적게 남은 카드를 선택하는 것이 맞는지 의문
                        userCards = sortUserCardsByRemainingPerformance(userCards);
                        return userCards.get(0); // 최우선 순위 카드 반환
                }else {
                    return null; //결제 가능한 카드가 없음
                }

            }else {//보유한 카드가 없음
                return null;
            }
        }
    }//selectCardToPay()

    //남은 실적에 따른 우선순위 정렬
    private List<UserCard> sortUserCardsByRemainingPerformance(List<UserCard> cardList) {
        List<UserCard> sortedCardList = new ArrayList<>(cardList); // 입력 리스트를 변경하지 않고 새로운 리스트 생성
        sortedCardList.sort((uc1, uc2) -> {
            Optional<UserCardHistory> history1 = userCardHistoryRepository.findByUserCardIdAndDate(uc1.getUid(), getDate());
            Optional<UserCardHistory> history2 = userCardHistoryRepository.findByUserCardIdAndDate(uc2.getUid(), getDate());

            // 채워야 할 실적
            int remaining1 = uc1.getCard().getPerformance() - (history1.isPresent() ? history1.get().getUseAmount() : 0);
            int remaining2 = uc2.getCard().getPerformance() - (history2.isPresent() ? history2.get().getUseAmount() : 0);

            if(remaining1 >0 && remaining2 > 0){ // 둘 다 양수일때
                return Integer.compare(remaining1, remaining2); // 오름차순-(채워야 할 실적이 작은 것 부터)
            } else if ((remaining1 > 0 && remaining2 <= 0) || (remaining2>0 && remaining1<=0) ) {
                return Integer.compare(remaining2, remaining1); // 내림차순-(채워지지 않은 실적 먼저)
            }else {
                return 0; // 둘 다 0이면 순서 유지
            }
        });

        return sortedCardList;
    }

    // 받을 수 있는 혜택금액에 따른 우선순위 정렬
    private List<UserCard> sortByRemainingBenefit(List<UserCard> cardList, Integer price, Long categoryId) {
         /*
         *** (받을수 있는 혜택 금액 - 할인 금액) 순서로 정렬 ***
         * 받을 수 있는 혜택 금액 = (benefitAmount - useAmount)
         * */

        List<UserCard> sortedCardList = new ArrayList<>(cardList); // 입력 리스트를 변경하지 않고 새로운 리스트 생성 (컬렉션 뷰 수정으로 인한 UnsupportedOperationException 회피)
        sortedCardList.sort((uc1, uc2) -> {
            //카드 1의 할인 금액
            Integer discount1 = price * (uc1.getCard().getBenefitList().stream()
                    .filter(cb -> cb.getCategory().getUid().equals(categoryId))
                    .mapToInt(CardBenefit::getBenefitRate)
                    .findFirst()
                    .orElse(0) / 100);

            //카드 2의 할인 금액
            Integer discount2 = price * (uc2.getCard().getBenefitList().stream()
                    .filter(cb -> cb.getCategory().getUid().equals(categoryId))
                    .mapToInt(CardBenefit::getBenefitRate)
                    .findFirst()
                    .orElse(0) / 100);

            // 각 카드의 이번 달 사용량 정보
            Optional<UserCardHistory> history1 = userCardHistoryRepository.findByUserCardIdAndDate(uc1.getUid(), getDate());
            Optional<UserCardHistory> history2 = userCardHistoryRepository.findByUserCardIdAndDate(uc2.getUid(), getDate());

            // Default values in case of null history
            int remainingBenefit1 = (history1.isPresent() ? history1.get().getBenefitAmount() - history1.get().getUseAmount() : 0) - discount1;
            int remainingBenefit2 = (history2.isPresent() ? history2.get().getBenefitAmount() - history2.get().getUseAmount() : 0) - discount2;

            return Integer.compare(remainingBenefit2, remainingBenefit1); // 내림차순 (혜택은 혜택 최대치를 넘을 수 없기 때문에 남은 혜택은 항상 양수)
        });

        return sortedCardList;
    }

    /*  예외 처리
            (jpa 쿼리로 구현할 수 있지만 service 단에서 처리할 경우,
            메서드를 수정해 클라이언트에게 각 카드별 결제 실패 원인을 제공해 수 있음)
            1. 정지된 카드 (만료 카드)
            2. 한도 초과 카드

        유저 카드와, 결제 가능 또는 결제 불가 사유 Map반환
    */
    private Map<UserCard, String> checkavailableCards(List<UserCard> userCards, Integer price){
        Map<UserCard, String> userCardWithPaymentAvailableStatus = new HashMap<>();
        Map<UserCard, String> tempMap = new HashMap<>();
        for (UserCard uc : userCards) {

            Optional<UserCardHistory> thisMonthHistory =  userCardHistoryRepository.findByUserCardIdAndDate(uc.getUid(), getDate());
            if(!uc.isCardValid()){
                //1. 정지된 카드 (만료된 카드)
                tempMap.put(uc, "INVALID");
            } else if (uc.getPaymentLimit() < ( (thisMonthHistory.isPresent() ? thisMonthHistory.get().getUseAmount() : 0) + price) ){
                //2. 한도 초과 카드
                tempMap.put(uc, "MAXED_OUT");
            } else {
                tempMap.put(uc, "AVAILABLE");
            }
        }
        // 기존 맵 업데이트
        userCardWithPaymentAvailableStatus.putAll(tempMap);

        return userCardWithPaymentAvailableStatus;
    }

    //현재 시간을 기반으로 한 Date 반환
    private java.sql.Date getDate(){
        return new java.sql.Date(System.currentTimeMillis());
    }
    private java.sql.Date getLastMonthDate(){
        //지난 달의 Date 반환
        return java.sql.Date.valueOf(LocalDate.now().minusMonths(1).withDayOfMonth(1));
    }
}
