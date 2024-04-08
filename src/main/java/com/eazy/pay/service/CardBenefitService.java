package com.eazy.pay.service;

import com.eazy.pay.dao.CardBenefitRepository;
import com.eazy.pay.dto.CardDTO;
import com.eazy.pay.mapper.CardMapper;
import com.eazy.pay.model.Card;
import com.eazy.pay.model.CardBenefit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CardBenefitService {

    @Autowired
    private CardBenefitRepository cardBenefitRepository;

    public List<CardDTO> getCategoryCards(Long categoryId) {
        List<CardDTO> cardDTOList = cardBenefitRepository.findByCategoryUid(categoryId).stream()
                .map(CardBenefit::getCard)
                .map(CardMapper.INSTANCE::toDTO)
                .collect(Collectors.toList());

        return cardDTOList;
    }
}
