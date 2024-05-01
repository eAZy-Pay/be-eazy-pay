package com.eazy.pay.model;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.sql.Date;

@Entity
@Table(name = "user_cards")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
public class UserCard extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "uid")
    private User user;

    @ManyToOne
    @JoinColumn(name = "card_id", referencedColumnName = "uid")
    private Card card;

    private String num;

    @Column(name = "expiration_date")
    private Date expirationDate;


    @Column(name = "payment_limit")
    private int paymentLimit;

    @Column(name = "link_eazy")
    private boolean linkEazy;

    @Column(name = "card_valid")
    private boolean cardValid;

}
