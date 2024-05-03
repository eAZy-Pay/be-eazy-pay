package com.eazy.pay.service;

import com.eazy.pay.dao.QuestionRepository;
import com.eazy.pay.dao.UserRepository;
import com.eazy.pay.dto.QnaDTO;
import com.eazy.pay.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class QuestionServiceTest {
    @Mock
    private QuestionRepository questionRepository;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private QuestionService questionService;

private Question question1;
private Question question2;
private User user1;
private User user2;

    @BeforeEach
    void setUp() {
        user1 = User.builder()
                .uid(1L)
                .name("User 1")
                .phoneNumber("010-1234-5678")
                .build();

        user2 = User.builder()
                .uid(2L)
                .name("박선주")
                .phoneNumber("010-1234-5678")
                .build();

        // 답변되지 않은 질문
        question1 = Question.builder()
                .uid(1L)
                .title("질문1")
                .content("내용1")
                .date(new Date())
                .answered(false)
                .answer("아직 답변되지 않았어요.")
                .user(user1)
                .build();

        // 답변이 완료된 질문

        question2 = Question.builder()
                .uid(2L)
                .title("질문2")
                .content("내용2")
                .date(new Date())
                .answered(true)
                .answer("세상에서 가장 쉬운 페이 이지페이!")
                .user(user2)
                .build();

        List<Question> questionList = Arrays.asList(question1, question2);

        PageRequest pageable = PageRequest.of(0, 10);
        Page<Question> questions = new PageImpl<>(questionList, pageable, questionList.size());

        lenient().when(questionRepository.findAll(pageable)).thenReturn(questions);
        lenient().when(questionRepository.findById(1L)).thenReturn(Optional.of(question1));
        lenient().when(questionRepository.findById(2L)).thenReturn(Optional.of(question2));
        lenient().when(questionRepository.save(question1)).thenReturn(question1);
        lenient().when(questionRepository.save(question2)).thenReturn(question2);
    }


    @Test
    void testGetAllQnas() { // 모든 질문을 가져와 DTO 형태로 변환
        // when
        List<QnaDTO> result = questionService.getAllQna();

        // then
        assertEquals(2, result.size());
        QnaDTO qnaDTO1 = result.get(0);
        assertEquals(1L, qnaDTO1.getUid());
        assertEquals("질문1", qnaDTO1.getTitle());
        assertEquals("내용1", qnaDTO1.getContent());
        assertEquals("User 1", qnaDTO1.getUserName());
        assertEquals("아직 답변되지 않았어요.", qnaDTO1.getAnswer());
        assertEquals(false, qnaDTO1.getAnswered());
        assertEquals(new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date()), qnaDTO1.getDate());
        assertEquals(1L, qnaDTO1.getUserId());


        QnaDTO qnaDTO2 = result.get(1);
        assertEquals(2L, qnaDTO2.getUid());
        assertEquals("질문2", qnaDTO2.getTitle());
        assertEquals("내용2", qnaDTO2.getContent());
        assertEquals("박선주", qnaDTO2.getUserName());
        assertEquals("세상에서 가장 쉬운 페이 이지페이!", qnaDTO2.getAnswer());
        assertEquals(true, qnaDTO2.getAnswered());
        assertEquals(new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date()), qnaDTO2.getDate());
        assertEquals(2L, qnaDTO2.getUserId());
    }

    @Test
    void testUpdateQna() throws ParseException { // 질문 수정이나 새 질문 추가
        // given
        QnaDTO dto = QnaDTO.builder()
                .uid(1L)
                .date(new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date()))
                .answer("이제 답변드렸어요.")
                .userId(1L)
                .title("새질문1")
                .content("새내용1")
                .userName("User 1")
                .answered(true)
                .build();

        // when
        boolean result = questionService.updateQna(dto);

        // then
        assertTrue(result);

        // 수정된 질문 객체 확인
        verify(questionRepository, atLeastOnce()).save(any());

        // 이미 수정된 질문 객체를 사용하여 검증
        assertEquals("이제 답변드렸어요.", dto.getAnswer());
        assertEquals(true, dto.getAnswered());
        assertEquals("새질문1", dto.getTitle());
        assertEquals("새내용1", dto.getContent());
        assertEquals("이제 답변드렸어요.", dto.getAnswer());
        assertEquals(true, dto.getAnswered());


    }


    @Test
    public void testDeleteQnaById() { // 질문 삭제
        // when
        boolean result = questionService.deleteQnaById(1L);
        boolean result2 = questionService.deleteQnaById(2L);

        // then
        assertTrue(result);
        assertTrue(result2);

        // 삭제된 질문 객체 확인
        verify(questionRepository, times(2)).deleteById(any());
    }


    @Test
    void testGetQnaById() {
        // given
        Long questionId = 1L;
        Question existingQuestion = Question.builder()
                .uid(questionId)
                .date(new Date())
                .title("질문1")
                .content("내용1")
                .answered(false)
                .answer("아직 답변되지 않았어요.")
                .user(User.builder().uid(1L).name("User 1").build())
                .build();

        when(questionRepository.findById(questionId)).thenReturn(Optional.of(existingQuestion));

        // when
        QnaDTO result = questionService.getQnaById(questionId);

        // then
        assertNotNull(result);
        assertEquals(questionId, result.getUid());
        assertEquals("질문1", result.getTitle());
        assertEquals("내용1", result.getContent());
        assertEquals("User 1", result.getUserName());
        assertEquals("아직 답변되지 않았어요.", result.getAnswer());
        assertFalse(result.getAnswered());
    }


    @Test
    public void testCreateQna() {

        QnaDTO qnaDTO3 = QnaDTO.builder()
                .date(new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date()))
                .answer("아직 답변되지 않았어요.")
                .userId(1L)
                .title("질문3")
                .content("내용3")
                .userName("User 1")
                .answered(false)
                .build();

        boolean result = questionService.createQna(qnaDTO3);

        assertTrue(result);
        // 새 질문 객체 확인
        assertEquals("질문3", qnaDTO3.getTitle());
        assertEquals("내용3", qnaDTO3.getContent());
        assertEquals("User 1", qnaDTO3.getUserName());
        assertEquals("아직 답변되지 않았어요.", qnaDTO3.getAnswer());
        assertEquals(false, qnaDTO3.getAnswered());
        assertEquals(new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date()), qnaDTO3.getDate());
        assertEquals(1L, qnaDTO3.getUserId());
        //assertEquals(3L, qnaDTO3.getUid()); //sql에서  AUTOINCREMENT로 설정되어 있어서 uid값이 자동으로 증가되어 저장됩니다.

    }

}
