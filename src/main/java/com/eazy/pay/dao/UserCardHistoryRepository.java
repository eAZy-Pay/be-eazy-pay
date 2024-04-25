package com.eazy.pay.dao;

import com.eazy.pay.model.UserCard;
import com.eazy.pay.model.UserCardHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.util.List;

@Repository
public interface UserCardHistoryRepository extends JpaRepository<UserCardHistory, Long> {

    @Query("SELECT uch.userCard FROM UserCardHistory uch " +
            "WHERE uch.userCard.user.uid = ?1 " +
            "AND uch.yearAndMonth >= ?2 " +
            "AND uch.yearAndMonth <= ?3 " +
            "AND uch.is_fulfilled = true "+
            "AND uch.userCard.paymentLimit >="+
            "(" +
            "SELECT COALESCE(MIN(subUch.useAmount + ?5), 0) FROM UserCardHistory subUch " + //coalesce: null이면 0으로 대체
            "     WHERE MONTH(subUch.yearAndMonth) = MONTH(?4) " +
            "     AND YEAR(subUch.yearAndMonth) = YEAR(?4)" +
            ")" +
            "AND uch.userCard.cardValid = true")
    List<UserCard> findFulfilledByUserIdAndDate(Long userId, Date start, Date end, Date now, Integer price); //전월 카드 실적 확인, 이번달 한도 확인

    @Query("SELECT uch FROM UserCardHistory uch " +
            "WHERE uch.userCard.uid = ?1 " +
            "AND MONTH(uch.yearAndMonth) = MONTH(?2) " +
            "AND YEAR(uch.yearAndMonth) = YEAR(?2) ")
    UserCardHistory findByUserCardIdAndDate(Long userCardId, Date date);
}