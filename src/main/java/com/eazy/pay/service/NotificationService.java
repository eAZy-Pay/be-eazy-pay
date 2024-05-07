package com.eazy.pay.service;

import com.eazy.pay.dao.NotificationRepository;
import com.eazy.pay.dto.NotificationDTO;
import com.eazy.pay.mapper.NotificationMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    private final ConcurrentHashMap<Long, SseEmitter> userEmitters = new ConcurrentHashMap<>();


    public void addEmitter(Long userId, SseEmitter emitter) {
        SseEmitter previousEmitter = userEmitters.put(userId, emitter);
        if (previousEmitter != null) {
            previousEmitter.complete();
        }
        emitter.onCompletion(() -> removeEmitter(userId));
        emitter.onTimeout(() -> removeEmitter(userId));
        emitter.onError(e -> handleEmitterError(userId, e));

        sendInitialNotification(userId, emitter);
    }

    private void handleEmitterError(Long userId, Throwable e) {
        removeEmitter(userId);
        SseEmitter emitter = userEmitters.get(userId);
        if (emitter != null) {
            emitter.completeWithError(e);
        }
    }

    private void removeEmitter(Long userId) {
        SseEmitter emitter = userEmitters.remove(userId);
        if (emitter != null) {
            emitter.complete();
        }
    }

    public void sendInitialNotification(Long userId, SseEmitter emitter) {
        List<NotificationDTO> notifications = fetchNotifications(userId);
        if (!notifications.isEmpty()) {
            try {
                emitter.send(SseEmitter.event().name("initial").data(notifications));
            } catch (Exception e) {
                handleEmitterError(userId, e);
            }
        }
    }

    private List<NotificationDTO> fetchNotifications(Long userId) {
        return notificationRepository.findByUserUidAndActiveRead(userId, false).stream()
                .map(NotificationMapper.INSTANCE::toDTO)
                .toList();
    }

    @Scheduled(fixedRateString = "${notification.rate:10000}")
    public void sendEvents() {
        userEmitters.forEach((userId, emitter) -> {
            if (emitter != null) {
                List<NotificationDTO> notifications = fetchNotifications(userId);
                if (!notifications.isEmpty()) {
                    try {
                        emitter.send(SseEmitter.event().name("update").data(notifications));
                    } catch (Exception e) {
                        handleEmitterError(userId, e);
                    }
                }
            }
        });
    }

    public void readAllNotifications(Long userId) {
        notificationRepository.findByUserUidAndActiveRead(userId, false).forEach(notification -> {
            notification.setActiveRead(true);
            notificationRepository.save(notification);
        });
    }

    public void readNotification(Long notificationId) {
        notificationRepository.findByUid(notificationId).ifPresent(notification -> {
            notification.setActiveRead(true);
            notificationRepository.save(notification);
        });
    }
}