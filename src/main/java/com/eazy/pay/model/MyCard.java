package com.eazy.pay.model;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "my_cards")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MyCard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int uid;

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

    private int performance;

    @Column(name = "payment_limit")
    private int paymentLimit;

    @Column(name = "link_eazy")
    private boolean linkEazy;
}
