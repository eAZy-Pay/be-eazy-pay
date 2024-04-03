package com.eazy.pay.service;

import com.eazy.pay.model.Faq;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface FaqRepository extends JpaRepository<Faq, Integer> {
    List<Faq> findAll();
}
