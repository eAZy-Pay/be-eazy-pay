package com.eazy.pay.dto;

import lombok.*;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class NotificationDTO {
    private Long uid;
    private Long userId;
    private String userName;
    private String message;
    private String createdAt;
    private boolean activeRead;
}
