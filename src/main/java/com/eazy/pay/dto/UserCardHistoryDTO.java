package com.eazy.pay.dto;

import lombok.*;

import java.sql.Date;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class UserCardHistoryDTO {
    private Long uid;
    private Long userCardId;
    private Date yearAndMonth;
    private Integer benefitAmount;
    private Integer useAmount;
    private Boolean isFulfilled;
}
