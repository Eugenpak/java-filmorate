package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;


import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@ExtendWith(MockitoExtension.class)
class FilmControllerTest {
    private Film film;

    @Mock
    private FilmStorage filmStorage;
    @Mock
    private UserService userService;

    @InjectMocks
    private FilmService filmService;

    void validateInput(Film film) throws ConstraintViolationException {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
    }

    private User getTestUser() {
        return User.builder().id(1L).email("test@mail.ru").login("login")
                .name("name").birthday(LocalDate.of(1970,1,1)).build();
    }

    @BeforeEach
    public void initEach() {
        LocalDate releaseFilm = LocalDate.of(1970,1,1); // 1970-01-01
        film = Film.builder()
                .id(1L)
                .name("name film")
                .description("description film")
                .releaseDate(releaseFilm)
                .duration(120)
                .build();
    }

    @Test
    void shouldNotNullNameValidation() {
        film.setName(null);
        try {
            validateInput(film);
        } catch (ConstraintViolationException ex) {
            assertEquals(ex.getMessage(),"name: Имя фильма обязательно");
        }
    }

    @Test
    void shouldNotBlankNameValidation() {
        film.setName("   ");
        try {
            validateInput(film);
        } catch (ConstraintViolationException ex) {
            assertEquals(ex.getMessage(),"name: Имя фильма обязательно");
        }
    }

    @Test
    void shouldNotPassMax200DescriptionValidation() {
        film.setDescription("Матрица New ".repeat(500));
        try {
            validateInput(film);
        } catch (ConstraintViolationException ex) {
            assertEquals(ex.getMessage(),"description: Описание фильма должно содержать до 200 символов");
        }
    }

    //@Test
    void shouldNotPassReleaseDateValidation() {
        /*
        LocalDate releaseFilm = LocalDate.of(0,1,1);
        film.setId(null);
        film.setReleaseDate(releaseFilm);
        User user = getTestUser();
        when(filmStorage.create(film)).thenReturn(film);
        when(userService.findUserById(1)).thenReturn(user);


        //UserStorage userStorage = new InMemoryUserStorage();
        //FriendStorage friendStorage = new FriendDbStorage()
        //UserService userService = new UserService(userStorage);
        //FilmStorage filmStorage = new InMemoryFilmStorage();
        FilmService filmService = new FilmService(filmStorage,userService);

        FilmController fc = new FilmController(filmService);
        try {
           fc.create(film);
        } catch (ValidationException ex) {
            assertEquals(ex.getMessage(),"Дата релиза — не раньше 28 декабря 1895 года.");
        }

         */
    }

    @Test
    void shouldNotNegativeDurationValidation() {
        film.setDuration(-10);
        try {
            validateInput(film);
        } catch (ConstraintViolationException ex) {
            assertEquals(ex.getMessage(),"duration: Продолжительность фильма >= 0");
        }
    }

    //@Test
    void findAll() {
        /*
        UserStorage userStorage = new InMemoryUserStorage();
        UserService userService = new UserService(userStorage);
        FilmStorage filmStorage = new InMemoryFilmStorage();
        FilmService filmService = new FilmService(filmStorage,userService);

        FilmController fc = new FilmController(filmService);

        film.setId(null);
        fc.create(film);

        assertEquals(1,fc.findAll().size());
        assertEquals(1,fc.findAll().stream().toList().get(0).getId());
        */

    }

    //@Test
    void createFilm() {
        /*
        UserStorage userStorage = new InMemoryUserStorage();
        UserService userService = new UserService(userStorage);
        FilmStorage filmStorage = new InMemoryFilmStorage();
        FilmService filmService = new FilmService(filmStorage,userService);

        FilmController fc = new FilmController(filmService);

        film.setId(null);
        Film createdFilm = fc.create(film);
        assertEquals(1,createdFilm.getId());
        assertEquals("name film",createdFilm.getName());
        assertEquals("description film",createdFilm.getDescription());
        assertEquals(120,createdFilm.getDuration());
        */
    }

    //@Test
    void updateFilm() {
        /*
        UserStorage userStorage = new InMemoryUserStorage();
        UserService userService = new UserService(userStorage);
        FilmStorage filmStorage = new InMemoryFilmStorage();
        FilmService filmService = new FilmService(filmStorage,userService);

        FilmController fc = new FilmController(filmService);

        film.setId(null);
        Film savedFilm = fc.create(film);

        Film modifiedfilm = Film.builder()
                .id(1L)
                .name("UPDATE film")
                .description("UPDATE film")
                .releaseDate(new Date(0))
                .duration(200)
                .build();
        final Film updateFilm = fc.update(modifiedfilm);
        assertEquals(updateFilm,savedFilm);
        assertEquals(1,updateFilm.getId());
        assertEquals("UPDATE film",updateFilm.getName());
        assertEquals("UPDATE film",updateFilm.getDescription());
        assertEquals(200,updateFilm.getDuration());
        assertEquals(1,fc.findAll().size());
        */
    }
}