package com.eazy.pay.controller;

import com.eazy.pay.dto.SignInDTO;
import com.eazy.pay.model.User;
import com.eazy.pay.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserService userService;

    @GetMapping("/login")
    public SignInDTO getuser(@RequestParam("user_name") String userName, @RequestParam("user_password") String userPassword){
        User user = userService.getUserByUsername(userName);
        if (user != null && passwordEncoder.matches(userPassword, user.getPassword())) {
            Authentication authentication = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);
            System.out.println(user.getUsername());
            return new SignInDTO(HttpStatus.OK, user.getUid(), user.getUsername(), user.getIsAdmin());
        } else {
            return new SignInDTO(HttpStatus.UNAUTHORIZED);
        }
    }

}
