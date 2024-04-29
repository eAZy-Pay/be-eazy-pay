package com.eazy.pay.service;

import com.eazy.pay.dao.PaymentHistoryRepository;
import com.eazy.pay.dao.UserCatetoryHistoryRepository;
import com.eazy.pay.dto.*;
import com.eazy.pay.mapper.UserCardMapper;
import com.eazy.pay.model.*;
import com.eazy.pay.dao.UserCardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserCardService {

    @Autowired
    private UserCardRepository userCardRepository;

    @Autowired
    private PaymentHistoryRepository paymentHistoryRepository;
    @Autowired
    private UserCatetoryHistoryRepository userCatetoryHistoryRepository;

    public List<UserCard> getAllUserCards() {
        return userCardRepository.findAll();
    }

    // UserId로 자신의 보유 카드 모두가져오기
    public List<UserCard> getUserCardsByUserId(Long userId) {return userCardRepository.findByUserUid(userId);}

    public BenefitAndSimpleUserCardsDTO getSimpleBenefitDashboardByUserId(Long userId, int month) {
            List<UserCard> userCards = userCardRepository.findByUserUid(userId); //자신의 모든 보유 카드 가져오기 (혜택 순서로 정렬 구현 X)

        int totalBenefitAmount = 0; // 모든 카드들의 3개월간 혜택
        int totalPaymentLimit = 0; // 총 payment limit 초기화

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

            // 해당 카드의 특정 월 기간 내 모든 거래내역 가져오기
            List<PaymentHistory> payBenefitsForMonth =
                    paymentHistoryRepository.findByCardNumAndDateWithinDate(
                            cardNum,
                            Timestamp.valueOf(startDate.atStartOfDay()), // 시작 날짜를 Timestamp로 변환
                            Timestamp.valueOf(endDate.plusDays(1).atStartOfDay()) // 종료 날짜에 1일 더해 포함되게 처리
                    ).orElse(null);

            int benefitAmount = 0;
            int useAmount = 0;

            if (payBenefitsForMonth != null) {
                for (PaymentHistory pb : payBenefitsForMonth) {
                    benefitAmount += pb.getBenefitAmount();
                    useAmount += pb.getPaymentAmount();
                }
            }

            totalBenefitAmount += benefitAmount; // 총 혜택 금액 누적
            totalPaymentLimit += paymentLimit; // 총 payment limit 누적

            SimpleUserCardDTO userCard = SimpleUserCardDTO.builder()
                            .card(card)
                            .benefitAmount(benefitAmount)
                            .paymentLimit(paymentLimit)
                            .useAmount(useAmount)
                            .build();

            beforeSort.add(userCard); //정렬되기 이전 카드로 리스트에 추가

        }
        // 계산되어 나온 SimpleUserCardDTO를 benefitAmount로 정렬한 뒤 useAmount로 정렬해주기

        sortUserCards(beforeSort);  // 정렬 로직 호출

        // 정렬된 리스트를 최대 4개의 요소만 선택하고 cards에 할당
        int maxSize = Math.min(beforeSort.size(), 4);
        cards = beforeSort.subList(0, maxSize);

        return BenefitAndSimpleUserCardsDTO.builder()
                .benefitAmount(totalBenefitAmount)
                .totalPaymentLimit(totalPaymentLimit)
                .cards(cards)
                .build();
    }

    private void sortUserCards(List<SimpleUserCardDTO> cards) {
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

    public void createUserCard(UserCardDTO userCardDTO) {

        // 사용자 ID와 카드 ID가 없을 경우 예외 발생
        if (userCardDTO.getUserId() == null || userCardDTO.getCardId() == null) {
            throw new IllegalArgumentException("사용자 ID와 카드 ID는 필수 입력값입니다.");
        }
        // 카드 만료일이 없을 경우 5년 뒤로 설정
        if (userCardDTO.getExpirationDate() == null) {
            Date expirationDate = new Date();
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
        userCardRepository.save(userCard); // 저장
    }

    public void disableUserCard(Long userCardId) {
        UserCard userCard = userCardRepository.findByUid(userCardId).orElseThrow(() ->
                new IllegalArgumentException("유효하지 않은 카드 ID입니다.")
        );
        userCard.setCardValid(false);
        userCardRepository.save(userCard);
    }

    public boolean checkUserCard(Long userId, Long cardId) {
        return userCardRepository.findByUserUid(userId).stream()
                .anyMatch(userCard -> userCard.getCard().getUid().equals(cardId) && userCard.isCardValid());
    }

    public CardUsageSummaryDTO getCardUsageSummary(Long userId) {
        java.sql.Date date = new java.sql.Date(System.currentTimeMillis());
        List<UserCard> userCardList = userCardRepository.findByUserUid(userId);
        List<UserCategoryHistory> userCategoryHistoryList = userCatetoryHistoryRepository.findByUserUidAndDate(userId, date);
        int totalAnnualFee = 0;
        int benefitOfYear = 0;

        List<CategoryBenefitAmountDTO> categoryBenefitAmountDTOList = new ArrayList<>();
        if(!userCardList.isEmpty() && !userCategoryHistoryList.isEmpty()) {
            //연회비 구하기
            for (UserCard userCard : userCardList) {
                totalAnnualFee += userCard.getCard().getAnnualFee(); //모든 보유 카드의 연회비 누적
            }

            //각 카테고리별 혜택 금액 구하기 & 혜택 금액 누적
            for (UserCategoryHistory userCategoryHistory : userCategoryHistoryList) {
                int benefitAmount = userCategoryHistory.getBenefitAmount();

                categoryBenefitAmountDTOList.add(CategoryBenefitAmountDTO.builder()
                        .categoryName(userCategoryHistory.getCategory().getName())
                        .benefitAmount(benefitAmount)
                        .build());
            }

            // 올해의 혜택 누적금액
            Optional<Integer> benefitOfYearData = userCatetoryHistoryRepository.findBenefitOfYearByUserUidAndDate(userId, date);
            if (benefitOfYearData.isPresent()) {
                benefitOfYear = benefitOfYearData.get();
            }

            //각 카테고리별 혜택 금액을 내림차순으로 정렬하고 3개까지만 DTO에 담아서 리턴
            List<CategoryBenefitAmountDTO> top3List = categoryBenefitAmountDTOList.stream()
                    .sorted(Comparator.comparing(CategoryBenefitAmountDTO::getBenefitAmount).reversed())
                    .limit(3)
                    .collect(Collectors.toList());

            return CardUsageSummaryDTO.builder()
                    .categoryBenefitAmount(top3List)
                    .totalAnnualFee(totalAnnualFee)
                    .benefitOfYear(benefitOfYear)
                    .build();
        }

        return CardUsageSummaryDTO.builder()
                .totalAnnualFee(totalAnnualFee)
                .build();
    }
}
