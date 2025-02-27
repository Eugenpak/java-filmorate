package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import java.sql.Date;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserController2MockitoTest {
    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private User getTestUser() {
        return User.builder().id(1L).email("test@mail.ru").login("login")
                .name("name").birthday(new Date(0)).build();
    }
    private User getTestNotValidUser() {
        return User.builder().id(1L).email("test@mail.ru").login("login")
                .name("name").birthday(new Date(10_000_000_000_000L)).build();
    }

    @Test
    void findAll() {
        List<User> expectedUsers = List.of(getTestUser());
        when(userService.findAll()).thenReturn(expectedUsers);

        List<User> actualUser = userController.findAll().stream().toList();
        verify(userService, times(1)).findAll();
        assertEquals(expectedUsers.size(), actualUser.size());
        assertSame(expectedUsers.get(0), actualUser.get(0));
    }

    @Test
    void create() {
        User expectedUsers = getTestUser();
        when(userService.create(expectedUsers)).thenReturn(expectedUsers);

        User actualUser = userController.create(expectedUsers);

        verify(userService, times(1)).create(expectedUsers);
        assertEquals(expectedUsers.getName(), actualUser.getName());
        assertSame(expectedUsers, actualUser);
    }

    @Test
    void createNotValidUser() {
        doThrow(new ValidationException("not valid User")).when(userService).create(getTestNotValidUser());

        assertThrows(ValidationException.class, () -> userController.create(getTestNotValidUser()));
        verify(userService, times(1)).create(getTestNotValidUser());
    }

    @Test
    void update() {
        User expectedUser = getTestUser();
        when(userService.update(expectedUser)).thenReturn(expectedUser);

        User actualUser = userController.update(expectedUser);

        verify(userService, times(1)).update(expectedUser);
        assertEquals(expectedUser.getName(), actualUser.getName());
        assertEquals(expectedUser.getId(), actualUser.getId());
        assertSame(expectedUser, actualUser);
    }

    @Test
    void addFriend(){
        userController.addFriend(1,2);
        verify(userService, times(1)).addFriend(1,2);
    }

    @Test
    void removeFromFriends(){
        userController.removeFromFriends(1,2);
        verify(userService, times(1)).removeFromFriends(1,2);
    }

    @Test
    void getAllFriends() {
        Collection<User> expectedFriends = List.of(getTestUser());
        when(userService.getAllFriends(2)).thenReturn(expectedFriends);

        Collection<User> actualUser = userController.getAllFriends(2);

        verify(userService, times(1)).getAllFriends(2);
        assertEquals(expectedFriends.size(), actualUser.size());
        assertSame(expectedFriends.stream().toList().get(0), actualUser.stream().toList().get(0));
        assertEquals(expectedFriends.stream().toList().get(0).getName(),
                actualUser.stream().toList().get(0).getName());
    }

    @Test
    void getCommonFriends() {
        Collection<User> expectedFriends = List.of(getTestUser());
        when(userService.getCommonFriends(2,3)).thenReturn(expectedFriends);

        Collection<User> actualUser = userController.getCommonFriends(2,3);

        verify(userService, times(1)).getCommonFriends(2,3);
        assertEquals(expectedFriends.size(), actualUser.size());
        assertSame(expectedFriends.stream().toList().get(0), actualUser.stream().toList().get(0));
        assertEquals(expectedFriends.stream().toList().get(0).getName(),
                actualUser.stream().toList().get(0).getName());
    }

    @Test
    void findUserById() {
        User expectedUsers = getTestUser();
        when(userService.findUserById(1)).thenReturn(expectedUsers);

        User actualUser = userController.findUserById(1);

        verify(userService, times(1)).findUserById(1);
        assertEquals(expectedUsers.getName(), actualUser.getName());
        assertSame(expectedUsers, actualUser);
    }
}
