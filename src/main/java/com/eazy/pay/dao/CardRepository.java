package com.eazy.pay.dao;

import com.eazy.pay.model.Card;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CardRepository extends JpaRepository<Card, Long> {

    List<Card> findAll();

    List<Card> findByNameContaining(String name);

    Optional<Card> findByUid(Long cardId);
}
