package com.eazy.pay.service;

import com.eazy.pay.dao.CardBenefitRepository;
import com.eazy.pay.dto.CardDTO;
import com.eazy.pay.mapper.CardMapper;
import com.eazy.pay.model.CardBenefit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CardBenefitService {

    @Autowired
    private CardBenefitRepository cardBenefitRepository;

    public Page<CardDTO> getCardsByCategoryId(Long categoryId, Pageable pageable) {

        return cardBenefitRepository.findByCategoryUid(categoryId, pageable)
                .map(CardBenefit::getCard)
                .map(CardMapper.INSTANCE::toDTO);
    }
}
