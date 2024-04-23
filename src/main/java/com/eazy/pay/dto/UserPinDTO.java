package com.eazy.pay.dto;

import lombok.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class UserPinDTO {
    private Long uid;
    private String pin;
}
