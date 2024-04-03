package com.eazy.pay.model;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int uid;

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
