package ru.practicum.controller.user;

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
import ru.practicum.service.user.UserService;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminUserController.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class AdminUserControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private UserService userService;
    @Captor
    ArgumentCaptor<Integer> intArgumentCaptor;
    @Captor
    ArgumentCaptor<List<Long>> longLiatArgumentCaptor;

    @SneakyThrows
    @Test
    void findAll_whenAllParams_thenCallServiceWithItsValues() {
        final List<Long> ids = new ArrayList<>();
        final StringBuilder idsString = new StringBuilder();
        ids.add(1L);
        ids.add(2L);

        boolean firstUri = true;
        for (Long id : ids) {
            if (!firstUri) {
                idsString.append(",");
            }
            idsString.append(id);
            if (firstUri) {
                firstUri = false;
            }
        }

        mockMvc.perform(get("/admin/users")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .param("ids", idsString.toString())
                        .param("from", "1")
                        .param("size", "2"))
                .andExpect(status().isOk());
        verify(userService).getAllByIds(longLiatArgumentCaptor.capture(), intArgumentCaptor.capture(),
                intArgumentCaptor.capture());

        final List<Long> actualIds = longLiatArgumentCaptor.getValue();
        final Integer actualFrom = intArgumentCaptor.getAllValues().get(0);
        final Integer actualSize = intArgumentCaptor.getAllValues().get(1);

        assertEquals(actualIds, ids);
        assertEquals(actualFrom, 1);
        assertEquals(actualSize, 2);
    }
}