package com.eazy.pay.dao;

import com.eazy.pay.dto.MonthlyFor6ResponseDTO;
import com.eazy.pay.model.MonthlyFor6;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MonthlyFor6Repository extends JpaRepository<MonthlyFor6, Long> {

    List<MonthlyFor6> findByUserUid(Long userId);
}