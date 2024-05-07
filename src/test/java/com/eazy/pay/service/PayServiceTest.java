package com.eazy.pay.service;

import com.eazy.pay.dao.*;
import com.eazy.pay.dto.CardToPayListRequestDTO;
import com.eazy.pay.dto.CompletedPaymentDTO;
import com.eazy.pay.dto.PayUserRequestDTO;
import com.eazy.pay.dto.UserPayCardDTO;
import com.eazy.pay.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
public class PayServiceTest {
    @Mock
    private UserCardRepository userCardRepository;
    @Mock
    private UserCardHistoryRepository userCardHistoryRepository;
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private PaymentHistoryRepository paymentHistoryRepository;
    @Mock
    private UserCategoryHistoryRepository userCategoryHistoryRepository;

    @InjectMocks
    private PayService payService;

    private Card card1;
    private Card card2;
    private UserCard user1Card1;
    private UserCard user1Card2;

    @BeforeEach
    void setUp() throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date expirationDate = dateFormat.parse("2028-12-31");
        Date thisYearDate = dateFormat.parse(String.valueOf(LocalDate.now()));

        Category category1 = Category.builder()
                .uid(1L)
                .name("생활/주거")
                .build();
        card1 = Card.builder()
                .uid(1L)
                .name("Test Card A")
                .benefitList(List.of(
                        CardBenefit.
                                builder().
                                benefitRate(10).
                                card(card1).
                                category(category1)
                                .build()
                ))
                .image("testImage1")
                .benefitLimit(100000)
                .performance(300_000)
                .annualFee(10000)
                .build();

        card2 = Card.builder()
                .uid(2L)
                .name("Test Card B")
                .benefitList(List.of(
                                CardBenefit.
                                        builder().
                                        card(card2).
                                        benefitRate(5).
                                        category(Category.builder()
                                                .uid(2L)
                                                .name("슈퍼/마트")
                                                .build()
                                        )
                                        .build()
                        )
                )
                .image("testImage2")
                .benefitLimit(100000)
                .performance(300_000)
                .annualFee(10000)
                .build();

        User user1 = User.builder()
                .uid(1L)
                .name("User1")
                .build();


        user1Card1 = UserCard.builder()
                .uid(1L)
                .card(card1)
                .user(user1)
                .num("1111111111111111")
                .expirationDate(new java.sql.Date(expirationDate.getTime()))
                .paymentLimit(1_000_000)
                .cardValid(true)
                .linkEazy(true)
                .build();

        user1Card2 = UserCard.builder()
                .uid(2L)
                .card(card2)
                .user(user1)
                .num("2222222222222222")
                .expirationDate(new java.sql.Date(expirationDate.getTime()))
                .paymentLimit(1_000_000)
                .cardValid(true)
                .linkEazy(true)
                .build();


        UserCardHistory lastUserCardHistory1 = UserCardHistory.builder()
                .isFulfilled(true)
                .benefitAmount(1000)
                .useAmount(300_000)
                .yearAndMonth(java.sql.Date.valueOf(LocalDate.now().minusMonths(1)))
                .build();

        UserCardHistory lastUserCardHistory2 = UserCardHistory.builder()
                .isFulfilled(true)
                .benefitAmount(1000)
                .useAmount(300_000)
                .yearAndMonth(java.sql.Date.valueOf(LocalDate.now().minusMonths(1)))
                .build();
        UserCardHistory userCardHistory1 = UserCardHistory.builder()
                .isFulfilled(true)
                .benefitAmount(1000)
                .useAmount(10_00)
                .yearAndMonth(new java.sql.Date(thisYearDate.getTime()))
                .build();

        UserCardHistory userCardHistory2 = UserCardHistory.builder()
                .isFulfilled(true)
                .benefitAmount(1000)
                .useAmount(10_00)
                .yearAndMonth(new java.sql.Date(thisYearDate.getTime()))
                .build();

        PaymentHistory paymentHistory = PaymentHistory.builder()
                .benefitAmount(1000)
                .storeName("Test Store")
                .categoryName("생활/주거")
                .storeCode("1111111111")
                .paymentDate(new java.sql.Date(thisYearDate.getTime()))
                .paymentAmount(1000)
                .build();

        UserCategoryHistory userCategoryHistory1 = UserCategoryHistory.builder()
                .benefitAmount(0)
                .useAmount(1000)
                .category(category1)
                .user(user1)
                .build();

        // 예시
        lenient().when(userCardRepository.findByUserId(1L)).thenReturn(List.of(user1Card1, user1Card2));
        lenient().when(userCardHistoryRepository.findByUserCardIdAndDate(1L, java.sql.Date.valueOf(LocalDate.now().minusMonths(1).withDayOfMonth(1))))
                .thenReturn(Optional.ofNullable(lastUserCardHistory1));
        lenient().when(userCardHistoryRepository.findByUserCardIdAndDate(2L, java.sql.Date.valueOf(LocalDate.now().minusMonths(1).withDayOfMonth(1))))
                .thenReturn(Optional.ofNullable(lastUserCardHistory2));
        lenient().when(userCardHistoryRepository.findByUserCardIdAndDate(1L, java.sql.Date.valueOf(LocalDate.now())))
                .thenReturn(Optional.ofNullable(userCardHistory1));
        lenient().when(userCardHistoryRepository.findByUserCardIdAndDate(1L, java.sql.Date.valueOf(LocalDate.now())))
                .thenReturn(Optional.ofNullable(userCardHistory2));
        lenient().when(userCardRepository.findById(1L)).thenReturn(Optional.ofNullable(user1Card1));
        lenient().when(categoryRepository.findById(1L)).thenReturn(Optional.ofNullable(Category.builder().uid(1L).name("생활/주거").build()));
        lenient().when(categoryRepository.findById(2L)).thenReturn(Optional.ofNullable(Category.builder().uid(2L).name("슈퍼/마트").build()));
        lenient().when(paymentHistoryRepository.save(paymentHistory)).thenReturn(paymentHistory);
        lenient().when(userCategoryHistoryRepository.save(userCategoryHistory1)).thenReturn(userCategoryHistory1);
    }


    @Test
    void testGetCardListToPay() {
        Long userId = 1L;
        Long categoryId = 1L;
        int price = 1000;

        CardToPayListRequestDTO requestDTO =
                CardToPayListRequestDTO.builder()
                        .userId(userId)
                        .categoryId(categoryId)
                        .price(price)
                        .build();


        UserPayCardDTO expect1 =
                UserPayCardDTO.builder()
                        .cardId(1L)
                        .payback(100)
                        .cardImage(card1.getImage())
                        .cardName(card1.getName())
                        .build();
        UserPayCardDTO expect2 =
                UserPayCardDTO.builder()
                        .cardId(2L)
                        .payback(0)
                        .cardImage(card2.getImage())
                        .cardName(card2.getName())
                        .build();
        List<UserPayCardDTO> expect = List.of(expect1, expect2);

        List<UserPayCardDTO> result = payService.getCardListToPay(requestDTO);
        assertEquals(2, result.size());
        assertEquals(expect.toString(), result.toString());
    }

    @Test
    void testuserPay() {
        Long userId = 1L;
        Long cardId = 1L;
        int price = 1000;

        PayUserRequestDTO requestDTO =
                PayUserRequestDTO.builder()
                        .userId(userId)
                        .cardId(cardId)
                        .categoryId(1L)
                        .price(price)
                        .build();

        CompletedPaymentDTO expect =
                CompletedPaymentDTO.builder()
                        .price(1000)
                        .payback(100)
                        .cardName("Test Card A")
                        .cardImage("testImage1")
                        .build();

        Object result = payService.userPay(requestDTO);
        assertEquals(expect.toString(), result.toString());
    }
}
