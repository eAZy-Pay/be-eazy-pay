package com.eazy.pay.dao;

import com.eazy.pay.model.CardBenefit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CardBenefitRepository extends JpaRepository<CardBenefit, Long> {

    Page<CardBenefit> findByCategoryUid(Long categoryId, Pageable pageable);

    List<CardBenefit> findByCategoryUidIn(List<Long> categoryIds);

}
