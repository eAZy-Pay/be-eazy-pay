package com.eazy.pay.controller;

import com.eazy.pay.dto.QnaDTO;
import com.eazy.pay.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/qnas")
public class QnaController {
    @Autowired
    private QuestionService questionService;


    @GetMapping
    public ResponseEntity getAllQuestion(@RequestParam(value = "uid", required = false) Long uid,
                                        @RequestParam(value = "page", required = false, defaultValue = "0") Integer page,
                                        @RequestParam(value = "size", required = false, defaultValue = "10") Integer size) {

        if (uid == null) {
            return ResponseEntity.ok(questionService.getAllQna(Pageable.ofSize(size).withPage(page)));
        }

        return ResponseEntity.ok(questionService.getQnaById(uid));
    }

    @GetMapping("/unanswered")
    public ResponseEntity getUnansweredQna(@RequestParam(value = "page", required = false, defaultValue = "0") Integer page,
                                          @RequestParam(value = "size", required = false, defaultValue = "10") Integer size) {

        return ResponseEntity.ok(questionService.getUnansweredQna(Pageable.ofSize(size).withPage(page)));
    }

    @PutMapping
    public ResponseEntity<Map<String, Object>> putQnaPost(@RequestBody QnaDTO dto) throws ParseException {
        Map<String, Object> responseMap = new HashMap<>();
        try {
            if(questionService.updateQna(dto)){
                responseMap.put("message", "QnA 질문이 성공적으로 수정되었습니다.");
                return ResponseEntity.status(HttpStatus.OK).body(responseMap);
            } else{
                responseMap.put("message", "서버 오류");
                return ResponseEntity.status(HttpStatus.CONFLICT).body(responseMap);
            }
        } catch (DataAccessException dae) { // 데이터베이스 관련 예외 처리
            responseMap.put("error", "데이터베이스 오류가 발생했습니다.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseMap);
        } catch (Exception ex) { // 일반적인 예외 처리
            responseMap.put("error", "서버 내부 오류가 발생했습니다. 나중에 다시 시도해주세요.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseMap);
        }
    }

    @PostMapping
    public  ResponseEntity<Map<String, Object>> postQnaPost(@RequestBody QnaDTO dto) throws ParseException {
        Map<String, Object> responseMap = new HashMap<>();
        try {
            if(questionService.createQna(dto)){
                responseMap.put("message", "QnA 질문이 성공적으로 등록되었습니다.");
                return ResponseEntity.status(HttpStatus.OK).body(responseMap);
            } else{
                responseMap.put("message", "서버 오류");
                return ResponseEntity.status(HttpStatus.CONFLICT).body(responseMap);
            }
        } catch (DataAccessException dae) { // 데이터베이스 관련 예외 처리
            responseMap.put("error", "데이터베이스 오류가 발생했습니다.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseMap);
        } catch (Exception ex) { // 일반적인 예외 처리
            responseMap.put("error", "서버 내부 오류가 발생했습니다. 나중에 다시 시도해주세요.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseMap);
        }
    }

    @DeleteMapping
    public ResponseEntity<Map<String, Object>> deleteQnaPost(@RequestParam(value = "uid") Long uid){
        Map<String, Object> responseMap = new HashMap<>();
        try {
            if (questionService.deleteQnaById(uid)) { // TODO: 조회 후 있으면 삭제 되도록 변경
                responseMap.put("message", "QnA 질문이 성공적으로 삭제되었습니다.");
                return ResponseEntity.status(HttpStatus.OK).body(responseMap);
            }
            else {
                responseMap.put("message", "서버 오류");
                return ResponseEntity.status(HttpStatus.CONFLICT).body(responseMap);
            }
        } catch (DataAccessException dae) { // 데이터베이스 관련 예외 처리
            responseMap.put("error", "데이터베이스 오류가 발생했습니다.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseMap);
        } catch (Exception ex) { // 일반적인 예외 처리
            responseMap.put("error", "서버 내부 오류가 발생했습니다. 나중에 다시 시도해주세요.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseMap);
        }
    }
}
