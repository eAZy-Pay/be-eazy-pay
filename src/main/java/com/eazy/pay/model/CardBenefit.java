package com.eazy.pay.model;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "card_benefits")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CardBenefit {

    @Id
    private int uid;

    @ManyToOne
    @JoinColumn(name = "card_id", referencedColumnName = "uid")
    private Card cardId;

    @ManyToOne
    @JoinColumn(name = "category_id", referencedColumnName = "uid")
    private Category categoryId;

    @Column(name = "benefit_rate")
    private Integer benefitRate;
}
