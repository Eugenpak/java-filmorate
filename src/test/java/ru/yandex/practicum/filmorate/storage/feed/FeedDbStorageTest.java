package ru.yandex.practicum.filmorate.storage.feed;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.UserFeed;
import ru.yandex.practicum.filmorate.service.UserService;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FeedDbStorageTest {
    @Autowired
    private FeedDbStorage feedDbStorage;
    @Autowired
    private UserService userService;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final User user1 = User.builder().email("test-one@mail.ru").login("login-A")
            .name("name-1").birthday(LocalDate.of(1980, 3, 5)).build();
    private final User user2 = User.builder().email("test-two@mail.ru").login("login-B")
            .name("name-2").birthday(LocalDate.of(1981, 3, 5)).build();
    private final User user3 = User.builder().email("test-two@mail.ru").login("login-B")
            .name("name-2").birthday(LocalDate.of(1981, 3, 5)).build();

    @AfterEach
    void tearDown() {
        jdbcTemplate.execute("DELETE FROM users");
        jdbcTemplate.execute("ALTER TABLE users ALTER COLUMN id RESTART WITH 1");
    }


    @Test
    void getUserFeedByIdTest() {
        userService.create(user1);
        userService.create(user2);
        userService.create(user3);
        userService.addFriend(1, 2);
        userService.removeFromFriends(1, 2);
        userService.addFriend(1, 3);
        List<UserFeed> userFeeds = feedDbStorage.getUserFeedById(1);
        assertEquals(3, userFeeds.size());

    }

    @Test
    void addFeedTest() {
        userService.create(user1);
        userService.create(user2);
        userService.addFriend(1, 2);
        UserFeed userFeed = new UserFeed();
        userFeed = feedDbStorage.getUserFeedById((long) 1).getFirst();
        assertEquals(1, userFeed.getEventId());
        assertEquals(1, userFeed.getUserId());
        assertEquals("FRIEND", userFeed.getEventType());
        assertEquals("ADD", userFeed.getOperation());
        assertEquals(2, userFeed.getEntityId());
    }


}