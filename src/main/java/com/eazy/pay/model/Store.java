package com.eazy.pay.model;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
@Entity
@Table(name = "stores")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Store extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "category_id", referencedColumnName = "uid")
    private Category category;

    @Column(name = "store_name")
    private String storeName;
}
