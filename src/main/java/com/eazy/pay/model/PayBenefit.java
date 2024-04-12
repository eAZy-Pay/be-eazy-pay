package com.eazy.pay.model;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.Date;

@Entity
@Table(name = "pay_benefit")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
public class PayBenefit extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "card_num", referencedColumnName = "num")
    private UserCard userCard;

    @Column(name = "payment_date")
    private Date paymentDate;

    @Column(name = "payment_amount")
    private int paymentAmount;


    @Column(name = "category_name")
    private String categoryName;

    @Column(name = "benefit_amount")
    private int benefitAmount;
}
