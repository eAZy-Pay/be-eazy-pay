package com.eazy.pay.service;

import com.eazy.pay.dao.PaymentHistoryRepository;
import com.eazy.pay.dao.UserCardRepository;
import com.eazy.pay.dao.UserCategoryHistoryRepository;
import com.eazy.pay.dto.BenefitAndSimpleUserCardsDTO;
import com.eazy.pay.dto.CardUsageSummaryDTO;
import com.eazy.pay.dto.SimpleUserCardDTO;
import com.eazy.pay.dto.UserCardDTO;
import com.eazy.pay.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserCardServiceTest {

    @Mock
    private UserCardRepository userCardRepository;

    @Mock
    private PaymentHistoryRepository paymentHistoryRepository;

    @Mock
    private UserCategoryHistoryRepository userCategoryHistoryRepository;

    @InjectMocks
    private UserCardService userCardService;

    int dashboardMonth = 1; //메인페이지에서 1개월 데이터를 보여줌
    UserCard user1Card1;
    PaymentHistory paymentHistory1;
    PaymentHistory paymentHistory2;
    UserCategoryHistory userCategoryHistory;
    UserCategoryHistory userCategoryHistory2;
    Card card1;
    Card card2;
    @BeforeEach
    void setUp() throws Exception {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date expirationDate = dateFormat.parse("2028-12-31");
        Date thisYearDate = dateFormat.parse(String.valueOf(LocalDate.now()));
        card1 = Card.builder()

                .uid(1L)
                .name("Test Card")
                .benefitLimit(100000)
                .performance(300_000)
                .annualFee(10000)
                .build();

        card2 = Card.builder()
                .uid(2L)
                .benefitLimit(100000)
                .performance(300_000)
                .annualFee(10000)
                .name("Test Card B")
                .build();

        User user1 = User.builder()
                .uid(1L)
                .name("User1")
                .build();

        user1Card1 = UserCard.builder()
                .uid(1L)
                .card(card1)
                .user(user1)
                .num("1234567890123456")
                .expirationDate(new java.sql.Date(expirationDate.getTime()))
                .paymentLimit(1_000_000)
                .cardValid(true)
                .linkEazy(true)
                .build();

        paymentHistory1 = PaymentHistory.builder()
                .uid(1L)
                .cardNum("1234567890123456")
                .paymentAmount(10_000)
                .benefitAmount(1000)
                .storeName("Test StoreA")
                .paymentDate(thisYearDate)
                .build();

        paymentHistory2 = PaymentHistory.builder()
                .uid(2L)
                .cardNum("1234567890123456")
                .paymentAmount(10_000)
                .benefitAmount(0)
                .storeName("Test StoreB")
                .paymentDate(thisYearDate)
                .build();

        userCategoryHistory = UserCategoryHistory.builder()
                .benefitAmount(1000)
                .useAmount(10_000)
                .yearAndMonth(new java.sql.Date(thisYearDate.getTime()))
                .build();

        userCategoryHistory2 = UserCategoryHistory.builder()
                .benefitAmount(0)
                .useAmount(10_000)
                .yearAndMonth(new java.sql.Date(thisYearDate.getTime()))
                .build();

        List<UserCard> user1Cards = List.of(user1Card1);

        lenient().when(userCardRepository.save(any(UserCard.class))).thenReturn(user1Card1);
        lenient().when(userCardRepository.findByUid(1L)).thenReturn(Optional.of(user1Card1));
        lenient().when(userCardRepository.findByUserUid(1L)).thenReturn(user1Cards);
        lenient().when(paymentHistoryRepository.findByCardNumAndDateWithinDate(
                "1234567890123456",
                Timestamp.valueOf(LocalDate.now().minusMonths( dashboardMonth- 1).withDayOfMonth(1).atStartOfDay()),
                Timestamp.valueOf(LocalDate.now().plusDays(1).atStartOfDay())
                )
        ).thenReturn(List.of(paymentHistory1, paymentHistory2));

        // 올해
        lenient().when(userCategoryHistoryRepository.findBenefitOfYearByUserUidAndDate(1L,new java.sql.Date(thisYearDate.getTime())))
        .thenReturn(userCategoryHistory.getBenefitAmount());

        //이번 달
        lenient().when(userCategoryHistoryRepository.findByUserIdAndDate(1L, new java.sql.Date(thisYearDate.getTime())))
                .thenReturn(List.of(userCategoryHistory2));

    }

    @Test
    void getSimpleBenefitDashboardByUserId(){
        List<SimpleUserCardDTO> sorted= List.of(
                SimpleUserCardDTO.builder()
                        .uid(1L)
                        .num(user1Card1.getNum())
                        .benefitAmount(1000)
                        .useAmount(20_000)
                        .paymentLimit(1_000_000)
                        .expirationDate(user1Card1.getExpirationDate())
                        .cardValid(user1Card1.isCardValid())
                        .linkEazy(user1Card1.isLinkEazy())
                        .card(card1)
                        .build()
        );

        BenefitAndSimpleUserCardsDTO expect =
                BenefitAndSimpleUserCardsDTO.builder()
                        .totalBenefitAmount(sorted.stream().mapToInt(SimpleUserCardDTO::getBenefitAmount).sum())
                        .totalPaymentLimit(sorted.stream().mapToInt(SimpleUserCardDTO::getPaymentLimit).sum())
                        .availableFunds(1_000_000 - 20_000)
                        .cards(sorted)
                        .build();

        BenefitAndSimpleUserCardsDTO result = userCardService.getSimpleBenefitDashboardByUserId(1L, dashboardMonth, 4 );
        assertEquals(result.getTotalBenefitAmount(), expect.getTotalBenefitAmount());
        assertEquals(result.getTotalPaymentLimit(), expect.getTotalPaymentLimit());
        assertEquals(result.getAvailableFunds(), expect.getAvailableFunds());
        assertEquals(result.getCards().toString(), expect.getCards().toString()); //객체끼리 비교를 위해 toString으로 비교

    }
    @Test
    void sortUserCards(){
        // 테스트용 데이터 생성
        List<SimpleUserCardDTO> expect = new ArrayList<>();
        expect.add(SimpleUserCardDTO.builder().uid(3L).card(card1).useAmount(3000000).benefitAmount(10000).build());
        expect.add(SimpleUserCardDTO.builder().uid(4L).card(card1).useAmount(250000).benefitAmount(10000).build());
        expect.add(SimpleUserCardDTO.builder().uid(1L).card(card1).useAmount(50).benefitAmount(100).build());
        expect.add(SimpleUserCardDTO.builder().uid(2L).card(card2).useAmount(30).benefitAmount(80).build());

        List<SimpleUserCardDTO> cards = new ArrayList<>();
        cards.add(SimpleUserCardDTO.builder().uid(1L).card(card1).useAmount(50).benefitAmount(100).build());
        cards.add(SimpleUserCardDTO.builder().uid(2L).card(card2).useAmount(30).benefitAmount(80).build());
        cards.add(SimpleUserCardDTO.builder().uid(3L).card(card1).useAmount(3000000).benefitAmount(10000).build());
        cards.add(SimpleUserCardDTO.builder().uid(4L).card(card1).useAmount(250000).benefitAmount(10000).build());
        // 테스트 코드의 나머지 부분은 필요한 만큼 카드를 추가하고 필드 값을 설정합니다.

        // 정렬 메서드 호출
        userCardService.sortUserCards(cards);

        // 예상된 정렬 순서대로 정렬되었는지 확인
        assertEquals(expect.get(0).getUid(), cards.get(0).getUid());  // 예상된 순서의 uid를 순서대로 확인합니다.
        assertEquals(expect.get(1).getUid(), cards.get(1).getUid());
        assertEquals(expect.get(2).getUid(), cards.get(2).getUid());
        assertEquals(expect.get(3).getUid(), cards.get(3).getUid());



    }
    @Test
    void createUserCard() throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date expirationDate = new Date(dateFormat.parse("2028-12-31").getTime());

        UserCardDTO userCardDTO = UserCardDTO.builder()
                .cardId(1L)
                .userId(1L)
                .num("1234567890123456")
                .expirationDate(new java.sql.Date(expirationDate.getTime()))
                .paymentLimit(1_000_000)
                .build();

        userCardService.createUserCard(userCardDTO);

        // ArgumentCaptor를 사용하여 save 메소드에 전달된 UserCard 객체를 캡쳐
        ArgumentCaptor<UserCard> userCardCaptor = ArgumentCaptor.forClass(UserCard.class);
        verify(userCardRepository, times(1)).save(userCardCaptor.capture());

        // createUserCard 메소드에 전달된 UserCardDTO 객체와 save 메소드에 전달된 UserCard 객체의 필드값이 일치하는지 확인
        UserCard savedUserCard = userCardCaptor.getValue();
        assertEquals("1234567890123456", savedUserCard.getNum());
        assertEquals(1L, savedUserCard.getUser().getUid());
        assertEquals(1L, savedUserCard.getCard().getUid());
        assertEquals(1_000_000, savedUserCard.getPaymentLimit());
    }

    @Test
    void testDisableUserCard() {
        Long userCardId = 1L;

        userCardService.disableUserCard(userCardId);

        ArgumentCaptor<UserCard> userCardCaptor = ArgumentCaptor.forClass(UserCard.class);
        verify(userCardRepository).save(userCardCaptor.capture());

        UserCard savedUserCard = userCardCaptor.getValue();
        // cardValid가 false로 변경되었는지 확인
        assertFalse(savedUserCard.isCardValid());
    }

    @Test
    void testCheckUserCard() {
        Long userId = 1L;
        Long cardId = 1L;

        assertTrue(userCardService.checkUserCard(userId, cardId));
    }

    @Test
    void testGetCardUsageSummary(){

        CardUsageSummaryDTO expect = CardUsageSummaryDTO.builder()
                .totalAnnualFee(user1Card1.getCard().getAnnualFee())
                .benefitOfMonth(paymentHistory2.getBenefitAmount())
                .benefitOfYear(paymentHistory1.getBenefitAmount())
                .build();

        CardUsageSummaryDTO result = userCardService.getCardUsageSummary(1L);

        assertEquals(expect.getTotalAnnualFee(), result.getTotalAnnualFee());
        assertEquals(expect.getBenefitOfMonth(), result.getBenefitOfMonth());
       // assertEquals(expect.getBenefitOfYear(), result.getBenefitOfYear());
    }
}