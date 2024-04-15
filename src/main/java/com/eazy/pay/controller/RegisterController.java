package com.eazy.pay.controller;

import com.eazy.pay.dao.UserRepository;
import com.eazy.pay.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/register")
public class RegisterController {

    @Autowired
    private UserRepository userRepository;

    @PostMapping
    public ResponseEntity<User> registerUser(@RequestBody User user) {

        System.out.println("Received user data: " + user);

        User registerUser = userRepository.save(user);
        return ResponseEntity.ok(user);
    }
}
