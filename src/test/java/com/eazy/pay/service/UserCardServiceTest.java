package com.eazy.pay.service;

import com.eazy.pay.dao.UserCardRepository;
import com.eazy.pay.dto.UserCardDTO;
import com.eazy.pay.model.Card;
import com.eazy.pay.model.User;
import com.eazy.pay.model.UserCard;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.autoconfigure.data.web.SpringDataWebProperties;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserCardServiceTest {

    @Mock
    private UserCardRepository userCardRepository;

    @InjectMocks
    private UserCardService userCardService;

    @BeforeEach
    void setUp() throws Exception {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date expirationDate = dateFormat.parse("2028-12-31");

        Card card1 = Card.builder()
                .uid(1L)
                .name("Test Card")
                .build();

        User user1 = User.builder()
                .uid(1L)
                .name("User1")
                .build();

        UserCard user1Card1 = UserCard.builder()
                .uid(1L)
                .card(card1)
                .user(user1)
                .num("1234567890123456")
                .expirationDate(expirationDate)
                .paymentLimit(1_000_000)
                .build();

        List<UserCard> user1Cards = List.of(user1Card1);

        lenient().when(userCardRepository.save(any(UserCard.class))).thenReturn(user1Card1);
        lenient().when(userCardRepository.findById(1L)).thenReturn(Optional.of(user1Card1));
        lenient().when(userCardRepository.findByUserUid(1L , PageRequest.of(0, 100))).thenReturn(user1Cards);
    }

    @Test
    void createUserCard() throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date expirationDate = dateFormat.parse("2028-12-31");

        UserCardDTO userCardDTO = UserCardDTO.builder()
                .cardId(1L)
                .userId(1L)
                .num("1234567890123456")
                .expirationDate(expirationDate)
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

}