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
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
                .name("name").birthday(LocalDate.of(1970, 1, 1)).build();
    }

    @BeforeEach
    public void initEach() {
        LocalDate releaseFilm = LocalDate.of(1970, 1, 1); // 1970-01-01
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

    @Test
    void shouldNotNegativeDurationValidation() {
        film.setDuration(-10);
        try {
            validateInput(film);
        } catch (ConstraintViolationException ex) {
            assertEquals(ex.getMessage(),"duration: Продолжительность фильма >= 0");
        }
    }
}