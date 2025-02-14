package ru.yandex.practicum.filmorate.controller;


import jakarta.validation.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class FilmControllerTest {
    private Film film;

    void validateInput(Film film) throws ConstraintViolationException {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
    }

    @BeforeEach
    public void initEach() {
        Date releaseFilm = new Date(0); // 1970-01-01
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
    void shouldNotPassReleaseDateValidation() {
        Date releaseFilm = new Date(-10_000_000_000_000L);
        film.setId(null);
        film.setReleaseDate(releaseFilm);
        FilmController fc = new FilmController();
        try {
           fc.create(film);
        } catch (ValidationException ex) {
            assertEquals(ex.getMessage(),"Дата релиза — не раньше 28 декабря 1895 года.");
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

    @Test
    void findAll() {
        FilmController fc = new FilmController();
        film.setId(null);
        fc.create(film);

        assertEquals(1,fc.findAll().size());
        assertEquals(1,fc.findAll().stream().toList().get(0).getId());
    }

    @Test
    void createFilm() {
        FilmController fc = new FilmController();
        film.setId(null);
        Film createdFilm = fc.create(film);
        assertEquals(1,createdFilm.getId());
        assertEquals("name film",createdFilm.getName());
        assertEquals("description film",createdFilm.getDescription());
        assertEquals(120,createdFilm.getDuration());
    }

    @Test
    void updateFilm() {
        FilmController fc = new FilmController();
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
    }
}