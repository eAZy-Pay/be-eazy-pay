package com.eazy.pay.dao;
import com.eazy.pay.model.UserCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface UserCardRepository extends JpaRepository<UserCard, Long> {
    List<UserCard> findAll();
    @Query("SELECT uc FROM UserCard uc WHERE uc.user.uid =?1")
    List<UserCard> findByUserId(Long userId);
}
