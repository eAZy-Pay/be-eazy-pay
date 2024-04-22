package com.eazy.pay.dao;
import com.eazy.pay.model.PaymentHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentHistoryRepository extends JpaRepository<PaymentHistory, Long> {
    List<PaymentHistory> findAll();
    @Query("SELECT ph FROM PaymentHistory ph WHERE ph.cardNum = ?1 AND ph.paymentDate >= ?2")
    Optional<List<PaymentHistory>>findByCardNumAndDateWithinDate(String num, Timestamp threeMonthsAgo);
}
