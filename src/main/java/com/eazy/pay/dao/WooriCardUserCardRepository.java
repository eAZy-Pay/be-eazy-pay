package com.eazy.pay.dao;

import com.eazy.pay.model.WooriCardUserCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WooriCardUserCardRepository extends JpaRepository<WooriCardUserCard, Long> {
    List<WooriCardUserCard> findAll();
}
