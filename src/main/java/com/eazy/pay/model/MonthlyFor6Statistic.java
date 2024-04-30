package com.eazy.pay.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.sql.Date;

@Entity
@Table(name = "monthly_for_6_statistics")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
public class MonthlyFor6Statistic extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "category_id", referencedColumnName = "uid")
    private Category category;

    @Column(name = "use_amount")
    private int useAmount;

    @Column(name = "year_and_month")
    private Date yearAndMonth;

    @Column(name = "age")
    private Integer age;
}
