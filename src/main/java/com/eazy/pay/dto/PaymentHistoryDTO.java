package com.eazy.pay.dto;
import lombok.*;
import java.util.Date;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class PaymentHistoryDTO {
    private Date paymentDate;
    private int paymentAmount;
    private String storeName;
}
