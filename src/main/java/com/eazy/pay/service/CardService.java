package com.eazy.pay.service;
import com.eazy.pay.dto.CardDTO;
import com.eazy.pay.dto.CardWithBenefitDTO;
import com.eazy.pay.mapper.CardMapper;
import com.eazy.pay.mapper.CardWithBenefitMapper;
import com.eazy.pay.model.Card;
import com.eazy.pay.dao.CardRepository;
import com.eazy.pay.model.CardBenefit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CardService {

    @Autowired
    private CardRepository cardRepository;

    public List<CardDTO> getAllCards() {

        return cardRepository.findAll().stream()
                .map(CardMapper.INSTANCE::toDTO)
                .collect(Collectors.toList());
    }

    public List<CardDTO> getCardsLikeName(String name) {
        List<CardDTO> cardDTOList = cardRepository.findByNameContaining(name).stream()
                .map(CardMapper.INSTANCE::toDTO)
                .collect(Collectors.toList());

        return cardDTOList;
    }

    public CardWithBenefitDTO getCardWithBenefitByCardId(Long cardId) {
        return CardWithBenefitMapper.INSTANCE.toDTO(cardRepository.findByUid(cardId).orElse(null));
    }

    public CardDTO getCardById(Long cardId) {
        return CardMapper.INSTANCE.toDTO(cardRepository.findByUid(cardId).orElse(null));
    }
}
