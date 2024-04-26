package com.eazy.pay.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.sql.Date;

@Entity
@Table(name = "woori_card_user_monthly_for_6")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
public class WooriCardUserMonthlyFor6 extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "category_id", referencedColumnName = "uid")
    private Category category;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "uid")
    private WooriCardUser user;

    @Column(name = "use_amount")
    private int useAmount;

    @Column(name = "year_and_month")
    private Date yearAndMonth;
}
