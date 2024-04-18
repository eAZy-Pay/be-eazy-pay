package com.eazy.pay.service;

import com.eazy.pay.dao.PayBenefitRepository;
import com.eazy.pay.dto.BenefitAndSimpleUserCardsDTO;
import com.eazy.pay.dto.SimpleUserCardDTO;
import com.eazy.pay.model.Card;
import com.eazy.pay.model.PayBenefit;
import com.eazy.pay.model.UserCard;
import com.eazy.pay.dao.UserCardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

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
    // 자신의 보유 카드 4개까지만 가져오기
    public List<UserCard> getUserCardsByUserIdWithLimit4(Long userId){return  userCardRepository.findByUserIdLimit4(userId);}


    public BenefitAndSimpleUserCardsDTO getSimpleBenefitDashboardByUserId(Long userId) {
        List<UserCard> userCards = userCardRepository.findByUserId(userId); //자신의 모든 보유 카드 가져오기 (혜택 순서로 정렬 구현 X)
        int totalBenefitAmount = 0; // 모든 카드들의 3개월간 혜택

        List<SimpleUserCardDTO> cards = new ArrayList<>(); //리턴할 DTO에 넣어줄 보유카드의 상품+사용 정보
        List<SimpleUserCardDTO> beforeSort = new ArrayList<>(); //모든 보유카드의 계산된 사용 정보에 따라 정렬하기 전 임시 리스트

        for (UserCard uc : userCards){
            int benefitAmount3 = 0; // 3개월간 혜택
            Card card = uc.getCard(); //카드 상품 정보 가져오기
            String cardNum = uc.getNum();

            //해당 보유 카드의 최근3개월 모든 거래내역
            List<PayBenefit> payBenefitsFor3 =
                    payBenefitRepository.findByCardNumAndDateWithinDate(cardNum,
                            Timestamp.valueOf(LocalDate.now().minusMonths(3).atStartOfDay())
                    ).orElse(null);
            if (payBenefitsFor3 != null){
                for (PayBenefit pb : payBenefitsFor3) {
                benefitAmount3 += pb.getBenefitAmount();
                }
            }

            totalBenefitAmount += benefitAmount3;

            int benefitAmount1 = 0; // 이번 달 혜택
            int useAmount = 0; // 이번 달 사용액
            //해당 보유 카드의 최근 1개월 모든 거래 내역
            List<PayBenefit> payBenefitsFor1 =
                    payBenefitRepository.findByCardNumAndDateWithinDate(cardNum,
                            Timestamp.valueOf(LocalDate.now().minusMonths(1).atStartOfDay())
                    ).orElse(null);

            if (payBenefitsFor1 != null){ // 거래내역이 아예 없으면 게산하지 않음
                for (PayBenefit pb : payBenefitsFor1) {
                    benefitAmount1 += pb.getBenefitAmount();
                    useAmount += pb.getPaymentAmount();
                }
            }

            SimpleUserCardDTO userCard = SimpleUserCardDTO.builder()
                            .card(card)
                            .benefitAmount(benefitAmount1)
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
}
