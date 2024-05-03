package com.eazy.pay.dao;

import com.eazy.pay.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository  extends JpaRepository<Notification, Long> {

    List<Notification> findByUserUidAndActiveRead(Long userUid, boolean activeRead);

    List<Notification> findByUserUid(Long userUid);

}
