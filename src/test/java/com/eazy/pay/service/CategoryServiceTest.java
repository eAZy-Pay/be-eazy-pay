package com.eazy.pay.service;

import com.eazy.pay.dao.CategoryRepository;
import com.eazy.pay.dao.EventCategoryRepository;
import com.eazy.pay.dto.CategoryDTO;
import com.eazy.pay.dto.EventCategoryDTO;
import com.eazy.pay.model.Category;
import com.eazy.pay.model.EventCategory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    EventCategoryRepository eventCategoryRepository;

    @InjectMocks
    private CategoryService categoryService;

    @BeforeEach
    void setUp() {
        Category category1 = Category.builder()
                .uid(1L)
                .name("외식")
                .build();

        Category category2 = Category.builder()
                .uid(2L)
                .name("문화")
                .build();

        EventCategory eventCategory1 = EventCategory.builder()
                .uid(1L)
                .name("나야나")
                .build();

        EventCategory eventCategory2 = EventCategory.builder()
                .uid(2L)
                .name("정석")
                .build();

        List<Category> categories = Arrays.asList(category1, category2);

        List<EventCategory> eventCategories = Arrays.asList(eventCategory1, eventCategory2);

        lenient().when(categoryRepository.findAll()).thenReturn(categories);
        lenient().when(eventCategoryRepository.findAll()).thenReturn(eventCategories);

    }

    @Test
    void getAllCategories() {
        List<CategoryDTO> categories = categoryService.getAllCategories();

        assertEquals(2, categories.size());
        assertEquals("외식", categories.get(0).getName());
        assertEquals("문화", categories.get(1).getName());

        verify(categoryRepository, times(1)).findAll();
    }

    @Test
    void getEventCategories() {
        List<EventCategoryDTO> eventCategories = categoryService.getEventCategories();

        assertEquals(2, eventCategories.size());
        assertEquals("나야나", eventCategories.get(0).getName());
        assertEquals("정석", eventCategories.get(1).getName());

        verify(eventCategoryRepository, times(1)).findAll();
    }
}