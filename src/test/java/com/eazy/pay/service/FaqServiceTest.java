package com.eazy.pay.service;

import com.eazy.pay.dao.FaqRepository;
import com.eazy.pay.dao.UserRepository;
import com.eazy.pay.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FaqServiceTest {
    @Mock
    private FaqRepository faqRepository;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private FaqService faqService;

    @BeforeEach
    void setUp() {
        // given
        Faq faq1 = Faq.builder()
                .uid(1L)
                .title("질문1")
                .answer("답변1")
                .build();
        Faq faq2 = Faq.builder()
                .uid(2L)
                .title("질문2")
                .answer("답변2")
                .build();

        List<Faq> faqList = Arrays.asList(faq1, faq2);

        // FaqRepository의 findAll() 메서드가 호출될 때 반환할 값 설정
        when(faqRepository.findAll()).thenReturn(faqList);
    }


    @Test
    void testGetAllFaqs() {
        // when
        List<Faq> result = faqService.getAllFaqs();

        // then
        // FaqRepository의 findAll() 메서드가 한 번 호출되었는지 확인
        verify(faqRepository, times(1)).findAll();

        // 반환된 값이 예상한 값과 같은지 확인
        assertEquals(2, result.size());
        assertEquals("질문1", result.get(0).getTitle());
        assertEquals("답변1", result.get(0).getAnswer());
        assertEquals("질문2", result.get(1).getTitle());
        assertEquals("답변2", result.get(1).getAnswer());
    }

}
