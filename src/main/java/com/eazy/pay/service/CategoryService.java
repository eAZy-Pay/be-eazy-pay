package com.eazy.pay.service;

import com.eazy.pay.dao.EventCategoryRepository;
import com.eazy.pay.dto.CategoryDTO;
import com.eazy.pay.dto.EventCategoryDTO;
import com.eazy.pay.mapper.CategoryMapper;
import com.eazy.pay.mapper.EventCategoryMapper;
import com.eazy.pay.model.Category;
import com.eazy.pay.dao.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private EventCategoryRepository eventCategoryRepository;


    public List<CategoryDTO> getAllCategories() {

        return categoryRepository.findAll().stream()
                .map(CategoryMapper.INSTANCE::toDTO)
                .collect(Collectors.toList());
    }

    public List<EventCategoryDTO> getEventCategories() {
        return eventCategoryRepository.findAll().stream()
                .map(EventCategoryMapper.INSTANCE::toDTO)
                .collect(Collectors.toList());
    }
}
