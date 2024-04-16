package com.eazy.pay.model;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import lombok.experimental.SuperBuilder;
import java.util.Collection;

import java.util.Date;
import java.util.List;

@Entity
@Table(name = "users")
@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
public class User extends BaseEntity implements UserDetails {

    private String name;

    private String id;

    private String password;

    private String email;

    @Column(name = "phone_number")
    private String phoneNumber;
    @Column(name = "birthday")
    @JsonFormat(pattern = "yyyyMMdd")
    private Date birthday;

    private String pin;

    @Column(name = "is_admin")
    private boolean isAdmin;

    public boolean getIsAdmin() {
        return isAdmin;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getUsername() {
        return name;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
