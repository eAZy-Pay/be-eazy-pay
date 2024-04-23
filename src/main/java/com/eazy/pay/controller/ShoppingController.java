package com.eazy.pay.controller;

import com.eazy.pay.dao.UserRepository;
import com.eazy.pay.dto.UserPinDTO;
import com.eazy.pay.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/api/shopping")
public class ShoppingController {

    @Autowired
    private UserRepository userRepository;

    @PostMapping(value = "/pin")
    public ResponseEntity order(@RequestBody UserPinDTO userPinDTO) {
        // findById로 User객체를 가져오기
        Optional<User> optionalUser = userRepository.findById(userPinDTO.getUid());
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            // 가져온 User 객체의 pin과 userPinDTO의 pin이 일치하는지 확인
            if (user.getPin().equals(userPinDTO.getPin())) {
                return ResponseEntity.ok("Pin matches");  // HTTP 200 OK
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Pin does not match");  // HTTP 400 Bad Request
            }
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");  // HTTP 404 Not Found
        }
    }
}
