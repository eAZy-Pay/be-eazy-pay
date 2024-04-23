package com.eazy.pay.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.Date;

@Entity
@Table(name = "user_cards")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
public class WooriCardUserCard extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "uid")
    private WooriCardUser wooriCardUser;

    @ManyToOne
    @JoinColumn(name = "card_id", referencedColumnName = "uid")
    private Card card;

    private String num;

    @Column(name = "expiration_date")
    private Date expirationDate;


    @Column(name = "payment_limit")
    private int paymentLimit;

    @Column(name = "card_valid")
    private boolean cardValid;

}
