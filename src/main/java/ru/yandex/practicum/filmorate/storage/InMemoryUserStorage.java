package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Component("inMemoryUserStorage")
@Slf4j
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();

    @Override
    public Collection<User> findAll() {
        return users.values();
    }

    @Override
    public User create(User user) {
        // формируем дополнительные данные
        user.setId(getNextId());
        // сохраняем новую публикацию в памяти приложения
        log.info("InMemoryUS pre-Save User = " + user);
        users.put(user.getId(), user);
        final User userMap = users.get(user.getId());
        log.info("InMemoryUS post-Save User = " + userMap);
        return userMap;
    }

    private long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

    @Override
    public User update(User user) {
        return users.put(user.getId(), user);
    }

    @Override
    public User findUserById(long id) {
        if (users.containsKey(id)) {
            return users.get(id);
        }
        return null;
    }

    @Override
    public void addFriend(long userId, long friendId) {
        User user = findUserById(userId);
        user.getFriends().add(friendId);
        User friend = findUserById(friendId);
        friend.getFriends().add(userId);
    }

    @Override
    public void removeFromFriends(long userId, long friendId) {
        User user = findUserById(userId);
        User friend = findUserById(friendId);
        user.getFriends().remove(friendId);
        friend.getFriends().remove(userId);

    }

    @Override
    public Collection<User> getAllFriends(long userId) {
        return users.get(userId).getFriends().stream().map(users::get).toList();
    }

    @Override
    public Collection<User> getCommonFriends(long userId,long otherId) {
        Set<Long> result = new HashSet<>(findUserById(userId).getFriends());
        log.info("InMemoryUS User getCommonFriends(userId)=" + result);
        result.retainAll(findUserById(otherId).getFriends());
        log.info("InMemoryUS User getCommonFriends(otherId)=" + findUserById(otherId).getFriends());
        return result.stream().map(users::get).toList();
    }
}