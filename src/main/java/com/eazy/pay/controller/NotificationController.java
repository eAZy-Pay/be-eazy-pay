package com.eazy.pay.controller;

import com.eazy.pay.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;


@RestController
@RequiredArgsConstructor
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    // 사용자가 SSE 연결을 구독할 때 사용되는 API
    @GetMapping(value = "/subscribe/{userId}"
            , produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public ResponseEntity<SseEmitter> subscribe(@PathVariable("userId") Long userId) {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        notificationService.addEmitter(userId, emitter);

        return ResponseEntity.ok()
                .header("Cache-Control", "no-cache")
                .header("Connection", "keep-alive")
                .body(emitter);
    }

    // 사용자 알림 다 읽음 처리 API
    @PutMapping("/api/notifications/readAll/{userId}")
    public ResponseEntity<Void> readAllNotifications(@PathVariable("userId") Long userId) {
        notificationService.readAllNotifications(userId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/api/notifications/read/{notificationId}")
    public ResponseEntity<Void> readNotification(@PathVariable("notificationId") Long notificationId) {
        notificationService.readNotification(notificationId);
        return ResponseEntity.ok().build();
    }

}
