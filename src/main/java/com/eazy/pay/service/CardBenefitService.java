package com.eazy.pay.service;

import com.eazy.pay.dao.CardBenefitRepository;
import com.eazy.pay.model.CardBenefit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CardBenefitService {

    @Autowired
    private CardBenefitRepository cardBenefitRepository;

    public List<CardBenefit> getAllCardBenefits() {
        return cardBenefitRepository.findAll();
    }
}
