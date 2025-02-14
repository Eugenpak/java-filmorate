package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.*;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.Date;
import java.util.Collection;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserControllerTest {
    void validateInput(User user) throws ConstraintViolationException {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
    }

    @Test
    void findAll() {
        UserController uc = new UserController();

        Collection<User> response = uc.findAll();
        assertEquals(0,response.size());
    }

    @Test
    void shouldNotBlankEmailValidation() {
        Date birthday = new Date(0); // 1970-01-01
        User user = User.builder()
                .id(1L)
                .email("")
                .login("testUser")
                .name("name")
                .birthday(birthday)
                .build();
        try {
            validateInput(user);
        } catch (ConstraintViolationException ex) {
            assertEquals("email: не должно быть пустым",ex.getMessage());
        }
    }

    @Test
    void shouldNoPassEmailValidation() {
        Date birthday = new Date(0); // 1970-01-01
        User user = User.builder()
                .id(1L)
                .email("testmail.ru@")
                .login("testUser")
                .name("name")
                .birthday(birthday)
                .build();
        try {
            validateInput(user);
        } catch (ConstraintViolationException ex) {
            assertEquals(ex.getMessage(),"email: Недопустимый email. Попробуйте снова.");
        }
    }

    @Test
    void shouldNotPassLoginValidation() {
        Date birthday = new Date(0); // 1970-01-01
        User user = User.builder()
                .id(1L)
                .email("test@mail.ru")
                .login("") // Login не должно быть пустым
                .name("name")
                .birthday(birthday)
                .build();
        try {
            validateInput(user);
        } catch (ConstraintViolationException  ex) {
            assertEquals("login: must not be blank",ex.getMessage());
        }
    }

    @Test
    void shouldNoPass2LoginValidation() {
        Date birthday = new Date(0); // 1970-01-01
        User user = User.builder()
                .email("test@mail.ru")
                .login("test User") // Login содержит пробел
                .name("name")
                .birthday(birthday)
                .build();

        UserController uc = new UserController();

        try {
            uc.create(user);
        } catch (ValidationException ex) {
            assertEquals(ex.getMessage(),"Логин не может быть пустым и содержать пробелы.");
        }
    }

    @Test
    void shouldNotBlankNameValidation() {
        Date birthday = new Date(0); // 1970-01-01
        User user = User.builder()
                .email("test@mail.ru")
                .login("testUser")
                .name("") // name не может быть пустым
                .birthday(birthday)
                .build();

        UserController uc = new UserController();
        User createdUser = uc.create(user);
        assertEquals("testUser",createdUser.getName());
        assertEquals(1,createdUser.getId());
    }

    @Test
    void shouldNotFutureBirthdayValidation() {
        Date birthday = new Date(10_000_000_000_000L); // 2286-11-20
        User user = User.builder()
                .email("test@mail.ru")
                .login("testUser")
                .name("") // name не может быть пустым
                .birthday(birthday)
                .build();

        UserController uc = new UserController();
        try {
            uc.create(user);
        } catch (ValidationException ex) {
            assertEquals(ex.getMessage(),"Дата рождения не может быть в будущем.");
        }
    }

    @Test
    void createUser() {
        Date birthday = new Date(0L); // 1970-01-01
        User user = User.builder()
                .email("test@mail.ru")
                .login("testLogin")
                .name("testName")
                .birthday(birthday)
                .build();

        UserController uc = new UserController();
        User createdUser = uc.create(user);
        assertEquals(1,createdUser.getId());
        assertEquals("test@mail.ru",createdUser.getEmail());
        assertEquals("testLogin",createdUser.getLogin());
        assertEquals("testName",createdUser.getName());
        assertEquals(birthday,createdUser.getBirthday());
    }

    @Test
    void updateUser() {
        Date birthday = new Date(0L); // 1970-01-01
        User user = User.builder()
                .email("test@mail.ru")
                .login("testLogin")
                .name("testName")
                .birthday(birthday)
                .build();

        UserController uc = new UserController();
        User savedUser = uc.create(user);
        User modifiedUser = User.builder()
                .id(1L)
                .email("update@mail.ru")
                .login("testLogin")
                .name("Name after update")
                .birthday(birthday)
                .build();
        final User updateUser = uc.update(modifiedUser);
        assertEquals(updateUser,savedUser);
        assertEquals(1,updateUser.getId());
        assertEquals("update@mail.ru",updateUser.getEmail());
        assertEquals("Name after update",updateUser.getName());
        assertEquals(1,uc.findAll().size());
    }
}