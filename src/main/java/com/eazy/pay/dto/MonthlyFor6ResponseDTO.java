package com.eazy.pay.dto;

import lombok.*;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class MonthlyFor6ResponseDTO {
    private Long categoryId;
    private String categoryName;
    private int useAmount;
}
