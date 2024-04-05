package com.eazy.pay.dao;
import com.eazy.pay.model.PayBenefit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PayBenefitRepository extends JpaRepository<PayBenefit, Integer> {
    List<PayBenefit> findAll();
}
