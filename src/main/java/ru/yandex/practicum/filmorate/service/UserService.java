package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import java.time.Instant;
import java.util.Collection;
import java.util.Date;

@Service
@Slf4j
public class UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public Collection<User> findAll() {
        log.info("Start User findAll()");
        return userStorage.findAll();
    }

    public User create(User user) {
        //log.info("Start User create()");
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
        if (user.getBirthday() == null || user.getBirthday().after(Date.from(Instant.now()))) {
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
        log.info("Start User update()");
        log.info("PUT->Body User = " + newUser);
        if (newUser.getId() == null) {
            log.error("ValidationException id");
            throw new ValidationException("Id должен быть указан");
        }
        if (!newUser.getEmail().contains("@")) {
            log.error("ValidationException email");
            throw new ValidationException("Этот имейл уже используется");
        }
        User oldUser = findUserById(newUser.getId());

        if (newUser.getEmail() != null) {
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
        log.info("Данные пользователя обновляются (id=" + newUser.getId() + ", email='" + newUser.getEmail() + "')");
        return userStorage.update(oldUser);
    }

    public User findUserById(long id) {
        User findUser = userStorage.findUserById(id);
        if (findUser == null) {
            throw new NotFoundException("Пользователь с id = " + id + " не найден");
        }
        return findUser;
    }

    public void addFriend(long userId,long friendId) {
        final User user = findUserById(userId);
        final User friend = findUserById(friendId);
        userStorage.addFriend(userId,friendId);
        log.info("Service user: '" + user.getName() + "'(id=" + userId + ") и '" + friend.getName() +
                "'(id=" + friendId + "-> addFriend()");
    }

    public void removeFromFriends(long userId,long friendId) {
        final User user = findUserById(userId);
        final User friend = findUserById(friendId);
        userStorage.removeFromFriends(userId,friendId);
        log.info("Service user: '" + user.getName() + "'(id=" + userId + ") и '" + friend.getName() +
                "'(id=" + friendId + "-> removeFromFriends()");
    }

    public Collection<User> getAllFriends(long userId) {
        findUserById(userId);
        return userStorage.getAllFriends(userId);
    }

    public Collection<User> getCommonFriends(long userId,long otherId) {
        findUserById(userId);
        findUserById(otherId);
        return userStorage.getCommonFriends(userId,otherId);
    }
}
