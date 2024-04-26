package com.eazy.pay.dao;

import com.eazy.pay.model.HighlightedCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HighlightedCardRepository extends JpaRepository<HighlightedCard, Long> {

    List<HighlightedCard> findByEventCategoryUid(Long eventCategoryUid);
}
