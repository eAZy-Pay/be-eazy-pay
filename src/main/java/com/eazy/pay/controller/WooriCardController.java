package com.eazy.pay.controller;

import com.eazy.pay.service.WooriCardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WooriCardController {
    @Autowired
    private WooriCardService wooriCaradService;
}
