package com.eazy.pay.dao;

import com.eazy.pay.model.UserCard;
import com.eazy.pay.model.UserCardHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserCardHistoryRepository extends JpaRepository<UserCardHistory, Long> {

    @Query("SELECT uch FROM UserCardHistory uch " +
            "WHERE uch.userCard.uid = :userCardId " +
            "AND MONTH(uch.yearAndMonth) = MONTH(:now) " +
            "AND YEAR(uch.yearAndMonth) = YEAR(:now) ")
    Optional<UserCardHistory> findByUserCardIdAndDate(@Param("userCardId") Long userCardId, @Param("now")  Date date);



    /*
    여러 개의 조회 결과를 담은 Optional을 반환하는 것은 보편적으로 사용되지 않습니다. Optional은 보통 값이 있을 수도 있고 없을 수도 있는 경우에 사용됩니다. 여러 개의 결과가 반환될 수 있는 경우에는 Optional이 아니라 그냥 List를 반환하는 것이 일반적입니다.
    * */

    @Query("SELECT uch.userCard FROM UserCardHistory uch " +
            "WHERE uch.userCard.user.uid = :userId " +
            //지난달의 정보
            "AND MONTH(uch.yearAndMonth) = MONTH(:now) -1 " +
            "AND YEAR(uch.yearAndMonth) = YEAR(:now) "+
            // 카드 실적 채움
            "AND uch.is_fulfilled = true " +
            // 해당 카테고리의 혜택을 가지고 있음
            "AND uch.userCard.card IN (SELECT cb.card FROM CardBenefit cb WHERE cb.category.uid = :categoryId) "+
            // 혜택을 받을 한도를 초과하지 않음 - 스칼라 값 (scalar value)받기 위해 SUM() 사용
            "AND (SELECT SUM(uch.benefitAmount) FROM UserCardHistory uch " +
            "WHERE uch.userCard.user.uid = :userId "+
            "AND MONTH(uch.yearAndMonth) = MONTH(:now) AND YEAR(uch.yearAndMonth) = YEAR(:now)) "+
            "< uch.userCard.card.benefitLimit"
            )
    List<UserCard> findHaveBenefitCardsByUserIdAndCategoryId(@Param("userId") Long userId,
                                                                 @Param("categoryId") Long categoryId,
                                                                 @Param("now") Date date);
}