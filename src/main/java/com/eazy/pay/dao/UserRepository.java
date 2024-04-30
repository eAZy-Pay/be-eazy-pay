package com.eazy.pay.dao;

import com.eazy.pay.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    List<User> findAll();

    Optional<User> findById(Long userId);

    @Query("SELECT u FROM User u WHERE u.id = ?1")
    Optional<User> findByLoginId(String loginId);


    Optional<User> findByNameAndPhoneNumber(String name, String phoneNumber);
}
