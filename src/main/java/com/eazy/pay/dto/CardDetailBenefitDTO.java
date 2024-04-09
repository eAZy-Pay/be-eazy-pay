package com.eazy.pay.dto;

import lombok.*;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class CardDetailBenefitDTO {
    private Long uid;
    private Long categoryId;
    private String categoryName;
    private int benefitRate;
}
