package com.eazy.pay.dao;

import com.eazy.pay.model.UsageStatistic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.util.List;

@Repository
public interface UsageStatisticsRepository extends JpaRepository<UsageStatistic, Long> {

    @Query("SELECT u FROM UsageStatistic u WHERE u.age = :age AND u.yearAndMonth = :date")
    List<UsageStatistic> findByAgeAndDate(@Param("age") Integer age, @Param("date") Date date);

}
