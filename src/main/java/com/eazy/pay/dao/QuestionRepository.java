package com.eazy.pay.dao;

import com.eazy.pay.model.Question;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {
    Page<Question> findAll(Pageable pageable);

    Page<Question> findByAnswered(boolean answered, Pageable pageable);

    Optional<Question> findByUid(Long uid);
}
