package ru.yandex.practicum.filmorate.storage.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.User;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserDbStorageTest {
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
    void findAll() {
        int sizeBefore = userStorage.findAll().size();
        User expectedUser = getTestUser().get(0);
        User actualUser = userStorage.create(expectedUser);
        int sizeAfter = userStorage.findAll().size();
        assertEquals(sizeBefore+1, sizeAfter);
    }

    @Test
    void  create() {
        User expectedUser = getTestUser().get(1);
        int sizeBeforeCreate = userStorage.findAll().size();
        User actualUser = userStorage.create(expectedUser);
        int sizeAfterCreate = userStorage.findAll().size();
        Long id = actualUser.getId();
        Optional<User> userOptional = userStorage.findUserById(actualUser.getId());

        assertThat(userOptional).isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("id", id)
                );
        assertEquals(sizeBeforeCreate+1, sizeAfterCreate);
    }
    @Test void  update() {
        User expectedUser = userStorage.create(getTestUser().get(2));
        String expectedName = expectedUser.getName();
        long expectedId = expectedUser.getId();
        expectedUser.setName("After NAME update in UserDbStorage");
        User actualUser = userStorage.update(expectedUser);
        assertEquals( "After NAME update in UserDbStorage",actualUser.getName());
        assertEquals(expectedId, actualUser.getId());
    }
    @Test void  delUserById() {
        if (userStorage.findAll().size() >= 1) {
            userStorage.delAllUsers();
        }
        assertEquals(0, userStorage.findAll().size());
        User expectedUser = userStorage.create(getTestUser().get(0));
        assertEquals(1, userStorage.findAll().size());
        userStorage.delUserById(expectedUser.getId());
        assertEquals(0, userStorage.findAll().size());
    }

    @Test
    void testFindUserById() {
        Assertions.assertNotNull(userStorage);
        User expectedUser = getTestUser().get(3);
        User actualUser = userStorage.create(expectedUser);
        Long id = actualUser.getId();
        Optional<User> userOptional = userStorage.findUserById(actualUser.getId());

        assertThat(userOptional).isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("id", id)
                );
    }
}