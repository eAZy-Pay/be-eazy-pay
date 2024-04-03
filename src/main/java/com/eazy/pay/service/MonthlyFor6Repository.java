package com.eazy.pay.service;

import com.eazy.pay.model.MonthlyFor6;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MonthlyFor6Repository extends JpaRepository<MonthlyFor6, Integer> {
    List<MonthlyFor6> findAll();
}
