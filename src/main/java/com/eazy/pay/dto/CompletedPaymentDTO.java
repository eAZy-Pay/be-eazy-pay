package com.eazy.pay.dto;

import lombok.*;
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class CompletedPaymentDTO {
    private int originalAmount;
    private int paidAmount;
    private int discount;
    private String cardName;
    private String cardImage;
}
