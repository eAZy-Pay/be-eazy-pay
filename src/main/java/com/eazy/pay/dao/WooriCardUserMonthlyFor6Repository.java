package com.eazy.pay.dao;

import com.eazy.pay.model.MonthlyFor6;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.util.List;

@Repository
public interface WooriCardUserMonthlyFor6Repository extends JpaRepository<MonthlyFor6, Long> {

    @Query("SELECT m FROM MonthlyFor6 m WHERE m.user.id = :userId AND m.yearAndMonth = :date")
    List<MonthlyFor6> findByUserIdAndDate(@Param("userId") Long userId, @Param("date") Date date);

}