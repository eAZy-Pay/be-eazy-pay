package com.eazy.pay.controller;

import com.eazy.pay.dto.CardDTO;
import com.eazy.pay.model.Card;
import com.eazy.pay.model.CardBenefit;
import com.eazy.pay.service.CardBenefitService;
import com.eazy.pay.service.CardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/cards")
public class CardController {
    @Autowired
    private CardBenefitService cardBenefitService;


    @GetMapping
    public List<CardDTO> getCategoryCards(@RequestParam("category_id") Long categoryId) {
        return cardBenefitService.getCategoryCards(categoryId);
    }
}
