package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.exception.InternalServerException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.friend.FriendStorage;

import java.time.LocalDate;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest({RestExceptionHandler.class,UserController.class})
class RestExceptionHandlerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService testUserService;

    @MockBean
    private FriendStorage testFriendStorage;

    @Test
    void handleNotFoundException() throws Exception {
        Mockito.when(testUserService.findUserById(100L))
                .thenThrow(new NotFoundException("Пользователь с id = " + 100 + " не найден"));

        mockMvc.perform(get("/users/100"))
                .andExpect(status().isNotFound());
        verify(testUserService, times(1)).findUserById(100);
    }

    @Test
    void handleUnknownException() throws Exception {
        User userPost = getTestUser();
        Mockito.when(testUserService.create(userPost))
                .thenThrow(new InternalServerException("Не удалось сохранить данные"));

        mockMvc.perform(post("/users"))
                .andExpect(status().isInternalServerError());
    }

    private User getTestUser() {
        return User.builder().email("test@mail.ru").login("login")
                .name("name").birthday(LocalDate.of(1970, 1, 1)).build();
    }

}