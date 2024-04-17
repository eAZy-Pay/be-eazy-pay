package com.eazy.pay.service;

import com.eazy.pay.dao.MonthlyFor6Repository;
import com.eazy.pay.dto.MonthlyFor6ResponseDTO;
import com.eazy.pay.model.MonthlyFor6;
import com.eazy.pay.model.User;
import com.eazy.pay.model.Category;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class) // MockitoExtension을 사용하여 Mock 객체를 주입받을 수 있도록 설정
class MonthlyFor6ServiceTest {

    @Mock
    private MonthlyFor6Repository monthlyFor6Repository;

    @InjectMocks
    private MonthlyFor6Service monthlyFor6Service;

    @BeforeEach
    void setUp() {
        User user = User.builder()
                .uid(1L)
                .name("User1")
                .build();

        Category category1 = Category.builder()
                .uid(1L)
                .name("외식")
                .build();

        Category category2 = Category.builder()
                .uid(2L)
                .name("문화/취미")
                .build();

        Category category3 = Category.builder()
                .uid(3L)
                .name("패션")
                .build();

        Category category4 = Category.builder()
                .uid(4L)
                .name("기타")
                .build();


        MonthlyFor6 monthly1 = MonthlyFor6.builder()
                .category(category1)
                .user(user)
                .useAmount(200_000)
                .build();

        MonthlyFor6 monthly2 = MonthlyFor6.builder()
                .category(category2)
                .user(user)
                .useAmount(100_000)
                .build();

        MonthlyFor6 monthly3 = MonthlyFor6.builder()
                .category(category3)
                .user(user)
                .useAmount(30_000)
                .build();

        MonthlyFor6 monthly4 = MonthlyFor6.builder()
                .category(category4)
                .user(user)
                .useAmount(0)
                .build();

        List<MonthlyFor6> monthlyList = Arrays.asList(monthly1, monthly2, monthly3, monthly4);

        when(monthlyFor6Repository.findByUserUid(1L)).thenReturn(monthlyList);
    }

    @Test
    void getMonthlyFor6ByUserId() {
        // when: getMonthlyFor6ByUserId 메서드 실행 user uid가 1인 6개월 통계 조회
        List<MonthlyFor6ResponseDTO> result = monthlyFor6Service.getMonthlyFor6ByUserId(1L);

        // then: user uid가 1인 6개월 통계가 조회되어야 함
        assertEquals(3, result.size());
        assertEquals("외식", result.get(0).getCategoryName());
        assertEquals(200_000, result.get(0).getUseAmount());

        assertEquals("문화/취미", result.get(1).getCategoryName());
        assertEquals(100_000, result.get(1).getUseAmount());

        assertEquals("패션", result.get(2).getCategoryName());
        assertEquals(30_000, result.get(2).getUseAmount());

        // monthlyFor6Repository.findByUserUid(1L) 메서드가 1번 호출되었는지 검증
        verify(monthlyFor6Repository, times(1)).findByUserUid(1L);
    }
}
