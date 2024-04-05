package com.eazy.pay.model;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class User extends BaseEntity {

    private String name;

    private String id;

    private String password;

    private String email;

    @Column(name = "phone_number")
    private String phoneNumber;

    private Date birthday;

    private String pin;

    @Column(name = "is_admin")
    private boolean isAdmin;

}
