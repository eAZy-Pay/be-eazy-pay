package com.eazy.pay.service;

import com.eazy.pay.dao.NotificationRepository;
import com.eazy.pay.dto.NotificationDTO;
import com.eazy.pay.mapper.NotificationMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    private final Map<Long, ConcurrentLinkedQueue<SseEmitter>> userEmitters = new ConcurrentHashMap<>();

    public void addEmitter(Long userId, SseEmitter emitter) {
        ConcurrentLinkedQueue<SseEmitter> emitters = userEmitters.computeIfAbsent(userId, k -> new ConcurrentLinkedQueue<>());
        if (!emitters.isEmpty()) {
            emitters.forEach(SseEmitter::complete);
            emitters.clear(); // Connection pool 차지하지 않도록 비워줌
        }
        emitters.add(emitter);
        emitter.onCompletion(() -> removeEmitter(userId, emitter));
        emitter.onTimeout(() -> removeEmitter(userId, emitter));
        emitter.onError(e -> handleEmitterError(userId, emitter, e));

        sendInitialNotification(userId, emitter);
    }

    private void handleEmitterError(Long userId, SseEmitter emitter, Throwable e) {
        removeEmitter(userId, emitter);
        emitter.completeWithError(e);
    }

    private void removeEmitter(Long userId, SseEmitter emitter) {
        ConcurrentLinkedQueue<SseEmitter> emitters = userEmitters.get(userId);
        if (emitters != null) {
            emitters.remove(emitter);
            if (emitters.isEmpty()) {
                userEmitters.remove(userId);
            }
        }
    }

    public void sendInitialNotification(Long userId, SseEmitter emitter) {
        List<NotificationDTO> notifications = notificationRepository.findByUserUidAndActiveRead(userId, false).stream()
                .map(NotificationMapper.INSTANCE::toDTO)
                .toList();
        if (!notifications.isEmpty()) {
            try {
                emitter.send(notifications);
            } catch (Exception e) {
                handleEmitterError(userId, emitter, e);
            }
        }
    }

    @Scheduled(fixedRateString = "${notification.rate:10000}")
    public void sendEvents() {
        userEmitters.forEach((userId, emitters) -> {
            System.out.println("Sending notifications to user " + userId);
            List<NotificationDTO> notifications = notificationRepository.findByUserUidAndActiveRead(userId, false).stream()
                    .map(NotificationMapper.INSTANCE::toDTO)
                    .toList();
            if (!notifications.isEmpty()) {
                emitters.forEach(emitter -> {
                    try {
                        emitter.send(notifications);
                    } catch (Exception e) {
                        handleEmitterError(userId, emitter, e);
                    }
                });
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
