package com.eazy.pay.dao;

import com.eazy.pay.model.WooriCardUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WooriCardUserRepository extends JpaRepository<WooriCardUser, Long> {
    List<WooriCardUser> findAll();
}
