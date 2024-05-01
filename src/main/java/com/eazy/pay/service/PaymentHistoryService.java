package com.eazy.pay.service;

import com.eazy.pay.dao.PaymentHistoryRepository;
import com.eazy.pay.dao.UsageStatisticsRepository;
import com.eazy.pay.dao.UserCardRepository;
import com.eazy.pay.dao.UserRepository;
import com.eazy.pay.dto.PaymentHistoryDTO;
import com.eazy.pay.dto.PaymentStatsDTO;
import com.eazy.pay.model.PaymentHistory;
import com.eazy.pay.model.UsageStatistic;
import com.eazy.pay.model.User;
import com.eazy.pay.model.UserCard;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class PaymentHistoryService {

    @Autowired
    private PaymentHistoryRepository paymentHistoryRepository;

    @Autowired
    private UserCardRepository userCardRepository;

    @Autowired
    private UsageStatisticsRepository usageStatisticsRepository;

    @Autowired
    private UserRepository userRepository;

    public PaymentStatsDTO getPaymentHistoryAndAmountForMonth(Long userId, int year, int month) {
        // 해당 유저가 가진 모든 카드 번호를 가져옴
        List<String> cardNumbers = userCardRepository.findByUserUid(userId)
                .stream()
                .map(UserCard::getNum)
                .toList();

        if (cardNumbers.isEmpty()) {
            // 유저가 가진 카드가 없으면 0을 반환
            return new PaymentStatsDTO(0, 0);
        }

        // 해당 월의 전체 승인 건수를 계산하는 로직
        LocalDateTime startOfMonth = LocalDateTime.of(year, month, 1, 0, 0);
        LocalDateTime endOfMonth = startOfMonth.plusMonths(1).minusSeconds(1);

        System.out.println(startOfMonth);
        System.out.println(endOfMonth);

        // 결제 건수 구하기
        int totalPaymentCount = paymentHistoryRepository.countByPaymentDateBetween(cardNumbers, startOfMonth, endOfMonth);

        // 결제 금액 합계 구하기
        Integer totalPaymentAmount = paymentHistoryRepository.sumPaymentAmountBetween(
                cardNumbers,
                Timestamp.valueOf(startOfMonth),
                Timestamp.valueOf(endOfMonth)
        );


        // null 확인 후 기본값 처리
        if (totalPaymentAmount == null) {
            totalPaymentAmount = 0;
        }
        // 결제 건수와 합계를 반환하는 PaymentStats 객체 생성
        return new PaymentStatsDTO(totalPaymentCount, totalPaymentAmount);
    }

    public Page<PaymentHistoryDTO> getPaymentHistoryByUserId(Long userId, int year, int month, Pageable pageable) {
        return userCardRepository.findPaymentHistoryByUserIdAndMonth(userId, year, month, pageable);
    }



    public List<PaymentHistory> getAllPayBenefits() {
        return paymentHistoryRepository.findAll();
    }


    public List<UsageStatistic> getUsageStaticsByUserIdAndDate(Long userId, Date yearAndMonth) {
        if(userId == 0){
            return usageStatisticsRepository.findByAgeAndDate(0, yearAndMonth);
        }

        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("해당 유저가 없습니다."));

        int birthMonth = user.getBirthday().toLocalDate().getMonthValue();
        int birthYear = user.getBirthday().toLocalDate().getYear();
        int currentYear = LocalDate.now().getYear();

        int age = currentYear - birthYear;
        if (LocalDate.now().getMonthValue() < birthMonth) {
            age--;
        }

        // 20 ~ 85세까지 5세 단위로 데이터 존재
        age = (age / 5) * 5;
        age = Math.max(age, 20);
        age = Math.min(age, 85);
        return usageStatisticsRepository.findByAgeAndDate(age, yearAndMonth);
    }

}
