package com.eazy.pay.dto;

import lombok.*;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class PaymentStatsDTO{
    private int totalPaymentCount;
    private Integer totalPaymentAmount;
}
