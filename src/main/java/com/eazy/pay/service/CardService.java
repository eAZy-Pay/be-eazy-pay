package com.eazy.pay.service;

import com.eazy.pay.dao.HighlightedCardRepository;
import com.eazy.pay.dto.CardDTO;
import com.eazy.pay.dto.CardWithBenefitDTO;
import com.eazy.pay.mapper.CardMapper;
import com.eazy.pay.mapper.CardWithBenefitMapper;
import com.eazy.pay.model.Card;
import com.eazy.pay.dao.CardRepository;
import com.eazy.pay.model.HighlightedCard;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CardService {

    @Autowired
    private CardRepository cardRepository;

    @Autowired
    private HighlightedCardRepository highlightedCardRepository;

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

    public CardDTO updateCard(Long cardId, CardDTO cardDTO) {
        Card card = cardRepository.findByUid(cardId).orElseThrow(() -> new IllegalArgumentException("수정할 카드가 없습니다."));
        
        card.setName(cardDTO.getName());
        card.setAnnualFee(cardDTO.getAnnualFee());
        card.setPerformance(cardDTO.getPerformance());
        card.setBenefitLimit(cardDTO.getBenefitLimit());
        card.setInfo(cardDTO.getInfo());
        cardRepository.save(card);
        return cardDTO;
    }

    public List<CardDTO> getHighlightedCards(Long eventCategoryId) {

        return highlightedCardRepository.findByEventCategoryUid(eventCategoryId).stream()
                .map(HighlightedCard::getCard)
                .map(CardMapper.INSTANCE::toDTO)
                .toList();
    }
}
