package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.feed.FeedStorage;
import ru.yandex.practicum.filmorate.storage.friend.FriendStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    private UserStorage userStorage;

    @Mock
    private FriendStorage friendStorage;

    @Mock
    private FeedStorage feedStorage;

    @InjectMocks
    private UserService userService;

    private User getTestUser() {
        return User.builder().id(1L).email("test@mail.ru").login("login")
                .name("name").birthday(LocalDate.of(1970, 1, 1)).build();
    }

    private User getTestOtherUser() {
        return User.builder().id(2L).email("other@mail.ru").login("login-2")
                .name("name-2").birthday(LocalDate.of(1970, 1, 1)).build();
    }

    @Test
    void findAll() {
        List<User> expectedUsers = List.of(getTestUser());
        when(userStorage.findAll()).thenReturn(expectedUsers);

        List<User> actualUser = userService.findAll().stream().toList();
        verify(userStorage, times(1)).findAll();
        assertEquals(expectedUsers.size(), actualUser.size());
        assertSame(expectedUsers.get(0), actualUser.get(0));
    }

    @Test
    void create() {
        User expectedUsers = getTestUser();
        when(userStorage.create(expectedUsers)).thenReturn(expectedUsers);

        User actualUser = userService.create(expectedUsers);

        verify(userStorage, times(1)).create(expectedUsers);
        assertEquals(expectedUsers.getName(), actualUser.getName());
        assertSame(expectedUsers, actualUser);
    }

    @Test
    void update() {
        User expectedUser = getTestUser();
        when(userStorage.update(expectedUser)).thenReturn(expectedUser);
        when(userStorage.findUserById(expectedUser.getId())).thenReturn(Optional.of(expectedUser));

        User actualUser = userService.update(expectedUser);

        verify(userStorage, times(1)).update(expectedUser);
        verify(userStorage, times(1)).findUserById(expectedUser.getId());
        assertEquals(expectedUser.getName(), actualUser.getName());
        assertEquals(expectedUser.getId(), actualUser.getId());
        assertSame(expectedUser, actualUser);
    }

    @Test
    void addFriend() {
        User expectedUser = getTestUser();
        User otherUser = getTestOtherUser();
        when(userStorage.findUserById(expectedUser.getId())).thenReturn(Optional.of(expectedUser));
        when(userStorage.findUserById(otherUser.getId())).thenReturn(Optional.of(otherUser));

        userService.addFriend(1, 2);
        verify(friendStorage, times(1)).addFriend(1, 2);
        verify(userStorage, times(1)).findUserById(expectedUser.getId());
        verify(userStorage, times(1)).findUserById(otherUser.getId());
    }

    @Test
    void removeFromFriends() {
        User expectedUser = getTestUser();
        User otherUser = getTestOtherUser();
        when(userStorage.findUserById(expectedUser.getId())).thenReturn(Optional.of(expectedUser));
        when(userStorage.findUserById(otherUser.getId())).thenReturn(Optional.of(otherUser));

        userService.removeFromFriends(1, 2);
        verify(friendStorage, times(1)).removeFromFriends(1, 2);
        verify(userStorage, times(1)).findUserById(1);
        verify(userStorage, times(1)).findUserById(2);
    }

    @Test
    void getAllFriends() {
        Collection<User> expectedFriends = List.of(getTestUser());
        when(friendStorage.getFriendsAll(2)).thenReturn(expectedFriends);
        when(userStorage.findUserById(2)).thenReturn(Optional.of(getTestUser()));

        Collection<User> actualUser = userService.getAllFriends(2);

        verify(friendStorage, times(1)).getFriendsAll(2);
        verify(userStorage, times(1)).findUserById(2);
        assertEquals(expectedFriends.size(), actualUser.size());
        assertSame(expectedFriends.stream().toList().get(0), actualUser.stream().toList().get(0));
        assertEquals(expectedFriends.stream().toList().get(0).getName(),
                actualUser.stream().toList().get(0).getName());
    }

    @Test
    void getCommonFriends() {
        Collection<User> expectedFriends = List.of(getTestUser());
        when(friendStorage.getFriendsCommon(2, 3)).thenReturn(expectedFriends);
        when(userStorage.findUserById(2)).thenReturn(Optional.of(getTestUser()));
        when(userStorage.findUserById(3)).thenReturn(Optional.of(getTestOtherUser()));

        Collection<User> actualUser = userService.getCommonFriends(2, 3);

        verify(friendStorage, times(1)).getFriendsCommon(2, 3);
        verify(userStorage, times(1)).findUserById(2);
        verify(userStorage, times(1)).findUserById(3);
        assertEquals(expectedFriends.size(), actualUser.size());
        assertSame(expectedFriends.stream().toList().get(0), actualUser.stream().toList().get(0));
        assertEquals(expectedFriends.stream().toList().get(0).getName(),
                actualUser.stream().toList().get(0).getName());
    }

    @Test
    void findFilmById() {
        User expectedUsers = getTestUser();
        when(userStorage.findUserById(1)).thenReturn(Optional.of(expectedUsers));

        User actualUser = userService.findUserById(1);

        verify(userStorage, times(1)).findUserById(1);
        assertEquals(expectedUsers.getName(), actualUser.getName());
        assertSame(expectedUsers, actualUser);
    }
}