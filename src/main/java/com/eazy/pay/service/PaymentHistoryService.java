package com.eazy.pay.service;

import com.eazy.pay.dao.UserCardRepository;
import com.eazy.pay.dto.PaymentHistoryDTO;
import com.eazy.pay.model.PaymentHistory;
import com.eazy.pay.dao.PaymentHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

@Service
public class PaymentHistoryService {

    @Autowired
    private PaymentHistoryRepository paymentHistoryRepository;

    @Autowired
    private UserCardRepository userCardRepository;

    public int getPaymentHistoryForMonth(int year, int month) {
        // 해당 월의 전체 승인 건수를 계산하는 로직
        LocalDateTime startOfMonth = LocalDateTime.of(year, month, 1, 0, 0);
        LocalDateTime endOfMonth = startOfMonth.plusMonths(1);

        return paymentHistoryRepository.countByPaymentDateBetween(startOfMonth, endOfMonth);
    }

    public int getTotalAmountForMonth(int year, int month) {
        LocalDate startOfMonth = LocalDate.of(year, month, 1);
        LocalDate endOfMonth = startOfMonth.with(TemporalAdjusters.lastDayOfMonth());

        // LocalDateTime으로 변환
        LocalDateTime startOfDay = startOfMonth.atStartOfDay();
        LocalDateTime endOfDay = endOfMonth.atTime(LocalTime.MAX);

        // 데이터베이스에서 해당 월의 결제 금액 합계를 구한다.
        Integer totalAmount = paymentHistoryRepository.sumPaymentAmountBetween(startOfDay, endOfDay);

        // 만약 데이터가 null이면 0을 반환한다.
        return totalAmount != null ? totalAmount : 0;
    }


    public Page<PaymentHistoryDTO> getPaymentHistoryByUserId(Long userId, int year, int month, Pageable pageable) {
        return userCardRepository.findPaymentHistoryByUserIdAndMonth(userId, year, month, pageable);
    }



    public List<PaymentHistory> getAllPayBenefits() {
        return paymentHistoryRepository.findAll();
    }

    public List<PaymentHistory> getPayBenefitsForNMonthByCardNum(String num, int month){ return paymentHistoryRepository.findByCardNumAndDateWithinDate(num, Timestamp.valueOf(LocalDate.now().minusMonths(month).atStartOfDay())).orElse(null);}

}
