package com.eazy.pay.dto;

import lombok.*;
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class CompletedPaymentDTO {
    private int price;
    private int payback;
    private String cardName;
    private String cardImage;
}
