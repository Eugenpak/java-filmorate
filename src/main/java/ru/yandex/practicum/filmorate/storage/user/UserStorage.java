package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;
import java.util.Collection;
import java.util.Optional;

public interface UserStorage {
    Collection<User> findAll();

    User create(User user);

    User update(User user);

    Optional<User> findUserById(long id);

    void addFriend(long userId, long friendId);

    void removeFromFriends(long userId, long friendId);

    Collection<User> getAllFriends(long userId);

    Collection<User> getCommonFriends(long userId,long otherId);

    boolean delUserById(long id);

    boolean delAllUsers();
}
