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

            if(cards.size()<4) { // 최대 4개 카드만 가져오기
                SimpleUserCardDTO userCard = SimpleUserCardDTO.builder()
                                .card(card)
                                .benefitAmount(benefitAmount1)
                                .useAmount(useAmount)
                                        .build();

                cards.add(userCard);
            }
        }
        return BenefitAndSimpleUserCardsDTO.builder()
                .benefitAmount(totalBenefitAmount)
                .cards(cards)
                .build();
    }
}
