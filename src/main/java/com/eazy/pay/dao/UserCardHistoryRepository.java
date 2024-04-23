package com.eazy.pay.dao;

import com.eazy.pay.model.UserCard;
import com.eazy.pay.model.UserCardHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.util.List;

@Repository
public interface UserCardHistoryRepository extends JpaRepository<UserCardHistory, Long> {

    @Query("SELECT uch.userCard FROM UserCardHistory uch WHERE uch.userCard.user.uid =?1 AND uch.yearAndMonth >=?2 AND uch.yearAndMonth <=?3 AND uch.is_fulfilled")
    List<UserCard> findFulfilledByUserIdAndDate(Long userId, Date start, Date end);
}
