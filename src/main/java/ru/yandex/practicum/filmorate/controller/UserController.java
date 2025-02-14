package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.Instant;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/users")
@Validated
@Slf4j
public class UserController {
    private final Map<Long, User> users = new HashMap<>();

    @GetMapping
    public Collection<User> findAll() {
        log.info("Start User findAll()");
        return users.values();
    }

    @PostMapping
    public User create(@Valid @RequestBody User user) {
        log.info("Start User create()");
        log.info("POST->Body User = " + user);
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
        // формируем дополнительные данные
        user.setId(getNextId());

        // сохраняем новую публикацию в памяти приложения
        users.put(user.getId(), user);
        log.info("Новый пользователь сохранен (id=" + user.getId() + ", email='" + user.getEmail() + "')");
        return user;
    }

    private long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

    private User getUserEmail(String email) {
        User firstUser = users.values()
                .stream()
                .filter(u -> u.getEmail().equals(email))
                .findFirst()
                .orElse(null);
        return firstUser;
    }

    @PutMapping
    public User update(@Valid @RequestBody User newUser) {
        // проверяем необходимые условия
        log.info("Start User update()");
        log.info("PUT->Body User = " + newUser);
        if (newUser.getId() == null) {
            log.error("ValidationException id");
            throw new ValidationException("Id должен быть указан");
        }
        if (getUserEmail(newUser.getEmail()) != null &&
                getUserEmail(newUser.getEmail()).getId().longValue() != newUser.getId().longValue()) {
            log.error("ValidationException email");
            throw new ValidationException("Этот имейл уже используется");
        }
        if (users.containsKey(newUser.getId())) {
            User oldUser = users.get(newUser.getId());
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
            log.info("Данные пользователя обновлены (id=" + newUser.getId() + ", email='" + newUser.getEmail() + "')");
            return oldUser;
        }
        throw new NotFoundException("Пользователь с id = " + newUser.getId() + " не найден");
    }
}

