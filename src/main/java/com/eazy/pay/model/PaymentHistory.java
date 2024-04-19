package com.eazy.pay.model;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.Date;

@Entity
@Table(name = "payment_history")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
public class PaymentHistory extends BaseEntity {

    @Column(name = "card_num")
    private String cardNum;

    @Column(name = "payment_date")
    private Date paymentDate;

    @Column(name = "payment_amount")
    private int paymentAmount;

    @Column(name = "store_code")
    private String storeCode;

    @Column(name = "store_name")
    private String storeName;


    @Column(name = "category_name")
    private String categoryName;

    @Column(name = "benefit_amount")
    private int benefitAmount;


}
