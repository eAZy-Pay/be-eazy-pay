package com.eazy.pay.controller;

import com.eazy.pay.dao.UserRepository;
import com.eazy.pay.model.User;
import com.eazy.pay.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/register")
public class RegisterController {

    @Autowired
    private UserService userService;


    @PostMapping
    public ResponseEntity registerUser(@RequestBody User user) {
        if (userService.registerUser(user)){
            return ResponseEntity.ok("registration success"); // 보유 카드 정보를 반환. 이름, 이미지만 응답
        } else {
            return ResponseEntity.status(500).body("registration fail");
        }
    }

    @GetMapping(value = "/checkid")
    public ResponseEntity checkId(@RequestParam("id") String strId) {
        if (userService.getUserByStrId(strId) == null) {
            return ResponseEntity.ok("id available");
        } else {
            return ResponseEntity.status(409).body("id already exists");
        }
    }

    @GetMapping(value = "/checkmember")
    public ResponseEntity checkMember(@RequestParam("name") String name,
                                      @RequestParam("phoneNumber") String phoneNumber) {
        if (userService.findByNameAndPhoneNumber(name, phoneNumber) != null) {
            return ResponseEntity.ok("member not exists");
        } else {
            return ResponseEntity.status(409).body("member exists");
        }
    }
}





