package com.eazy.pay.dao;

import com.eazy.pay.model.UserCategoryHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserCatetgoryHistoryRepository extends JpaRepository<UserCategoryHistory, Long> {

    List<UserCategoryHistory> findByUserUid(Long userId);

    @Query("SELECT uch FROM UserCategoryHistory uch WHERE uch.user.uid = :userId " +
            "AND YEAR(uch.yearAndMonth) = YEAR(:date) " +
            "AND MONTH(uch.yearAndMonth) = MONTH(:date) " +
            "AND uch.benefitAmount > 0 "
    )
    List<UserCategoryHistory> findByUserUidAndDate(@Param("userId") Long userId, @Param("date") Date date);

    @Query("SELECT SUM(uch.benefitAmount) FROM UserCategoryHistory uch WHERE uch.user.uid = :userId " +
            "AND YEAR(uch.yearAndMonth) = YEAR(:date) "+
            "AND uch.benefitAmount > 0 "
    )
    Optional<Integer> findBenefitOfYearByUserUidAndDate(@Param("userId") Long userId, @Param("date") Date date);


    @Query("SELECT uch FROM UserCategoryHistory uch WHERE uch.user.uid = :userId " +
            "AND YEAR(uch.yearAndMonth) = YEAR(:date) " +
            "AND MONTH(uch.yearAndMonth) = MONTH(:date) "+
            "AND uch.category.uid = :categoryId "
    )
    Optional<UserCategoryHistory> findByUserUidAndCategoryIdAndDate(@Param("userId") Long userId, @Param("categoryId") Long categoryId, @Param("date") Date date);

}
