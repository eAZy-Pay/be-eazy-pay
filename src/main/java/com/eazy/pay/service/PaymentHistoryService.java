package com.eazy.pay.service;

import com.eazy.pay.dao.UserCardRepository;
import com.eazy.pay.dto.PaymentHistoryDTO;
import com.eazy.pay.model.PaymentHistory;
import com.eazy.pay.dao.PaymentHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;

@Service
public class PaymentHistoryService {

    @Autowired
    private PaymentHistoryRepository paymentHistoryRepository;

    @Autowired
    private UserCardRepository userCardRepository;

    public Page<PaymentHistoryDTO> getPaymentHistoryByUserId(Long userId, Pageable pageable) {
        return userCardRepository.findPaymentHistoryByUserId(userId, pageable);
    }

    public List<PaymentHistory> getAllPayBenefits() {
        return paymentHistoryRepository.findAll();
    }

    public List<PaymentHistory> getPayBenefitsForNMonthByCardNum(String num, int month){ return paymentHistoryRepository.findByCardNumAndDateWithinDate(num, Timestamp.valueOf(LocalDate.now().minusMonths(month).atStartOfDay())).orElse(null);}
}
