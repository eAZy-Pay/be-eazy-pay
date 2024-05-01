package com.eazy.pay.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.sql.Date;

@Entity
@Table(name = "usage_statistics")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
public class UsageStatistic extends BaseEntity {

    @Column(name = "year_and_month")
    private Date yearAndMonth;

    private Integer age;

    private Integer percentile;

    @Column(name = "use_amount")
    private Integer useAmount;
}
