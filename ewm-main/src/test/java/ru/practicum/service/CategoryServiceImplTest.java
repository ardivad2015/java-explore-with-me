package ru.practicum.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.dto.category.CategoryDto;
import ru.practicum.dto.category.CategoryMapper;
import ru.practicum.exception.NotFoundException;
import ru.practicum.model.Category;
import ru.practicum.repository.CategoryRepository;
import ru.practicum.service.category.CategoryServiceImpl;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceImplTest {

    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private CategoryMapper categoryMapper;
    @Captor
    ArgumentCaptor<Category> categoryArgumentCaptor;
    @InjectMocks
    private CategoryServiceImpl categoryService;

    @Test
    void update_whenCategoryExistsAndNameUnique_thenUpdatedFields() {
        final Integer id = 1;
        final String name = "Cat. 1";
        final Category category = new Category();
        final CategoryDto categoryDto = new CategoryDto();

        category.setId(id);
        category.setName(name);

        categoryDto.setId(id);
        categoryDto.setName(name);

        when(categoryRepository.findById(id))
                .thenReturn(Optional.of(category));
        when(categoryRepository.findByName(name))
                .thenReturn(Optional.of(category));

        final CategoryDto actualCategory = categoryService.update(categoryDto);

        verify(categoryRepository).save(categoryArgumentCaptor.capture());

        final Category savedCategory = categoryArgumentCaptor.getValue();

        assertEquals(savedCategory.getName(), categoryDto.getName());
        assertEquals(savedCategory.getId(), categoryDto.getId());
    }

    @Test
    void update_whenCategoryNotFound_thenNotFoundExceptionThrown() {
        final Integer id = 1;
        final String name = "Cat. 1";
        final CategoryDto categoryDto = new CategoryDto();

        categoryDto.setId(id);
        categoryDto.setName(name);

        when(categoryRepository.findById(id))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> categoryService.update(categoryDto));
        verify(categoryRepository, never())
                .save(any(Category.class));
    }
}