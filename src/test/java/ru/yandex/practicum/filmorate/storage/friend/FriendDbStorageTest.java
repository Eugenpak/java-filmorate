package ru.yandex.practicum.filmorate.storage.friend;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FriendDbStorageTest {
    private final FriendDbStorage friendDbStorage;

    private final UserDbStorage userStorage;

    private List<User> getTestUser() {
        User one = User.builder().email("test-one@mail.ru").login("login-A")
                .name("name-1").birthday(LocalDate.of(1980,3,5)).build();
        User two = User.builder().email("test-two@mail.ru").login("login-B")
                .name("name-2").birthday(LocalDate.of(1970,7,17)).build();
        User upd = User.builder().email("test-3@mail.ru").login("login-C")
                .name("name-3").birthday(LocalDate.of(1990,9,3)).build();
        User del = User.builder().email("test-4@mail.ru").login("login-C")
                .name("name-4").birthday(LocalDate.of(2000,4,3)).build();
        return List.of(one,two,upd,del);
    }

    @Test
    void getFriendsAll() {
        friendDbStorage.delAllFriends();
        userStorage.delAllUsers();
        User user1 = userStorage.create(getTestUser().get(0));
        List<User> list = friendDbStorage.getFriendsAll(user1.getId()).stream().toList();
        assertEquals(0, list.size());
        User user2 = userStorage.create(getTestUser().get(1));
        boolean flagFriend = friendDbStorage.addFriend(user1.getId(),user2.getId());
        assertTrue(flagFriend);
    }

    @Test
    void getFriendsCommon() {
        friendDbStorage.delAllFriends();
        userStorage.delAllUsers();
        User user1 = userStorage.create(getTestUser().get(0));
        List<User> list1 = friendDbStorage.getFriendsAll(user1.getId()).stream().toList();
        assertEquals(0, list1.size());
        User user2 = userStorage.create(getTestUser().get(1));
        List<User> list2 = friendDbStorage.getFriendsAll(user2.getId()).stream().toList();
        assertEquals(0, list2.size());
        List<User> listCommon = friendDbStorage.getFriendsCommon(user1.getId(),user2.getId()).stream().toList();
        assertEquals(0, listCommon.size());
        User user3 = userStorage.create(getTestUser().get(2));
        friendDbStorage.addFriend(user1.getId(),user3.getId());
        friendDbStorage.addFriend(user2.getId(),user3.getId());
        listCommon = friendDbStorage.getFriendsCommon(user1.getId(),user2.getId()).stream().toList();
        assertEquals(1, listCommon.size());
        assertEquals(user3.getId(), listCommon.get(0).getId());
    }

    @Test
    void addFriend() {
        friendDbStorage.delAllFriends();
        userStorage.delAllUsers();
        User user1 = userStorage.create(getTestUser().get(0));
        List<User> list1 = friendDbStorage.getFriendsAll(user1.getId()).stream().toList();
        assertEquals(0, list1.size());
        User user2 = userStorage.create(getTestUser().get(1));
        friendDbStorage.addFriend(user1.getId(),user2.getId());
        list1 = friendDbStorage.getFriendsAll(user1.getId()).stream().toList();
        assertEquals(1, list1.size());
        assertEquals(user2.getId(), list1.get(0).getId());
    }

    @Test
    void removeFromFriends() {
        friendDbStorage.delAllFriends();
        userStorage.delAllUsers();
        User user1 = userStorage.create(getTestUser().get(0));
        List<User> list1 = friendDbStorage.getFriendsAll(user1.getId()).stream().toList();
        assertEquals(0, list1.size());
        User user2 = userStorage.create(getTestUser().get(1));
        friendDbStorage.addFriend(user1.getId(),user2.getId());
        list1 = friendDbStorage.getFriendsAll(user1.getId()).stream().toList();
        assertEquals(1, list1.size());
        boolean flagFriend = friendDbStorage.removeFromFriends(user1.getId(),user2.getId());
        assertTrue(flagFriend);
    }
}