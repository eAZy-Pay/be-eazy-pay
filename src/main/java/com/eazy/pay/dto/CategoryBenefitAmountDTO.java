package com.eazy.pay.dto;

import lombok.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class CategoryBenefitAmountDTO {
    private String categoryName;
    private int benefitAmount;
}
