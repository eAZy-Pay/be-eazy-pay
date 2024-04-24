package com.eazy.pay.dto;

import lombok.*;

import java.util.Date;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class UserCardDTO {
    private Long uid;
    private Long userId;
    private Long cardId;
    private String num;
    private Date expirationDate;
    private int paymentLimit;
    private boolean linkEazy;
    private boolean cardValid;
}
