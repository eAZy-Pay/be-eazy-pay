package com.eazy.pay.controller;

import com.eazy.pay.model.Faq;
import com.eazy.pay.service.FaqService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/faqs")
public class FaqContoller {
    @Autowired
    private FaqService faqService;
    @GetMapping("/all")
    public List<Faq> getAllFaqs() {
        List<Faq> faqList = faqService.getAllFaqs();
        // 개발자가 설정한 로그
        log.info("Get all FAQs");
        log.error("faq error log");
        return faqList;
    }
}
