package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.friend.FriendStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT) //RANDOM_PORT
@ExtendWith(MockitoExtension.class)
class UserControllerTest {
    @Mock
    private UserStorage userStorage;

    @Mock
    private FriendStorage friendStorage;

    @InjectMocks
    private UserService userService;

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

    private User getTestUser() {
        return User.builder().id(1L).email("test@mail.ru").login("login")
                .name("name").birthday(LocalDate.of(1970, 1, 1)).build();
    }

    @Test
    void probaAwa() {
        ResponseEntity<User[]> entity = template.getForEntity("/users", User[].class);

        assertEquals(HttpStatus.OK, entity.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, entity.getHeaders().getContentType());

        User[] users = entity.getBody();
    }

    @Test
    void invalidUserIdShouldReturn404() {
        User entity = template.getForObject("/users/99", User.class);
        //assertEquals(HttpStatus.NOT_FOUND, entity.getStatusCode());
    }


    @Test
    void shouldNotBlankEmailValidation() {
        LocalDate birthday = LocalDate.of(1970, 1, 1); // 1970-01-01
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
            assertEquals("email: must not be blank", ex.getMessage());
        }
    }

    @Test
    void shouldNoPassEmailValidation() {
        LocalDate birthday = LocalDate.of(1970, 1, 1); // 1970-01-01
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
            assertEquals(ex.getMessage(), "email: Недопустимый email. Попробуйте снова.");
        }
    }

    @Test
    void shouldNotPassLoginValidation() {
        LocalDate birthday = LocalDate.of(1970, 1, 1); // 1970-01-01
        User user = User.builder()
                .id(1L)
                .email("test@mail.ru")
                .login("") // Login не должно быть пустым
                .name("name")
                .birthday(birthday)
                .build();
        try {
            validateInput(user);
        } catch (ConstraintViolationException ex) {
            assertEquals("login: must not be blank", ex.getMessage());
        }
    }

    //---------------------------------------------------------------------------------
    /*

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

    //@Test
    void shouldNotBlankNameValidation() {
        /*
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

    //@Test
    void createUser() {
        /*
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

    //@Test
    void updateUser() {
        /*
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

    }  */

    //@Test
    void updateUserCheckMail() {
        /*
        Date birthday = new Date(0L); // 1970-01-01
        User user = User.builder().login("testLogin").name("testName").birthday(birthday)
                .email("test@mail.ru").build();
        User userOther = User.builder().login("Login-2").name("Name-2").birthday(birthday)
                .email("test_m@mail.ru").build();

        UserStorage userStorage = new InMemoryUserStorage();
        UserService userService = new UserService(userStorage);
        UserController uc = new UserController(userService);

        User savedUser = uc.create(user);
        uc.create(userOther);
        User modifiedUser = User.builder()
                .id(1L)
                .email("test_m@mail.ru") //update@mail.ru
                .login("testLogin")
                .name("Name after update")
                .birthday(birthday)
                .build();
        final User updateUser = uc.update(modifiedUser);
        assertEquals(updateUser,savedUser);
        assertEquals(1,updateUser.getId());
        assertNotEquals("test_m@mail.ru",updateUser.getEmail());
        assertEquals("Name after update",updateUser.getName());
        assertEquals(2,uc.findAll().size());
        */
    }

    /*
    @Test
    void findAll() {
        List<User> expectedUsers = List.of(getTestUser());
        when(userStorage.findAll()).thenReturn(expectedUsers);
        //UserStorage userStorage = new InMemoryUserStorage();
        UserService userService = new UserService(userStorage,friendStorage);
        UserController uc = new UserController(userService);

        Collection<User> response = uc.findAll();
        assertEquals(0,response.size());
    }
    */
}