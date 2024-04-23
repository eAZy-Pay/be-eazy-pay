package com.eazy.pay.controller;

import com.eazy.pay.service.WooriCaradService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WooriCardController {
    @Autowired
    private WooriCaradService wooriCaradService;
}
