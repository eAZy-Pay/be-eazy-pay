package com.eazy.pay.dto;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class CardUsageSummaryDTO {
    private List<CategoryBenefitAmountDTO> categoryBenefitAmount;
    private int totalAnnualFee;
    private int benefitOfMonth;
    private int benefitOfYear;
}
