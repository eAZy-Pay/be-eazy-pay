package com.eazy.pay.dao;
import com.eazy.pay.model.PaymentHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentHistoryRepository extends JpaRepository<PaymentHistory, Long> {
    List<PaymentHistory> findAll();

    // 특정 기간 내 결제 건수를 구하는 메서드
    @Query("SELECT COUNT(*) FROM PaymentHistory ph WHERE ph.cardNum IN :cardNumbers AND ph.paymentDate BETWEEN :start AND :end")
    int countByPaymentDateBetween(
            @Param("cardNumbers") List<String> cardNumbers,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );

    // 특정 기간 내 결제 금액 합계를 구하는 메서드
    @Query("SELECT SUM(ph.paymentAmount) FROM PaymentHistory ph WHERE ph.cardNum IN :cardNumbers AND ph.paymentDate BETWEEN :start AND :end")
    Integer sumPaymentAmountBetween(
            @Param("cardNumbers") List<String> cardNumbers,
            @Param("start") Timestamp start,
            @Param("end") Timestamp end
    );


    @Query("SELECT ph FROM PaymentHistory ph WHERE ph.cardNum = :cardNum AND ph.paymentDate BETWEEN :start AND :end")
    Optional<List<PaymentHistory>> findByCardNumAndDateWithinDate(
            @Param("cardNum") String cardNum,
            @Param("start") Timestamp start,
            @Param("end") Timestamp end
    );

}
