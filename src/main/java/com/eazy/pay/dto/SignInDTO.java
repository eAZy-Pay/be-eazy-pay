package com.eazy.pay.dto;

import lombok.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class SignInDTO {
    private HttpStatus status;
    private Long uid;
    private String name;
    private Boolean isAdmin;

    public SignInDTO(HttpStatus httpStatus) {
        status = httpStatus;
    }
}
