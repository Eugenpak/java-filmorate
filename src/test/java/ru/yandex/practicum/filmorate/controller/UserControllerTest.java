package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.sql.Date;
import java.util.Collection;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT) //RANDOM_PORT
class UserControllerTest {
    @Autowired
    TestRestTemplate template;

    void validateInput(User user) throws ConstraintViolationException {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
    }

    @Test
    void probaAwa() {
        ResponseEntity<User[]> entity = template.getForEntity("/users", User[].class);

        assertEquals(HttpStatus.OK,entity.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON,entity.getHeaders().getContentType());

        User[] users = entity.getBody();
    }

    @Test
    void invalidUserIdShouldReturn404() {
        User entity = template.getForObject("/users/99", User.class);
        //assertEquals(HttpStatus.NOT_FOUND, entity.getStatusCode());
    }

    @Test
    void findAll() {
        UserStorage userStorage = new InMemoryUserStorage();
        UserService userService = new UserService(userStorage);
        UserController uc = new UserController(userService);

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
            assertEquals("email: must not be blank",ex.getMessage());
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

        UserStorage userStorage = new InMemoryUserStorage();
        UserService userService = new UserService(userStorage);
        UserController uc = new UserController(userService);

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
        UserStorage userStorage = new InMemoryUserStorage();
        UserService userService = new UserService(userStorage);
        UserController uc = new UserController(userService);

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

        UserStorage userStorage = new InMemoryUserStorage();
        UserService userService = new UserService(userStorage);
        UserController uc = new UserController(userService);

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

        UserStorage userStorage = new InMemoryUserStorage();
        UserService userService = new UserService(userStorage);
        UserController uc = new UserController(userService);

        User createdUserT = uc.create(user);
        assertEquals(1,createdUserT.getId());
        assertEquals("test@mail.ru",createdUserT.getEmail());
        assertEquals("testLogin",createdUserT.getLogin());
        assertEquals("testName",createdUserT.getName());
        assertEquals(birthday,createdUserT.getBirthday());
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

        UserStorage userStorage = new InMemoryUserStorage();
        UserService userService = new UserService(userStorage);
        UserController uc = new UserController(userService);

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