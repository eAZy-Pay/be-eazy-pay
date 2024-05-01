package com.eazy.pay.dto;

import lombok.*;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class CardToPayListRequestDTO {
    private Long userId;
    private Long categoryId;
    private Integer price;
}
