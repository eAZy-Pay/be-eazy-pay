package com.eazy.pay.model;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.Date;
import java.util.List;

@Entity
@Table(name = "my_cards")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
public class MyCard extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "uid")
    private User user;

    @ManyToOne
    @JoinColumn(name = "card_id", referencedColumnName = "uid")
    private Card card;

    private String num;

    private String cvc;

    @Column(name = "expiration_date")
    private Date expirationDate;

    private String password;

    @Column(name = "payment_limit")
    private int paymentLimit;

    @Column(name = "link_eazy")
    private boolean linkEazy;
}
