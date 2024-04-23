package com.eazy.pay.dao;

import com.eazy.pay.model.Card;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CardRepository extends JpaRepository<Card, Long> {

    Page<Card> findAll(Pageable pageable);

    Page<Card> findByNameContaining(String name, Pageable pageable);

    Optional<Card> findByUid(Long cardId);

}
