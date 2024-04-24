package com.eazy.pay.service;

import com.eazy.pay.dao.CardBenefitRepository;
import com.eazy.pay.dto.CardDTO;
import com.eazy.pay.model.Card;
import com.eazy.pay.model.CardBenefit;
import com.eazy.pay.model.Category;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CardBenefitServiceTest {

    @Mock
    private CardBenefitRepository cardBenefitRepository;

    @InjectMocks
    private CardBenefitService cardBenefitService;

    @BeforeEach
    void setUp() {
        // 카드 및 카테고리 데이터 설정
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
                .card(card1)
                .category(category1)
                .benefitRate(10)
                .build();

        CardBenefit cardBenefit2 = CardBenefit.builder()
                .card(card2)
                .category(category1)
                .benefitRate(2)
                .build();

        CardBenefit cardBenefit3 = CardBenefit.builder()
                .card(card1)
                .category(category2)
                .benefitRate(2)
                .build();

        // Pageable 및 PageImpl을 사용하여 Page<CardBenefit> 생성
        Pageable pageable = PageRequest.of(0, 10);
        List<CardBenefit> cardBenefits = Arrays.asList(cardBenefit1, cardBenefit2);
        Page<CardBenefit> cardBenefitPage = new PageImpl<>(cardBenefits, pageable, cardBenefits.size());

        when(cardBenefitRepository.findByCategoryUid(1L, pageable)).thenReturn(cardBenefitPage);
    }

    @Test
    void getCardsByCategoryId() {
        Pageable pageable = PageRequest.of(0, 10);

        // getCardsByCategoryId 메서드 실행 (페이징을 추가)
        Page<CardDTO> result = cardBenefitService.getCardsByCategoryId(1L, pageable);

        // 카테고리 uid가 1인 카드 목록이 조회되어야 함
        assertEquals(2, result.getContent().size()); // size로 페이지 콘텐츠 수 확인
        assertEquals("외식 카드", result.getContent().get(0).getName()); // 첫 번째 카드 이름 확인
        assertEquals("무실적 카드", result.getContent().get(1).getName()); // 두 번째 카드 이름 확인

        // cardBenefitRepository.findByCategoryUid(1L, pageable) 메서드가 1번 호출되었는지 검증
        verify(cardBenefitRepository, times(1)).findByCategoryUid(1L, pageable);
    }
}
