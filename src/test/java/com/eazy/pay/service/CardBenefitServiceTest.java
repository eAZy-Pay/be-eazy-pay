package com.eazy.pay.service;

import com.eazy.pay.dao.CardBenefitRepository;
import com.eazy.pay.dto.CardDTO;
import com.eazy.pay.model.Card;
import com.eazy.pay.model.CardBenefit;
import com.eazy.pay.model.Category;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class CardBenefitServiceTest {

    @Mock
    private CardBenefitRepository cardBenefitRepository;

    @InjectMocks
    private CardBenefitService cardBenefitService;

    @BeforeEach
    void setUp(){
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

        Category category1 = Category.builder()
                .uid(1L)
                .name("외식")
                .build();

        CardBenefit cardBenefit1 = CardBenefit.builder()
                .card(card1)
                .category(category1)
                .benefitRate(10)
                .build();

        CardBenefit cardBenefit2 = CardBenefit.builder()
                .card(card2)
                .category(category1)
                .benefitRate(1)
                .build();

        List<CardBenefit> cardBenefitList = Arrays.asList(cardBenefit1, cardBenefit2);

        when(cardBenefitRepository.findByCategoryUid(1L)).thenReturn(cardBenefitList);

    }


    @Test
    void getCategoryCards() {
        //when: getCategoryCards 메서드 실행 카테고리 uid가 1인 카드 목록을 조회
        List<CardDTO> result = cardBenefitService.getCategoryCards(1L);

        // then: 카테고리 uid가 1인 카드 목록이 조회되어야 함
        assertEquals(2, result.size());
        assertEquals("image1", result.get(0).getImage());
        assertEquals("외식 카드", result.get(0).getName());
        assertEquals(10_000, result.get(0).getAnnualFee());
        assertEquals(300_000, result.get(0).getPerformance());
        assertEquals(10_000, result.get(0).getBenefitLimit());
        assertEquals("외식에 좋은 카드", result.get(0).getInfo());
        assertEquals("신청1", result.get(0).getApplicationUrl());

        assertEquals("image2", result.get(1).getImage());
        assertEquals("무실적 카드", result.get(1).getName());
        assertEquals(10_000, result.get(1).getAnnualFee());
        assertEquals(0, result.get(1).getPerformance());
        assertEquals(999_999_999, result.get(1).getBenefitLimit());
        assertEquals("무실적 카드", result.get(1).getInfo());
        assertEquals("신청2", result.get(1).getApplicationUrl());

        // cardBenefitRepository.findByCategoryUid(1L) 메서드가 1번 호출되었는지 검증
        verify(cardBenefitRepository, times(1)).findByCategoryUid(1L);
    }
}