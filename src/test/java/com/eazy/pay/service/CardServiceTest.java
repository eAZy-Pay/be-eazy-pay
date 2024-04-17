package com.eazy.pay.service;

import com.eazy.pay.dao.CardRepository;
import com.eazy.pay.dto.CardDTO;
import com.eazy.pay.model.Card;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class) // MockitoExtension을 사용하여 Mock 객체를 주입받을 수 있도록 설정
class CardServiceTest {

    @Mock // 가짜 객체(Mock)를 만들어 반환해주는 어노테이션
    private CardRepository cardRepository;

    @InjectMocks // Mock 객체를 주입받아 테스트 대상인 CardService를 만들어주는 어노테이션
    private CardService cardService;

    @BeforeEach // 각 테스트가 실행되기 전에 실행되는 메서드
    void setUp() {
        Card card1 = Card.builder()
                .uid(1L)
                .image("image1")
                .name("외식 카드")
                .annualFee(10_000)
                .performance(300_000)
                .benefitLimit(10_000)
                .info("외식에 좋은 카드")
                .applicationUrl("신청1")
                .build();

        Card card2 = Card.builder()
                .uid(2L)
                .image("image2")
                .name("무실적 카드")
                .annualFee(10_000)
                .performance(0)
                .benefitLimit(999_999_999)
                .info("무실적 카드")
                .applicationUrl("신청2")
                .build();

        List<Card> cardList = List.of(card1, card2);

        // 사용하지 않는 stub이 있으며 UnnecessaryStubbingException 발생
        // lenient() 메서드는 Mockito가 사용하지 않는 stub을 무시하도록 설정
        // cardRepository.findAll() 메서드가 호출되면 cardList를 반환하도록 설정
        lenient().when(cardRepository.findAll()).thenReturn(cardList);

        // cardRepository.findByNameContaining("무실적") 메서드가 호출되면 card2를 반환하도록 설정
        lenient().when(cardRepository.findByNameContaining("무실적")).thenReturn(List.of(card2));

    }


    @Test
    void getAllCards() {
        // CardService의 getAllCards() 메서드를 실행 모든 카드 정보를 조회
        List<CardDTO> cardList = cardService.getAllCards();

        // cardList에는 card1과 card2가 포함되어 있어야 함
        assertEquals(2, cardList.size());
        assertEquals("외식 카드", cardList.get(0).getName());
        assertEquals("무실적 카드", cardList.get(1).getName());

        // cardRepository.findAll() 메서드가 1번 호출되었는지 확인
        // verify() 메서드는 Mock 객체의 메서드가 호출되었는지 확인하는 메서드
        // times() 메서드는 호출 횟수를 지정하는 메서드
        verify(cardRepository, times(1)).findAll();
    }

    @Test
    void getCardsLikeName() {
        // CardService의 getCardsLikeName("무실적") 메서드를 실행 이름에 "무실적"이 포함된 카드 정보를 조회
        List<CardDTO> cardList = cardService.getCardsLikeName("무실적");

        // cardList에는 card2가 포함되어 있어야 함
        assertEquals(1, cardList.size());
        assertEquals("무실적 카드", cardList.get(0).getName());

        // cardRepository.findByNameContaining("무실적") 메서드가 1번 호출되었는지 확인
        verify(cardRepository, times(1)).findByNameContaining("무실적");
    }
}