package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

//@WebMvcTest({RestExceptionHandler.class,UserController.class})
class RestExceptionHandlerTest {
    /*@Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService testUserService;

    @MockBean
    private FriendStorage testFriendStorage;*/

    @Test
    void handleNotFoundException() throws Exception {
        /*Mockito.when(testUserService.findUserById(100L))
                .thenThrow(new NotFoundException("Пользователь с id = " + 100 + " не найден"));

        mockMvc.perform(get("/users/100"))
                .andExpect(status().isNotFound());
        verify(testUserService, times(1)).findUserById(100);*/
    }

    @Test
    void handleUnknownException() throws Exception {
        /*User userPost = getTestUser();
        Mockito.when(testUserService.create(userPost))
                .thenThrow(new InternalServerException("Не удалось сохранить данные"));

        mockMvc.perform(post("/users"))
                .andExpect(status().isInternalServerError());*/
        //verify(testUserService, times(1)).create(userPost);
    }

    private User getTestUser() {
        return User.builder().email("test@mail.ru").login("login")
                .name("name").birthday(LocalDate.of(1970, 1, 1)).build();
    }

    /*
    @Test
    void handleValidationException(){
    }

    @Test
    void handleMethodArgumentNotValidException() {
    }
    */
}