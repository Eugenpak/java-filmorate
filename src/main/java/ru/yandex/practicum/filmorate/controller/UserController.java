package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import java.util.Collection;


@RestController
@RequestMapping("/users")
@Validated
@Slf4j
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public Collection<User> findAll() {
        log.info("Start User findAll()");
        return userService.findAll();
    }

    @PostMapping
    public User create(@Valid @RequestBody User user) {
        log.info("Start User create()");
        log.info("POST->Body User = " + user);
        // сохраняем новую публикацию в памяти приложения
        log.info("UC Новый пользователь сохраняется (id=" + user.getId() + ", email='" + user.getEmail() + "')");
        return userService.create(user);
    }

    @PutMapping
    public User update(@Valid @RequestBody User user) {
        // проверяем необходимые условия
        log.info("Start User update()");
        log.info("PUT->Body User = " + user);
        return userService.update(user);
    }

    @PutMapping(value = "/{id}/friends/{friendId}")
    public void addFriend(@NotNull @PathVariable long id, @NotNull @PathVariable long friendId) {
        userService.addFriend(id, friendId);
        log.info("UC User addFriend(id=" + id + ",friendId=" + friendId + ")");
    }

    @DeleteMapping(value = "/{id}/friends/{friendId}")
    public void removeFromFriends(@NotNull @PathVariable long id, @NotNull @PathVariable long friendId) {
        userService.removeFromFriends(id, friendId);
        log.info("UC User removeFromFriends(id=" + id + "id=" + friendId + ")");
    }

    @GetMapping(value = "/{id}/friends")
    public Collection<User> getAllFriends(@NotNull @PathVariable long id) {
        log.info("UC User getAllFriends(id=" + id + ")");
        return userService.getAllFriends(id);
    }

    @GetMapping(value = "/{id}/friends/common/{otherId}")
    public Collection<User> getCommonFriends(@NotNull @PathVariable long id, @NotNull @PathVariable long otherId) {
        log.info("UC User getCommonFriends(id=" + id + ",otherId=" + otherId + ")");
        Collection<User> debugList = userService.getCommonFriends(id,otherId);
        log.info("UC User getCommonFriends()=" + debugList);
        return debugList;
    }

    @GetMapping(value = "/{id}")
    public User findUserById(@NotNull @PathVariable long id) {
        log.info("UC User findUserById(id=" + id + ")");
        return userService.findUserById(id);
    }
}

