package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.UserFeed;
import ru.yandex.practicum.filmorate.storage.feed.FeedStorage;
import ru.yandex.practicum.filmorate.storage.friend.FriendStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class UserService {
    private final UserStorage userStorage;
    private final FriendStorage friendStorage;
    private final FeedStorage feedStorage;

    @Autowired
    public UserService(@Qualifier("UserDbStorage") UserStorage userStorage, FriendStorage friendStorage,
                       FeedStorage feedStorage) {
        this.userStorage = userStorage;
        this.friendStorage = friendStorage;
        this.feedStorage = feedStorage;
    }

    public Collection<User> findAll() {
        log.info("Start User findAll()");
        return userStorage.findAll();
    }

    public User create(User user) {
        log.info("US->Service User = " + user);
        if (user.getEmail() == null || user.getEmail().isBlank() || !user.getEmail().contains("@")) {
            String str = "Имейл должен быть указан, содержать символ '@'";
            log.error(str);
            throw new ValidationException(str);
        }
        if (user.getLogin().contains(" ")) {
            String str = "Логин не может быть пустым и содержать пробелы.";
            log.error(str);
            throw new ValidationException(str);
        }
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        if (user.getBirthday() == null || user.getBirthday().isAfter(LocalDate.now())) {
            String str = "Дата рождения не может быть в будущем.";
            log.error(str);
            throw new ValidationException(str);
        }
        // сохраняем новую публикацию в памяти приложения
        User createdUser = userStorage.create(user);
        log.info("Новый пользователь сохранен S (id=" + createdUser.getId() + ", email='" + createdUser.getEmail() + "')");
        return createdUser;
    }

    public User update(User newUser) {
        // проверяем необходимые условия
        log.info("Start U-S update()");
        log.info("PUT->Body User = " + newUser);
        if (newUser.getId() == null) {
            log.error("ValidationException id");
            throw new ValidationException("Id должен быть указан");
        }
        if (!newUser.getEmail().contains("@")) { //
            log.error("ValidationException email");
            throw new ValidationException("Этот имейл неправильного формат");
        }
        User oldUser = findUserById(newUser.getId());

        if (newUser.getEmail() != null && !checkEmail(newUser, findAll())) {
            oldUser.setEmail(newUser.getEmail());
        }
        if (newUser.getLogin() != null) {
            oldUser.setLogin(newUser.getLogin());
        }
        if (newUser.getName() != null) {
            oldUser.setName(newUser.getName());
        }
        // если user найдена и все условия соблюдены, обновляем её содержимое
        oldUser.setBirthday(newUser.getBirthday());
        log.info("Данные пользователя обновляются (id=" + oldUser.getId() + ", email='" + oldUser.getEmail() + "')");
        return userStorage.update(oldUser);
    }

    private boolean checkEmail(User user, Collection<User> userCollection) {
        return userCollection
                .stream()
                .filter(u -> u.getEmail().equals(user.getEmail())) // Фильтруем по email
                .anyMatch(u -> u.getId() != user.getId()); // Проверяем, что это не тот же самый пользователь
    }

    public User findUserById(long id) {
        Optional<User> findUser = userStorage.findUserById(id);
        if (findUser.isEmpty()) {
            throw new NotFoundException("Пользователь с id = " + id + " не найден");
        }
        return findUser.get();
    }

    public void delUserById(long id) {
        log.info("Start U-S delUserById({})", id);
        boolean delUser = userStorage.delUserById(id);
        if (!delUser) {
            throw new NotFoundException("Пользователь с id = " + id + " не найден");
        }
        log.info("Удален User delUserById({})", id);
    }

    public void addFriend(long userId, long friendId) {
        log.info("Start U-S addFriend(userId:{},friendId:{})", userId, friendId);
        final User user = findUserById(userId);
        final User friend = findUserById(friendId);
        friendStorage.addFriend(userId, friendId);
        log.info("Service user: '" + user.getName() + "'(id=" + userId + ") и '" + friend.getName() +
                "'(id=" + friendId + "-> addFriend()");


        log.info("Перед сохранением АКТИВНОСТИ");
        feedStorage.addFeed(userId, "FRIEND", "ADD", friendId);
        log.info("После сохранением АКТИВНОСТИ");

    }

    public void removeFromFriends(long userId, long friendId) {
        log.info("Start U-S removeFromFriends(userId:{},friendId:{})", userId, friendId);
        final User user = findUserById(userId);
        final User friend = findUserById(friendId);
        friendStorage.removeFromFriends(userId, friendId);
        log.info("U-S удален друг user: '" + user.getName() + "'(id=" + userId + ") и '" + friend.getName() +
                "'(id=" + friendId + "-> removeFromFriends()");

        log.info("Перед сохранением АКТИВНОСТИ");
        feedStorage.addFeed(userId, "FRIEND", "DELETE", friendId);
        log.info("После сохранением АКТИВНОСТИ");
    }

    public Collection<User> getAllFriends(long userId) {
        log.info("Start U-S getAllFriends(userId:{})", userId);
        findUserById(userId);
        return friendStorage.getFriendsAll(userId);
    }

    public Collection<User> getCommonFriends(long userId, long otherId) {
        log.info("Start U-S getCommonFriends(userId:{},otherId:{})", userId, otherId);
        findUserById(userId);
        findUserById(otherId);
        return friendStorage.getFriendsCommon(userId, otherId);
    }

    public List<UserFeed> getUserFeedById(long id) {
        log.info("Started UserStorage -----> getUserFeedById");
        return feedStorage.getUserFeedById(id);
    }
}
