package com.eazy.pay.service;

import com.eazy.pay.model.MyCard;
import com.eazy.pay.dao.MyCardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MyCardService {

    @Autowired
    private MyCardRepository myCardRepository;

    public List<MyCard> getAllMyCards() {
        return myCardRepository.findAll();
    }
}
