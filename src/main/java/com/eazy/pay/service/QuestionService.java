package com.eazy.pay.service;

import com.eazy.pay.dao.NotificationRepository;
import com.eazy.pay.dao.UserRepository;
import com.eazy.pay.dto.QnaDTO;
import com.eazy.pay.mapper.QnaMapper;
import com.eazy.pay.model.Notification;
import com.eazy.pay.model.Question;
import com.eazy.pay.dao.QuestionRepository;
import com.eazy.pay.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class QuestionService {

    @Autowired
    private QuestionRepository questionRepository;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    public Page<QnaDTO> getAllQna(Pageable pageable) {
        return questionRepository.findAll(pageable).
                map(QnaMapper.INSTANCE::toDTO);
    }

    public boolean updateQna(QnaDTO qnaDTO) throws ParseException {
        if (qnaDTO.getUid() != null) {
            Question existingQna = questionRepository.findById(qnaDTO.getUid())
                    .orElseThrow(() -> new IllegalArgumentException("해당하는 질문이 없습니다."));

            System.out.println("existingQna: " + existingQna);
            // 답변이 이미 처리된 경우 충돌 처리
            if (existingQna.isAnswered() && qnaDTO.getAnswer() != null) {
                throw new IllegalStateException("이미 답변된 질문입니다.");
            }

            if (qnaDTO.getAnswer() != null && !qnaDTO.getAnswer().isEmpty()) {
                existingQna.setAnswer(qnaDTO.getAnswer());
                existingQna.setAnswered(true);
                Notification notification = Notification.builder()
                        .user(existingQna.getUser())
                        .message("질문에 대한 답변이 도착했습니다.")
                        .createdAt(LocalDateTime.now())
                        .activeRead(false)
                        .build();
                notificationRepository.save(notification);
            }

            if (qnaDTO.getTitle() != null && !qnaDTO.getTitle().isEmpty()) {
                existingQna.setTitle(qnaDTO.getTitle());
            }

            if (qnaDTO.getContent() != null && !qnaDTO.getContent().isEmpty()) {
                existingQna.setContent(qnaDTO.getContent());
            }

            System.out.println("existingQna: " + existingQna);

            questionRepository.save(existingQna);
            return true;
        }
        return false;
    }


    public boolean deleteQnaById(Long uid) {
        questionRepository.deleteById(uid);
        return true;
    }

    public QnaDTO getQnaById(Long uid) {
        return QnaMapper.INSTANCE.toDTO(questionRepository.findById(uid).orElseThrow(
                () -> new IllegalArgumentException("해당하는 질문이 없습니다.")
        ));
    }

    public boolean createQna(QnaDTO dto) {
        Question newQuestion = Question.builder()
                .user(userRepository.findById(dto.getUserId()).orElseThrow(
                        () -> new IllegalArgumentException("해당하는 사용자가 없습니다.")
                ))
                .title(dto.getTitle())
                .content(dto.getContent())
                .date(Date.from(Instant.now()))
                .answered(false)
                .answer("아직 답변되지 않았어요.")
                .build();

        questionRepository.save(newQuestion);
        return true;
    }

    public Page<QnaDTO> getUnansweredQna(Pageable pageable) {

        return questionRepository.findByAnswered(false, pageable)
                .map(QnaMapper.INSTANCE::toDTO);
    }
}
