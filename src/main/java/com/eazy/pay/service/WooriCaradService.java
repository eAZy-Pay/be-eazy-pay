package com.eazy.pay.service;

import com.eazy.pay.dao.WooriCardUserCardRepository;
import com.eazy.pay.dao.WooriCardUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WooriCaradService {
    @Autowired
    private WooriCardUserRepository wooriCardUserRepository;

    @Autowired
    private WooriCardUserCardRepository wooriCardUserCardRepository;

}
