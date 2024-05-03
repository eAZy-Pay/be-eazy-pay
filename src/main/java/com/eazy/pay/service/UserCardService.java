package com.eazy.pay.service;

import com.eazy.pay.dao.*;
import com.eazy.pay.dto.*;
import com.eazy.pay.mapper.UserCardMapper;
import com.eazy.pay.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.sql.Timestamp;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
public class UserCardService {

    @Autowired
    private UserCardRepository userCardRepository;
    @Autowired
    private UserCardHistoryRepository userCardHistoryRepository;
    @Autowired
    private CardRepository cardRepository;

    @Autowired
    private PaymentHistoryRepository paymentHistoryRepository;

    @Autowired
    private UserCategoryHistoryRepository userCategoryHistoryRepository;

    public void deleteUserCard(Long userCardId) {
        userCardRepository.deleteById(userCardId);
    }

    public List<UserCard> getAllUserCards() {
        return userCardRepository.findAll();
    }

    public UserCard getUserCardByUid(Long uid){
        return userCardRepository.findByUid(uid).orElse(null);
    }

    // UserId로 자신의 보유 카드 모두가져오기
    public List<UserCard> getUserCardsByUserId(Long userId) {return userCardRepository.findByUserUid(userId);}

    public BenefitAndSimpleUserCardsDTO getSimpleBenefitDashboardByUserId(Long userId, int month, int count) {
        List<UserCard> userCards = userCardRepository.findByUserUid(userId); //자신의 모든 보유 카드 가져오기

        int totalBenefitAmount = 0; // 총 받은 혜택
        int totalPaymentLimit = 0; // 총 결제 한도
        int totalUsedAmount = 0; // 총 사용 금액

        List<SimpleUserCardDTO> cards = new ArrayList<>(); //리턴할 DTO에 넣어줄 보유카드의 상품+사용 정보
        List<SimpleUserCardDTO> beforeSort = new ArrayList<>(); //모든 보유카드의 계산된 사용 정보에 따라 정렬하기 전 임시 리스트

        // 날짜 계산
        LocalDate now = LocalDate.now();
        LocalDate startDate = now.minusMonths(month - 1).withDayOfMonth(1); // 설정된 month에 따라 시작일을 계산
        LocalDate endDate = now; // 오늘 날짜로 종료일을 설정
        

        for (UserCard uc : userCards) {
            Card card = uc.getCard();
            String cardNum = uc.getNum();
            int paymentLimit = uc.getPaymentLimit();
            boolean linkEazy = uc.isLinkEazy();
            boolean cardValid = uc.isCardValid();
            Date expirationDate = uc.getExpirationDate();
            String num = uc.getNum();
            Long uid = uc.getUid();

            // 해당 카드의 특정 월 기간 내 모든 거래내역 가져오기
            List<PaymentHistory> payBenefitsForMonth =
                    paymentHistoryRepository.findByCardNumAndDateWithinDate(
                            cardNum,
                            Timestamp.valueOf(startDate.atStartOfDay()), // 시작 날짜를 Timestamp로 변환
                            Timestamp.valueOf(endDate.plusDays(1).atStartOfDay()) // 종료 날짜에 1일 더해 포함되게 처리
                    );

            int benefitAmount = 0;
            int useAmount = 0;

            if (payBenefitsForMonth != null) {
                for (PaymentHistory pb : payBenefitsForMonth) {
                    benefitAmount += pb.getBenefitAmount();
                    useAmount += pb.getPaymentAmount();
                }
            }

            totalBenefitAmount += benefitAmount; 
            totalPaymentLimit += paymentLimit;
            totalUsedAmount += useAmount;

            SimpleUserCardDTO userCard = SimpleUserCardDTO.builder()
                            .card(card)
                            .uid(uid)
                            .benefitAmount(benefitAmount)
                            .paymentLimit(paymentLimit)
                            .useAmount(useAmount)
                            .expirationDate(expirationDate)
                            .cardValid(cardValid)
                            .linkEazy(linkEazy)
                            .num(num)
                            .build();

            beforeSort.add(userCard); //정렬되기 이전 카드로 리스트에 추가

        }
        // 계산되어 나온 SimpleUserCardDTO를 benefitAmount로 정렬한 뒤 useAmount로 정렬해주기

        sortUserCards(beforeSort);  // 정렬 로직 호출

        // 정렬된 리스트를 최대 count의 수만큼 선택 후 cards에 할당, count가 0이면 모든 카드를 선택
        int maxSize = (count <= 0) ? beforeSort.size() : Math.min(beforeSort.size(), count);

        cards = beforeSort.subList(0, maxSize);

        return BenefitAndSimpleUserCardsDTO.builder()
                .totalBenefitAmount(totalBenefitAmount)
                .totalPaymentLimit(totalPaymentLimit)
                .totalUsedAmount(totalUsedAmount)
                .availableFunds(totalPaymentLimit - totalUsedAmount)
                .cards(cards)
                .build();
    }

     void sortUserCards(List<SimpleUserCardDTO> cards) {
        /*
        1. 실적과 혜택을 모두 채운 카드
        2. 실적만 채운 카드
        3. 혜택만 채운 카드
        4. 실적과 혜택을 모두 채우지 못한 카드
        5. 채운 혜택 금액이 높은 순서
        6. 채울 성과가 얼마 안 남은 순서
        */

        cards.sort(Comparator
                .comparing((SimpleUserCardDTO dto) -> {
                    Card card = dto.getCard();
                    boolean performanceMet = dto.getUseAmount() >= card.getPerformance();
                    boolean benefitMet = dto.getBenefitAmount() >= card.getBenefitLimit();
                    return (performanceMet && benefitMet) ? 0 : 4;  // 모두 채움 vs 모두 미달성
                })
                .thenComparing((SimpleUserCardDTO dto) -> {
                    Card card = dto.getCard();
                    boolean performanceMet = dto.getUseAmount() >= card.getPerformance();
                    boolean benefitMet = dto.getBenefitAmount() >= card.getBenefitLimit();
                    return (performanceMet && !benefitMet) ? 1 : 4;  // 실적만 채움 vs 기타
                })
                .thenComparing((SimpleUserCardDTO dto) -> {
                    Card card = dto.getCard();
                    boolean performanceMet = dto.getUseAmount() >= card.getPerformance();
                    boolean benefitMet = dto.getBenefitAmount() >= card.getBenefitLimit();
                    return (!performanceMet && benefitMet) ? 2 : 4;  // 혜택만 채움 vs 기타
                })
                .thenComparing((SimpleUserCardDTO dto) -> {
                    Card card = dto.getCard();
                    boolean performanceMet = dto.getUseAmount() >= card.getPerformance();
                    boolean benefitMet = dto.getBenefitAmount() >= card.getBenefitLimit();
                    return (!performanceMet && !benefitMet) ? 3 : 0;  // 모두 미달성
                })
                .thenComparing(SimpleUserCardDTO::getBenefitAmount, Comparator.reverseOrder())  // 채운 혜택 금액이 높은 순서
                .thenComparing(dto -> {
                    Card card = dto.getCard();
                    // 채울 성과가 얼마 안 남은 순서 (작은 값이 낮은 순서)
                    return card.getPerformance() - dto.getUseAmount();
                }));
    }

    public UserCard createUserCard(UserCardDTO userCardDTO) {

        // 사용자 ID와 카드 ID가 없을 경우 예외 발생
        if (userCardDTO.getUserId() == null || userCardDTO.getCardId() == null) {
            throw new IllegalArgumentException("사용자 ID와 카드 ID는 필수 입력값입니다.");
        }
        // 카드 만료일이 없을 경우 5년 뒤로 설정
        if (userCardDTO.getExpirationDate() == null) {
            Date expirationDate = new Date(System.currentTimeMillis());
            expirationDate.setYear(expirationDate.getYear() + 5);
            userCardDTO.setExpirationDate(expirationDate);
        }
        // 카드번호 값이 없을 경우 랜덤 값으로 설정
        if (userCardDTO.getNum() == null) {
            long randomNumber = (long)(Math.random() * 10_000_000_000_000_000L); // 0 ~ 9999999999999999 사이의 랜덤 숫자 생성
            String formattedNumber = String.format("%016d", randomNumber); // 16자리로 포맷팅
            // 이미 존재하는 카드번호인지 확인
            while (userCardRepository.existsByNum(formattedNumber)) {
                randomNumber = (long)(Math.random() * 10_000_000_000_000_000L);
                formattedNumber = String.format("%016d", randomNumber);
            }
            userCardDTO.setNum(formattedNumber);
        }
        // 결제 한도 값이 없을 경우 300만원으로 설정
        if (userCardDTO.getPaymentLimit() <= 0) {
            userCardDTO.setPaymentLimit(3_000_000);
        }
        // 카드 활성화 여부와 eAZy 카드와 연결 여부를 true로 설정
        userCardDTO.setCardValid(true);
        userCardDTO.setLinkEazy(true);

        UserCard userCard = UserCardMapper.INSTANCE.toEntity(userCardDTO);
        return userCardRepository.save(userCard); // 저장
    }

    public void disableUserCard(Long userCardId) {
        Optional<UserCard> userCard = userCardRepository.findByUid(userCardId);
        if(userCard.isPresent()){
           UserCard uc = userCard.get();
        uc.setCardValid(false);
        userCardRepository.save(uc);
        }
    }

    public void toggleCardValidity(Long userCardId) {
        UserCard userCard = userCardRepository.findById(userCardId)
                .orElseThrow(() -> new RuntimeException("Card not found"));

        // 카드의 현재 유효성을 토글링
        userCard.setCardValid(!userCard.isCardValid());
        userCardRepository.save(userCard); // 변경된 유효성 저장
    }

    public void toggleCardLink(Long userCardId) {
        UserCard userCard = userCardRepository.findById(userCardId)
                .orElseThrow(() -> new RuntimeException("Card not found"));

        // 카드의 현재 유효성을 토글링
        userCard.setLinkEazy(!userCard.isLinkEazy());
        userCardRepository.save(userCard); // 변경된 유효성 저장
    }

    public void togglePaymentLimit(Long userCardId, int paymentLimit) {
        UserCard userCard = userCardRepository.findById(userCardId)
                .orElseThrow(() -> new RuntimeException("Card not found"));

        UserCardHistory currentMonthUsage = userCardHistoryRepository.findCurrentMonthUsageByUserCardId(userCardId);

        if (currentMonthUsage != null && paymentLimit < currentMonthUsage.getUseAmount()) {
            throw new IllegalArgumentException("사용 금액 이하로 한도를 변경하실 수 없습니다.");
        }

        userCard.setPaymentLimit(paymentLimit);
        userCardRepository.save(userCard);
    }


    public boolean checkUserCard(Long userId, Long cardId) {
        return userCardRepository.findByUserUid(userId).stream()
                .anyMatch(userCard -> userCard.getCard().getUid().equals(cardId) && userCard.isCardValid());
    }

    public CardUsageSummaryDTO getCardUsageSummary(Long userId) { // 카테고리 내용이 아닌 연회비는 dashBoard로 뺄 것
        Date date = new java.sql.Date(System.currentTimeMillis());
        date = Date.valueOf(date.toLocalDate().withDayOfMonth(1));

        List<UserCard> userCardList = userCardRepository.findByUserUid(userId);
        // 올해의 혜택 누적금액
        int benefitOfYear = userCategoryHistoryRepository.findBenefitOfYearByUserUidAndDate(userId, date);
        List<CategoryBenefitAmountDTO> categoryBenefitAmountDTOList = new ArrayList<>();
        if(!userCardList.isEmpty()) { //보유카드 존재
            //연회비 구하기, 유효한 카드만 필터링
            int totalAnnualFee = userCardList.stream()
                    .filter(UserCard::isCardValid)
                    .map((UserCard userCard) -> userCard.getCard().getAnnualFee())
                    .reduce(0, Integer::sum);

            // 이번 달 혜택 정보 중 값이 0보다 큰 것만 가져오기
            List<UserCategoryHistory> userCategoryHistoryList = userCategoryHistoryRepository.findByUserIdAndDate(userId, date).stream()
                    .filter(userCategoryHistory -> userCategoryHistory.getBenefitAmount() > 0)
                    .collect(Collectors.toList());

            System.out.println("userCategoryHistoryList: " + userCategoryHistoryList);

            if (!userCategoryHistoryList.isEmpty()) {
                // 각 카테고리별 혜택 금액 구하기 & 이번달 혜택 금액 누적
                int benefitOfMonth = 0;
                for (UserCategoryHistory userCategoryHistory : userCategoryHistoryList) {
                    int benefitAmount = userCategoryHistory.getBenefitAmount();
                    benefitOfMonth += benefitAmount;
                    categoryBenefitAmountDTOList.add(CategoryBenefitAmountDTO.builder()
                            .categoryName(userCategoryHistory.getCategory().getName())
                            .benefitAmount(benefitAmount)
                            .build());
                }
                System.out.println("categoryBenefitAmountDTOList: " + categoryBenefitAmountDTOList);

            // 올해의 혜택 누적금액
                Integer benefitOfYearData = userCategoryHistoryRepository.findBenefitOfYearByUserUidAndDate(userId, date);
                System.out.println("benefitOfYearData: " + benefitOfYearData);
                //각 카테고리별 혜택 금액을 내림차순으로 정렬하고 3개까지만 DTO에 담아서 리턴
                List<CategoryBenefitAmountDTO> top3List = categoryBenefitAmountDTOList.stream()
                        .sorted(Comparator.comparing(CategoryBenefitAmountDTO::getBenefitAmount).reversed())
                        .limit(3)
                        .collect(Collectors.toList());

                System.out.println("top3List: " + top3List);

                //top3List에 포함되지 않는 나머지 카테고리명에 대한 월별 사용 금액을 합친 값 추가
                int otherCategoryAmount = categoryBenefitAmountDTOList.stream()
                        .filter(categoryBenefitAmountDTO -> top3List.stream()
                                .noneMatch(top3 -> top3.getCategoryName().equals(categoryBenefitAmountDTO.getCategoryName())))
                        .mapToInt(CategoryBenefitAmountDTO::getBenefitAmount)
                        .sum();

                top3List.add(CategoryBenefitAmountDTO.builder()
                        .categoryName("기타")
                        .benefitAmount(otherCategoryAmount)
                        .build());

                System.out.println("top3List: " + top3List);

                //이번 달 혜택과 연회비, 올해의 혜택 누적금액 리턴
                return CardUsageSummaryDTO.builder()
                        .categoryBenefitAmount(top3List)
                        .totalAnnualFee(totalAnnualFee)
                        .benefitOfYear(benefitOfYear)
                        .benefitOfMonth(benefitOfMonth)
                        .build();
            }
            // 월중 혜택 부재
            return CardUsageSummaryDTO.builder()
                    .benefitOfYear(benefitOfYear)
                    .totalAnnualFee(totalAnnualFee)
                    .build();
        }
        // 아무 결제 내역이 없음, 연중 혜택 부재
        return CardUsageSummaryDTO.builder()
                .totalAnnualFee(0)
                .build();
    }


    public List<ValidUserCardDTO> getValidUserCardsByUserId(Long userUid) {
        List<UserCard> validUserCards = userCardRepository.findByUserUidAndCardValid(userUid, true);
        // UserCard 엔티티 목록을 UserCardDTO 리스트로 변환
        List<UserCardDTO> userCardDTOs = validUserCards.stream()
                .map(UserCardMapper.INSTANCE::toDTO)
                .collect(Collectors.toList());
        // UserCardDTO 리스트를 ValidUserCardDTO 리스트로 변환
        List<ValidUserCardDTO> validUserCardDTOs = userCardDTOs.stream()
                .map(dto -> {
                    Card card = cardRepository.findByUid(dto.getCardId())
                            .orElseThrow(() -> new RuntimeException("Card not found"));
                    return ValidUserCardDTO.builder()
                            .uid(dto.getUid())
                            .userId(dto.getUserId())
                            .cardId(dto.getCardId())
                            .image(card.getImage())
                            .cardName(card.getName())
                            .build();
                }).collect(Collectors.toList());
        // PayService의 카드 결제 추천 함수 반환 값에서 예상 혜택 뽑아내서 validUserCardDTOs에 추가

        return validUserCardDTOs;
    }
}
