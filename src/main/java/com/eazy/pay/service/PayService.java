package com.eazy.pay.service;

import com.eazy.pay.dao.*;
import com.eazy.pay.dto.*;
import com.eazy.pay.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
    @Autowired
    UserCategoryHistoryRepository userCategoryHistoryRepository;
    @Autowired
    NotificationRepository notificationRepository;

    // 사용자가 선택한 카드의 UID로 결제
    public Object userPay(PayUserRequestDTO requestDTO){
        Long userId = requestDTO.getUserId();
        Long cardId = requestDTO.getCardId();
        Long categoryId = requestDTO.getCategoryId();
        Integer price =requestDTO.getPrice();
        String storeCode = requestDTO.getStoreCode();
        String storeName =requestDTO.getStoreName();

        //결제에 사용할 카드
        Optional<UserCard> selectedCardByUser = userCardRepository.findById(cardId);
        return pay(userId, categoryId, price, storeCode, storeName, selectedCardByUser);

    }

    public Object autoPay(PayAutoRequestDTO requestDTO){
        Long userId = requestDTO.getUserId();
        Long categoryId = requestDTO.getCategoryId();
        Integer price = requestDTO.getPrice();
        String storeCode = requestDTO.getStoreCode();
        String storeName = requestDTO.getStoreName();

        Optional<UserCard> uc = selectCardToPay(userId, categoryId, price);
        return pay(userId, categoryId, price, storeCode, storeName, uc);
    }

    private Object pay(Long userId, Long categoryId, Integer price, String storeCode, String storeName, Optional<UserCard> uc) {
        if(uc.isPresent()){
            UserCard payCard= uc.get();
            Card cardProduct = payCard.getCard();
            int criticalPerformance = cardProduct.getPerformance(); // 실적 임계값
            Optional<UserCardHistory> thisMonthHistory = userCardHistoryRepository.findByUserCardIdAndDate(payCard.getUid(), getDate());

            //결제금액 계산
            int payback = calulatePaybackByUserCardAndCategoryIdAndPrice(payCard, categoryId, price);

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
                    .cardNum(payCard.getNum())
                    .paymentDate(new Date(System.currentTimeMillis()))
                    .paymentAmount(price)
                    .storeCode(storeCode)
                    .storeName(storeName)
                    .categoryName(categoryName)
                    .benefitAmount(payback)
                    .build());
            //----------------------------------------카드의 월별 결제 내역 저장--------------------------------------
            if(thisMonthHistory.isPresent()){ // 이미 저장된 내역이 있을 경우 UserCardHistory 업데이트
                UserCardHistory tmh = thisMonthHistory.get();
                tmh.setUseAmount(tmh.getUseAmount() + price);
                tmh.setBenefitAmount(tmh.getBenefitAmount() + payback);
                tmh.setIsFulfilled(criticalPerformance <= tmh.getUseAmount() + price); //이번 결제로 실적 완성?
                userCardHistoryRepository.save(tmh);
            }else{// 이미 저장된 내역이 없을 경우 UserCardHistory 생성
                UserCardHistory uch = UserCardHistory.builder()
                        .userCard(payCard)
                        .yearAndMonth(Date.valueOf(LocalDate.now().withDayOfMonth(1)))
                        .benefitAmount(payback)
                        .useAmount(price)
                        .isFulfilled(criticalPerformance <= price)
                        .build();
                userCardHistoryRepository.save(uch);
            }

            //------------------------------------------카테고리별 혜택 내역 저장--------------------------------------------------
            if (payback >0) {
                Optional<UserCategoryHistory> categoryHistory = userCategoryHistoryRepository.findBenefitOfMonthByUserUidAndCategoryIdAndDate(userId, categoryId, getDate());

                if (categoryHistory.isPresent()) {
                    UserCategoryHistory ucyh = categoryHistory.get();
                    ucyh.setBenefitAmount(ucyh.getBenefitAmount() + payback);
                    ucyh.setUseAmount(ucyh.getUseAmount() + price);
                    userCategoryHistoryRepository.save(ucyh);
                } // 이미 저장된 내역이 있을 경우 UserCategoryHistory 업데이트
                else {// 이미 저장된 내역이 없을 경우 UserCategoryHistory 생성
                    userCategoryHistoryRepository.save(UserCategoryHistory.builder()
                            .user(payCard.getUser())
                            .category(category)
                            .yearAndMonth(Date.valueOf(LocalDate.now().withDayOfMonth(1)))
                            .benefitAmount(payback)
                            .useAmount(price)
                            .build());
                }
            }

            Notification notification = Notification.builder()
                    .user(payCard.getUser())
                    .message("결제가 완료되었습니다. 결제금액: " + String.format("%,d", price) + "원, 페이백금액: " + String.format("%,d", payback) + "원")
                    .createdAt(LocalDateTime.now())
                    .activeRead(false)
                    .build();

            notificationRepository.save(notification);


            // 결제 완료 정보 응답
            return CompletedPaymentDTO.builder()
                    .price(price)
                    .payback(payback)
                    .cardImage(cardProduct.getImage())
                    .cardName(cardProduct.getName())
                    .build();
        }
        return "NoAvailableCard";
    }

    public List<UserPayCardDTO> getCardListToPay(CardToPayListRequestDTO requestdto){
        Long userId = requestdto.getUserId();
        Long categoryId = requestdto.getCategoryId();
        int price = requestdto.getPrice();

        List<UserPayCardDTO> result = new ArrayList<>(); // 요청 응답을 위한 리스트 생성
        List<UserCard> userCards = userCardRepository.findByUserId(userId);

        // 결제 카드 유효 체크
        Map<UserCard, String> availabilityChecked = checkavailableCards(userCards, price);
        userCards = availabilityChecked.entrySet().stream()
                .filter(entry -> entry.getValue().equals("AVAILABLE"))
                .map(Map.Entry::getKey)
                .toList();

        if (!userCards.isEmpty()) {

            //1. 페이백 받는 금액이 같다면, 실적 달성이 더 얼마 안남은 카드 순위 정렬
            userCards = sortUserCardsByRemainingPerformance(userCards);
            //2. 혜택 한도를 고려한 페이백 금액이 가장 큰 카드 순위 정렬
            userCards = sortByRemainingBenefit(userCards, price, categoryId);
            for (UserCard uc : userCards){
                result.add(UserPayCardDTO.builder()
                        .cardId(uc.getUid())
                        .payback(calulatePaybackByUserCardAndCategoryIdAndPrice(uc, categoryId, price))
                        .cardName(uc.getCard().getName())
                        .cardImage(uc.getCard().getImage())
                        .build());
            }
            return result;// 최우선 순위대로 카드 반환

        }else {return null;}

    }//getCardListToPay()



    //유저의 카드 중 결제에 사용할 카드 선택
    private Optional<UserCard> selectCardToPay(Long userId, Long categoryId, Integer price) {
        List<UserCard> userCards = userCardRepository.findByUserId(userId);

        // 카드 유효 체크
        Map<UserCard, String> availabilityChecked = checkavailableCards(userCards, price);
        // availabilityChecked의 entry value가 "ABAILABLE"인 카드만 필터링
        userCards = availabilityChecked.entrySet().stream()
                .filter(entry -> entry.getValue().equals("AVAILABLE"))
                .map(Map.Entry::getKey)
                .toList();

        if (!userCards.isEmpty()) {
            //1. 페이백 받는 금액이 같다면, 실적 달성이 더 얼마 안남은 카드 순위 정렬
            userCards = sortUserCardsByRemainingPerformance(userCards);
            //2. 혜택 한도를 고려한 페이백 금액이 가장 큰 카드 순위 정렬
            userCards = sortByRemainingBenefit(userCards, price, categoryId);

            return Optional.ofNullable(userCards.get(0));// 최우선 순위대로 한 장의 카드 반환

        }else {//보유한 카드가 없음
            return Optional.empty();
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


            if(remaining1<0 || remaining2<0){
                return Integer.compare(remaining2, remaining1); // 두 수중 큰 것 먼저 (이미 실적 채움 < 채우지 않은 실적)
            }else {return Integer.compare(remaining1, remaining2);} // 오름차순-(채워야 할 실적이 작은 것 부터)

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
            Integer payback1 = calulatePaybackByUserCardAndCategoryIdAndPrice(uc1, categoryId, price);

            //카드 2의 할인 금액
            Integer payback2 = calulatePaybackByUserCardAndCategoryIdAndPrice(uc2, categoryId, price);

            return Integer.compare(payback2, payback1);// 내림차순


        });
        return sortedCardList;
    }


    /*  예외 처리
     *    (jpa 쿼리로 구현할 수 있지만 service 단에서 처리할 경우,
     *    메서드를 수정해 클라이언트에게 각 카드별 결제 실패 원인을 제공해 수 있음)
     *
     *    1. 정지된 카드 (만료 카드)
     *    2. 한도 초과 카드
     *
     *    유저 카드와, 결제 가능 또는 결제 불가 사유 Map반환
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

    //지난 달의 Date 반환
    private java.sql.Date getLastMonthDate(){
        return java.sql.Date.valueOf(LocalDate.now().minusMonths(1).withDayOfMonth(1));

    }
    
    //페이백 금액 계산
    private int calulatePaybackByUserCardAndCategoryIdAndPrice(UserCard userCard, Long categoryId, int price){
        int payback = 0;
        //카드에 대해 월별로 계산된 정보
        Optional<UserCardHistory> lastMonthHistory = userCardHistoryRepository.findByUserCardIdAndDate(userCard.getUid(), getLastMonthDate());
        Optional<UserCardHistory> thisMonthHistory = userCardHistoryRepository.findByUserCardIdAndDate(userCard.getUid(), getDate());
        if(lastMonthHistory.isPresent()) {
            UserCardHistory lmh = lastMonthHistory.get();
            //전월 실적달성 확인
            if(lmh.getIsFulfilled()){
                Card userCardProduct = userCard.getCard();// 해당 사용자카드의 상품 정보
                // 카드가 가진 혜택 목록
                List<CardBenefit> benefitList = userCardProduct.getBenefitList();
                //전월 실적에 따른 할인율 적용
                double benefitRate = benefitList.stream()
                        .filter(cb -> cb.getCategory().getUid().equals(categoryId))
                        .map(CardBenefit::getBenefitRate)
                        .findFirst()
                        .orElse(0) / 100.0; // 할인율을 double로 변환
                payback = (int) (price * benefitRate); // 할인 금액 계산

                // 혜택 한도 확인
                if(thisMonthHistory.isPresent()){
                    UserCardHistory tmh = thisMonthHistory.get();
                    int thisMonthBenefitAmount = tmh.getBenefitAmount();
                    // 카드의 혜택 한도 금액
                    int benefitLimit = userCardProduct.getBenefitLimit();
                    if(benefitLimit <= (thisMonthBenefitAmount + payback)){ //이대로 결제하면 할인 한도가 초과될 때
                        //최대 혜택금 - 받은 혜택금(혜택한도까지 남은 혜택)이 이번 결제의 페이백이 된다.
                        payback = benefitLimit - thisMonthBenefitAmount;
                    }
                }
            }
        }
        return  payback;
    }
}
