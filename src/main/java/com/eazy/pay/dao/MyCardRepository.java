package com.eazy.pay.dao;
import com.eazy.pay.model.MyCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MyCardRepository extends JpaRepository<MyCard, Long> {
    List<MyCard> findAll();
}
