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
import ru.practicum.service.category.CategoryService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PublicCategoryController.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class PublicCategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private CategoryService categoryService;
    @Captor
    ArgumentCaptor<Integer> intArgumentCaptor;

    @SneakyThrows
    @Test
    void getAllCategories_whenAllParams_thenCallServiceWithItsValues() {
        mockMvc.perform(get("/categories?from={from}&size={size}", 1, 2))
                .andExpect(status().isOk());
        verify(categoryService).getAll(intArgumentCaptor.capture(),intArgumentCaptor.capture());

        final Integer actualFrom = intArgumentCaptor.getAllValues().get(0);
        final Integer actualSize = intArgumentCaptor.getAllValues().get(1);

        assertEquals(actualFrom, 1);
        assertEquals(actualSize, 2);
    }

    @SneakyThrows
    @Test
    void getAllCategories_whenParamsNotDefined_thenCallServiceWithDefaults() {
        mockMvc.perform(get("/categories"))
                .andExpect(status().isOk());
        verify(categoryService).getAll(intArgumentCaptor.capture(),intArgumentCaptor.capture());

        final Integer actualFrom = intArgumentCaptor.getAllValues().get(0);
        final Integer actualSize = intArgumentCaptor.getAllValues().get(1);

        assertEquals(actualFrom, 0);
        assertEquals(actualSize, 10);
    }

}