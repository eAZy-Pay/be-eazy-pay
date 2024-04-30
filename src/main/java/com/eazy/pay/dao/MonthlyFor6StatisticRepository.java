package com.eazy.pay.dao;

import com.eazy.pay.model.MonthlyFor6;
import com.eazy.pay.model.MonthlyFor6Statistic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.util.List;

@Repository
public interface MonthlyFor6StatisticRepository extends JpaRepository<MonthlyFor6Statistic, Long> {
    @Query("SELECT m FROM MonthlyFor6Statistic m WHERE m.age = :age AND m.yearAndMonth = :date")
    List<MonthlyFor6Statistic> findByAgeAndDate(@Param("age") Integer age, @Param("date") Date date);
}
