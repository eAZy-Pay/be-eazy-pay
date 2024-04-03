package com.eazy.pay.controller;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController

public class serverStatusController {
    @GetMapping("/server")
    public String server_alive(){
        return "서버 잘~ 돌아간다~!";
    }
}
