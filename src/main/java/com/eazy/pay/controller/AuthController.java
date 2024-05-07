package com.eazy.pay.controller;

import com.eazy.pay.dao.UserRepository;
import com.eazy.pay.dto.SignInDTO;
import com.eazy.pay.dto.UserPinDTO;
import com.eazy.pay.model.User;
import com.eazy.pay.service.UserService;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@Slf4j
@RestController
public class AuthController {
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;

    @PostMapping("/login")
    public SignInDTO loginUser(@RequestBody Map<String, String> requestBody){
        String loginId = requestBody.get("user_name");
        String userPassword = requestBody.get("user_password");
        User user = userService.getUserByLoginId(loginId);
        if (user != null && passwordEncoder.matches(userPassword, user.getPassword())) {
            Authentication authentication = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);
            log.info("User logged in: " + user.getUid());
            return new SignInDTO(HttpStatus.OK, user.getUid(), user.getUsername(), user.getIsAdmin());
        } else {
            log.info("User login failed");
            return new SignInDTO(HttpStatus.UNAUTHORIZED);
        }
    }

    @PostMapping(value = "/api/checkpin")
    public ResponseEntity order(@RequestBody UserPinDTO userPinDTO) {
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

    @PatchMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody Map<String, String> requestBody) {
        Long userId;
        try {
            userId = Long.parseLong(requestBody.get("user_uid"));
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().body("Password change failed: Server error");
        }
        String newPassword = requestBody.get("new_password");
        boolean isChanged = userService.changeUserPassword(userId, newPassword);
        if (isChanged) {
            return ResponseEntity.ok("{\"message\": \"Password changed successfully\"}");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"error\": \"Password change failed: User not found\"}");
        }
    }

    @PatchMapping("/change-pin")
    public ResponseEntity<?> changePinPassword(@RequestBody Map<String, String> requestBody) {
        Long userId;
        try {
            userId = Long.parseLong(requestBody.get("uid"));
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().body("Pin change failed: Server error");
        }
        String newPin = requestBody.get("pin");
        boolean isChanged = userService.changePinPassword(userId, newPin);
        if (isChanged) {
            return ResponseEntity.ok("{\"message\": \"Pin changed successfully\"}");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"error\": \"Pin change failed: User not found\"}");
        }
    }
}
