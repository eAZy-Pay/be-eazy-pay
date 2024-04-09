package com.eazy.pay.dto;

import lombok.*;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class CardDTO {
    private Long uid;
    private String image;
    private String name;
    private Integer annualFee;
    private Integer cardBenefitLimit;
    private String info;
    private String applicationUrl;
}
