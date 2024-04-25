package com.eazy.pay.dao;

import com.eazy.pay.model.EventCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventCategoryRepository extends JpaRepository<EventCategory, Long> {

    List<EventCategory> findAll();
}
