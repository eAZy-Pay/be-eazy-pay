package com.eazy.pay.service;

import com.eazy.pay.dao.UserRepository;
import com.eazy.pay.dto.QnaDTO;
import com.eazy.pay.model.Question;
import com.eazy.pay.dao.QuestionRepository;
import com.eazy.pay.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class QuestionService {

    @Autowired
    private QuestionRepository questionRepository;
    @Autowired
    private UserRepository userRepository;

    public List<QnaDTO> getAllQnas() {
        List<Question> questionsList = questionRepository.findAll();
        List<QnaDTO> qnasDTOList = new ArrayList<QnaDTO>();

        for(Question q : questionsList){
            QnaDTO dto = QnaDTO.builder()
                    .uid(q.getUid())
                    .date(new SimpleDateFormat("yyyy-MM-dd HH:mm").format(q.getDate()))
                    .title(q.getTitle())
                    .content(q.getContent())
                    .userId(q.getUser().getUid())
                    .userName(q.getUser().getName())
                    .isAnswered(q.isAnswered())
                    .answer(q.getAnswer())
                    .build();
            qnasDTOList.add(dto);
        }

        return qnasDTOList;
    }
    public boolean updateQna(QnaDTO dto) throws ParseException {
        if(dto.getUid()!=null){
            Question existingQna = questionRepository.findById(dto.getUid()).orElse(null);
            existingQna.setTitle(dto.getTitle());
            existingQna.setContent(dto.getContent());
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
        Question q = questionRepository.findById(uid).orElse(null);
        if (q != null) {
            return QnaDTO.builder()
                    .uid(q.getUid())
                    .date(new SimpleDateFormat("yyyy-MM-dd HH:mm").format(q.getDate()))
                    .title(q.getTitle())
                    .content(q.getContent())
                    .userId(q.getUser().getUid())
                    .userName(q.getUser().getName())
                    .isAnswered(q.isAnswered())
                    .answer(q.getAnswer())
                    .build();
        } else {
            return QnaDTO.builder().build();
        }
    }

    public boolean createQna(QnaDTO dto) {
        Question newQuestion = null;
        newQuestion = Question.builder()
                .user(userRepository.findById(dto.getUserId()).orElse(null))
                .title(dto.getTitle())
                .content(dto.getContent())
                .date(new Date())
                .isAnswered(false)
                .answer("아직 답변되지 않았어요.")
                .build();
        questionRepository.save(newQuestion);
        return true;
    }
}
