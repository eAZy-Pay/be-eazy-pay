package com.eazy.pay.controller;

import com.eazy.pay.dto.QnaDTO;
import com.eazy.pay.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.List;

@RestController
@RequestMapping("/api/qnas")
public class QnaController {
    @Autowired
    private QuestionService questionService;


    @GetMapping
    public Object getAllQuestion(@RequestParam(value = "uid", required = false) Long uid) {
        if (uid == null) {
            List<QnaDTO> qnaDTOList = questionService.getAllQnas();
            return qnaDTOList;
        }else{
            QnaDTO qnaDTO = questionService.getQnaById(uid);
            if(qnaDTO.getUid() == null){return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("No such qna");}
            return qnaDTO;
        }
    }

    @PutMapping
    public ResponseEntity putQnaPost(@RequestBody QnaDTO dto) throws ParseException {
        if(questionService.updateQna(dto)){
            return new ResponseEntity<>("게시글이 성공적으로 등록되었습니다.", HttpStatus.OK);
        } else{
            return new ResponseEntity<>("비정상", HttpStatus.CONFLICT);
        }
    }

    @PostMapping
    public  ResponseEntity postQnaPost(@RequestBody QnaDTO dto) throws ParseException {
        if(questionService.createQna(dto)){
            return new ResponseEntity<>("게시글이 성공적으로 등록되었습니다.", HttpStatus.OK);
        } else{
            return new ResponseEntity<>("비정상", HttpStatus.CONFLICT);
        }
    }

    @DeleteMapping
    public ResponseEntity deleteQnaPost(@RequestParam(value = "uid") Long uid){
        if (questionService.deleteQnaById(uid)) { // TODO: 조회 후 있으면 삭제 되도록 변경
            return ResponseEntity.ok(HttpStatus.OK);
        }
        else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("No such qna");
        }
    }
}
