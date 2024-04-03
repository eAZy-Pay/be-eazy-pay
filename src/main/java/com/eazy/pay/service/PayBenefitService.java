package com.eazy.pay.service;

import com.eazy.pay.model.PayBenefit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PayBenefitService {

    @Autowired
    private PayBenefitRepository payBenefitRepository;

    public List<PayBenefit> getAllPayBenefits() {
        return payBenefitRepository.findAll();
    }
}
