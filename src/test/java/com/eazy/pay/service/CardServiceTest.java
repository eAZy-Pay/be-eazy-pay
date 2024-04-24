package com.eazy.pay.service;

import com.eazy.pay.dao.CardRepository;
import com.eazy.pay.dto.CardDTO;
import com.eazy.pay.dto.CardWithBenefitDTO;
import com.eazy.pay.model.Card;
import com.eazy.pay.model.CardBenefit;
import com.eazy.pay.model.Category;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CardServiceTest {

    @Mock
    private CardRepository cardRepository;

    @InjectMocks
    private CardService cardService;

    @BeforeEach
    void setUp() {
        Card card1 = Card.builder()
                .uid(1L)
                .image("image1")
                .name("외식 카드")
                .annualFee(10_000)
                .performance(300_000)
                .benefitLimit(10_000)
                .info("외식에 좋은 카드")
                .build();

        Card card2 = Card.builder()
                .uid(2L)
                .image("image2")
                .name("무실적 카드")
                .annualFee(10_000)
                .performance(0)
                .benefitLimit(999_999_999)
                .info("무실적 카드")
                .build();

        Category category1 = Category.builder()
                .uid(1L)
                .name("외식")
                .build();

        Category category2 = Category.builder()
                .uid(2L)
                .name("차량")
                .build();

        CardBenefit cardBenefit1 = CardBenefit.builder()
                .card(card2)
                .category(category1)
                .benefitRate(4)
                .build();

        CardBenefit cardBenefit2 = CardBenefit.builder()
                .card(card2)
                .category(category2)
                .benefitRate(2)
                .build();

        List<Card> cardList = Arrays.asList(card1, card2);

        List<CardBenefit> cardBenefitList = List.of(cardBenefit1, cardBenefit2);

        card2.setBenefitList(cardBenefitList);

        // PageImpl을 사용하여 Page<Card> 객체 생성
        PageRequest pageable = PageRequest.of(0, 10);
        Page<Card> cardPage = new PageImpl<>(cardList, pageable, cardList.size());

        // lenient() 메서드를 사용하여 Mock 객체의 메서드 호출을 유연하게 설정
        lenient().when(cardRepository.findAll(pageable)).thenReturn(cardPage);
        lenient().when(cardRepository.findByNameContaining("무실적", pageable)).thenReturn(new PageImpl<>(List.of(card2)));
        lenient().when(cardRepository.findByUid(2L)).thenReturn(Optional.of(card2));
    }

    @Test
    void getAllCards() {
        Pageable pageable = PageRequest.of(0, 10);
        // CardService의 getAllCards() 메서드 실행
        Page<CardDTO> cardPage = cardService.getAllCards(pageable);

        assertEquals(2, cardPage.getTotalElements()); // 페이지의 총 항목 수 확인
        assertEquals("외식 카드", cardPage.getContent().get(0).getName());
        assertEquals("무실적 카드", cardPage.getContent().get(1).getName());

        verify(cardRepository, times(1)).findAll(pageable);
    }

    @Test
    void getCardsLikeName() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<CardDTO> cardPage = cardService.getCardsLikeName("무실적", pageable);

        assertEquals(1, cardPage.getTotalElements());
        assertEquals("무실적 카드", cardPage.getContent().get(0).getName());

        verify(cardRepository, times(1)).findByNameContaining("무실적", pageable);
    }

    @Test
    void getCardWithBenefitByCardId() {
        CardWithBenefitDTO cardWithBenefitDTO = cardService.getCardWithBenefitByCardId(2L);

        assertEquals("무실적 카드", cardWithBenefitDTO.getCard().getName());
        assertEquals(2, cardWithBenefitDTO.getBenefitList().size());
        assertEquals("외식", cardWithBenefitDTO.getBenefitList().get(0).getCategoryName());
        assertEquals(4, cardWithBenefitDTO.getBenefitList().get(0).getBenefitRate());
        assertEquals("차량", cardWithBenefitDTO.getBenefitList().get(1).getCategoryName());
        assertEquals(2, cardWithBenefitDTO.getBenefitList().get(1).getBenefitRate());

        verify(cardRepository, times(1)).findByUid(2L);
    }

    @Test
    void createCard() {
        CardDTO cardDTO = CardDTO.builder()
                .uid(3L)
                .image("image3")
                .name("테스트 카드")
                .annualFee(10_000)
                .performance(0)
                .benefitLimit(999_999_999)
                .info("테스트 카드")
                .build();

        CardDTO createdCardDTO = cardService.createCard(cardDTO);

        assertEquals(cardDTO.getName(), createdCardDTO.getName());
        assertEquals(cardDTO.getImage(), createdCardDTO.getImage());

        verify(cardRepository, times(1)).save(any(Card.class));
    }

    @Test
    void deleteCard() {
        cardService.deleteCard(2L);

        verify(cardRepository, times(1)).deleteById(2L);
    }

    @Test
    void updateCard() {
        CardDTO cardDTO = CardDTO.builder()
                .uid(2L)
                .image("image2")
                .name("무실적 카드 수정")
                .annualFee(10_000)
                .performance(0)
                .benefitLimit(999_999_999)
                .info("무실적 카드 수정")
                .build();

        CardDTO updatedCardDTO = cardService.updateCard(2L, cardDTO);

        assertEquals(cardDTO.getUid(), updatedCardDTO.getUid());
        assertEquals(cardDTO.getName(), updatedCardDTO.getName());
        assertEquals(cardDTO.getImage(), updatedCardDTO.getImage());

        verify(cardRepository, times(1)).save(any(Card.class));

    }

}
