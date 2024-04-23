package com.eazy.pay.service;

import com.eazy.pay.dto.CardDTO;
import com.eazy.pay.dto.CardWithBenefitDTO;
import com.eazy.pay.mapper.CardMapper;
import com.eazy.pay.mapper.CardWithBenefitMapper;
import com.eazy.pay.model.Card;
import com.eazy.pay.dao.CardRepository;
import com.eazy.pay.model.CardBenefit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CardService {

    @Autowired
    private CardRepository cardRepository;

    public Page<CardDTO> getAllCards(Pageable pageable) {

        return cardRepository.findAll(pageable)
                .map(CardMapper.INSTANCE::toDTO);
    }

    public Page<CardDTO> getCardsLikeName(String name, Pageable pageable) {

        return cardRepository.findByNameContaining(name, pageable)
                .map(CardMapper.INSTANCE::toDTO);
    }

    public CardWithBenefitDTO getCardWithBenefitByCardId(Long cardId) {
        return CardWithBenefitMapper.INSTANCE.toDTO(cardRepository.findByUid(cardId).orElse(null));
    }

    public CardDTO getCardById(Long cardId) {
        return CardMapper.INSTANCE.toDTO(cardRepository.findByUid(cardId).orElse(null));
    }

    public CardDTO createCard(CardDTO cardDTO) {
        Card card = CardMapper.INSTANCE.toEntity(cardDTO);
        cardRepository.save(card);
        return cardDTO;
    }

    public void deleteCard(Long cardId) {
        cardRepository.deleteById(cardId);
    }
}
