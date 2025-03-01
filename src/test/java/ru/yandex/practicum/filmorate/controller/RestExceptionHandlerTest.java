package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.yandex.practicum.filmorate.service.UserService;

@SpringBootTest
class RestExceptionHandlerTest {
    @InjectMocks
    private RestExceptionHandler restExceptionHandler;
    @InjectMocks
    private UserController userController;
    @Mock
    private UserService mocUserService;
    private MockMvc mvc;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.standaloneSetup(userController).build();
    }

    @Test
    void handleValidationException() {
    }

    @Test
    void handleNotFoundException() {
    }

    @Test
    void handleMethodArgumentNotValidException() {
    }

    @Test
    void handleUnknownException() {
    }
}