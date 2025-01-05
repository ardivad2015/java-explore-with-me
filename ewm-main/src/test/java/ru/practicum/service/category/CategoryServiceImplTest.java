package ru.practicum.service.category;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import ru.practicum.dto.category.CategoryDto;
import ru.practicum.dto.category.CategoryMapper;
import ru.practicum.dto.category.CategoryMapperImpl;
import ru.practicum.exception.NotFoundException;
import ru.practicum.model.Category;
import ru.practicum.repository.category.CategoryRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceImplTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Captor
    ArgumentCaptor<Category> categoryArgumentCaptor;

    @Test
    void update_whenCategoryExistsAndNameUnique_thenUpdatedFields() {
        final CategoryService categoryService = new CategoryServiceImpl(categoryRepository, new CategoryMapperImpl());
        final Long id = 1L;
        final String name = "Cat. 1";
        final String updatedName = "Cat. 1 upd";
        final Category category = new Category();
        final CategoryDto categoryDto = new CategoryDto();

        category.setId(id);
        category.setName(name);

        categoryDto.setId(id);
        categoryDto.setName(updatedName);

        when(categoryRepository.findById(id))
                .thenReturn(Optional.of(category));
        when(categoryRepository.findByName(updatedName))
                .thenReturn(Optional.empty());

        final CategoryDto actualCategory = categoryService.update(categoryDto);

        assertEquals(actualCategory.getName(), categoryDto.getName());
        assertEquals(actualCategory.getId(), categoryDto.getId());
    }

    @Test
    void update_whenCategoryNotFound_thenNotFoundExceptionThrown() {
        final CategoryService categoryService = new CategoryServiceImpl(categoryRepository, new CategoryMapperImpl());
        final Long id = 1L;
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