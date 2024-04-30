package com.eazy.pay.dto;

import lombok.*;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class UserCategoryHistoryDTO{
    private Long uid;
    private Long categoryId;
    private String categoryName;
    private Integer benefitAmount;
    private Integer useAmount;
}
