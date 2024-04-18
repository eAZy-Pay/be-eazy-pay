package com.eazy.pay.controller;

import com.eazy.pay.dao.UserRepository;
import com.eazy.pay.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/register")
public class RegisterController {

    @Autowired
    private UserRepository userRepository;

    @PostMapping
    public ResponseEntity registerUser(@RequestBody User user) {
        try {
            user.setPassword(new BCryptPasswordEncoder().encode(user.getPassword()));
            User registerUser = userRepository.save(user);
            return ResponseEntity.ok(registerUser);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("registration fail");
        }
    }

    @GetMapping(value = "/checkid")
    public ResponseEntity checkId(@RequestParam("id") String strId) {
        Optional<User> ou = this.userRepository.findByStrId(strId);
        if (ou.isPresent()) {
            return ResponseEntity.status(409).body("id already exists");
        } else {
            return ResponseEntity.ok("id available");
        }
    }

    @GetMapping(value = "/checkmember")
    public ResponseEntity checkMember(@RequestParam("name") String name, @RequestParam("phoneNumber") String phoneNumber) {
        Optional<User> ou = this.userRepository.findByNameAndPhoneNumber(name, phoneNumber);
        if (ou.isPresent()) {
            return ResponseEntity.ok("member exists");
        } else {
            return ResponseEntity.status(404).body("member not exists");
        }
    }
}





