package com.eazy.pay.dto;

import lombok.*;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class PayAutoRequestDTO {
    Long userId;
    Long categoryId;
    Integer price;
    String storeCode;
    String storeName;
}