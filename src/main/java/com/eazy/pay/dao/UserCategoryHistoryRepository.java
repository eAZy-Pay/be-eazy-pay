package com.eazy.pay.dao;

import com.eazy.pay.model.UserCategoryHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.util.List;

@Repository
public interface UserCategoryHistoryRepository extends JpaRepository<UserCategoryHistory, Long> {

    @Query("SELECT uch FROM UserCategoryHistory uch " +
            "WHERE uch.user.uid = :userId")
    List<UserCategoryHistory> findByUserId(@Param("userId") Long userId);

    @Query("SELECT uch FROM UserCategoryHistory uch " +
            "WHERE uch.user.uid = :userId " +
            "AND uch.yearAndMonth = :date")
    List<UserCategoryHistory> findByUserIdAndDate(@Param("userId") Long userId, @Param("date") Date date);
}
