package com.eazy.pay.model;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "monthly_for_6")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class MonthlyFor6 extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "category_id", referencedColumnName = "uid")
    private Category category;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "uid")
    private User user;

    @Column(name = "base_quater")
    private String baseQuarter;

    @Column(name = "use_amount")
    private int useAmount;
}
