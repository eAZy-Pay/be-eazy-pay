package com.eazy.pay.dao;
import com.eazy.pay.model.PayBenefit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface PayBenefitRepository extends JpaRepository<PayBenefit, Long> {
    List<PayBenefit> findAll();
    @Query("SELECT pb FROM PayBenefit pb WHERE pb.userCard.num = ?1 AND pb.paymentDate >= ?2")
    Optional<List<PayBenefit>>findByCardNumAndDateWithinDate(String num, Timestamp threeMonthsAgo);
}
