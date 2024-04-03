package com.eazy.pay.controller;
import com.eazy.pay.model.*;
import com.eazy.pay.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;


@RestController
@RequestMapping("/api")
public class ApiController {

    @Autowired
    private MyCardService myCardService;

    @Autowired
    private UserService userService;

    @Autowired
    private CardBenefitService cardBenefitService;

    @Autowired
    private CardService cardService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private PayBenefitService payBenefitService;

    @Autowired
    private StoreService storeService;

    @Autowired
    private MonthlyFor6Service monthlyFor6Service;

    @Autowired
    private QuestionService questionService;

    @Autowired
    private FaqService faqService;

    @GetMapping("/mycards")
    public List<MyCard> getAllMyCards() {
        return myCardService.getAllMyCards();
    }

    @GetMapping("/users")
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/cardbenefits")
    public List<CardBenefit> getAllCardBenefits() {
        return cardBenefitService.getAllCardBenefits();
    }

    @GetMapping("/cards")
    public List<Card> getAllCards() {
        return cardService.getAllCards();
    }

    @GetMapping("/categories")
    public List<Category> getAllCategories() {
        return categoryService.getAllCategories();
    }

    @GetMapping("/paybenefits")
    public List<PayBenefit> getAllPayBenefits() {
        return payBenefitService.getAllPayBenefits();
    }

    @GetMapping("/stores")
    public List<Store> getAllStores() {
        return storeService.getAllStores();
    }

    @GetMapping("/monthlyfor6")
    public List<MonthlyFor6> getAllMonthlyFor6() {
        return monthlyFor6Service.getAllMonthlyFor6();
    }

    @GetMapping("/questions")
    public List<Question> getAllQuestions() {
        return questionService.getAllQuestions();
    }

    @GetMapping("/faqs")
    public List<Faq> getAllFaq() {
        return faqService.getAllFaqs();
    }
}
