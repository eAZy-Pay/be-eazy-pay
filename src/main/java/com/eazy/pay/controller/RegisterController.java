package com.eazy.pay.controller;

import com.eazy.pay.dao.UserRepository;
import com.eazy.pay.model.User;
import com.eazy.pay.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/register")
public class RegisterController {

    @Autowired
    private UserService userService;


    @PostMapping
    public ResponseEntity registerUser(@RequestBody User user) {
        try {
            User registeredUser = userService.registerUser(user);
            // 등록에 성공하면 응답을 UserCard의 이름, 이미지로 변경할 예정
            Map<String, Object> response = new HashMap<>();
            response.put("name", registeredUser.getName());
            response.put("email", registeredUser.getEmail());
//            System.out.println(ResponseEntity.ok(response));
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            // 실패 시 적절한 HTTP 상태 코드와 메시지 반환
            return ResponseEntity.status(500).body("Registration failed: " + e.getMessage());
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





