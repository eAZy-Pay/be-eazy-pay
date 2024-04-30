package com.eazy.pay.service;

import com.eazy.pay.dao.UserCardHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.time.LocalDate;

@Service
public class UserCardHistoryService {

    @Autowired
    UserCardHistoryRepository userCardHistoryRepository;

    public boolean getUserCardHistoryFulfilled(Long userCardId) {
        // 현재 날짜 가져오기
        LocalDate currentDate = LocalDate.now();

        // 저번 달의 날짜를 가져옴
        LocalDate lastMonthDate = currentDate.minusMonths(1);

        // 해당 날짜를 Date 타입으로 변환
        Date lastMonthDateSql = Date.valueOf(lastMonthDate.withDayOfMonth(1));

        // 쿼리 결과
        Boolean result = userCardHistoryRepository.findIsFulfilledByUserCardIdAndDate(userCardId, lastMonthDateSql);

        // 쿼리 결과가 null이면 기본값으로 false를 반환
        return result != null ? result : false;
    }
}
