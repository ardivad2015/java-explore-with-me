package ru.practicum.controller.category;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.dto.category.CategoryDto;
import ru.practicum.service.category.CategoryService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminCategoryController.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class AdminCategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private CategoryService categoryService;
    @Captor
    ArgumentCaptor<CategoryDto> categoryDtoArgumentCaptor;

    @SneakyThrows
    @Test
    void updateCategory() {
        final CategoryDto categoryDto = new CategoryDto();
        final Long categoryId = 1L;
        categoryDto.setName("cat. 1");
        categoryDto.setId(categoryId);

        when(categoryService.update(any(CategoryDto.class))).thenReturn(categoryDto);

        String response = mockMvc.perform(patch("/admin/categories/{id}", categoryId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(categoryDto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(categoryService).update(categoryDtoArgumentCaptor.capture());
        final CategoryDto updatedCategory = categoryDtoArgumentCaptor.getValue();

        assertEquals(objectMapper.writeValueAsString(categoryDto), response);
        assertEquals(updatedCategory.getId(), categoryId);
        assertEquals(updatedCategory.getName(), categoryDto.getName());
    }
}