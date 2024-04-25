package com.eazy.pay.controller;

import com.eazy.pay.dto.CategoryDTO;
import com.eazy.pay.dto.EventCategoryDTO;
import com.eazy.pay.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;


    @GetMapping
    public List<CategoryDTO> getCategories() {
        return categoryService.getAllCategories();
    }

    @GetMapping("/event")
    public List<EventCategoryDTO> getEventCategories() {
        return categoryService.getEventCategories();
    }
}
