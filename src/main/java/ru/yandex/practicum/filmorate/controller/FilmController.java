package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;

@RestController
@RequestMapping("/films")
@Validated
@Slf4j
public class FilmController {
    private final Map<Long, Film> films = new HashMap<>();
    private static final Date MY_CONSTANT = new GregorianCalendar(1895, Calendar.DECEMBER, 28)
            .getTime();

    @GetMapping
    public Collection<Film> findAll() {
        log.info("Start Film findAll()");
        return films.values();
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        // проверяем выполнение необходимых условий
        log.info("Start Film create()");
        log.info("POST->Body Film = " + film);
        if (film.getName() == null || film.getName().isBlank()) {
            log.error("ValidationException name");
            throw new ValidationException("Название не может быть пустым");
        }
        if (film.getDescription().length() > 200) {
            log.error("ValidationException description");
            throw new ValidationException("Описание должно содержать до 200 символов");
        }

        if (film.getReleaseDate().before(MY_CONSTANT)) {
            log.error("ValidationException releaseDate");
            throw new ValidationException("Дата релиза — не раньше 28 декабря 1895 года.");
        }
        if (film.getDuration() < 0) {
            log.error("ValidationException duration " + film.getDuration());
            throw new ValidationException("Продолжительность фильма должна быть положительным числом.");
        }
        // формируем дополнительные данные
        film.setId(getNextId());

        // сохраняем новую публикацию в памяти приложения
        films.put(film.getId(), film);
        log.info("Новый фильм сохранен (id=" + film.getId() + ", name='" + film.getName() + "')");
        return film;
    }

    private long getNextId() {
        long currentMaxId = films.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film newFilm) {
        // проверяем необходимые условия
        log.info("Start Film update()");
        log.info("PUT->Body Film = " + newFilm);
        if (newFilm.getId() == null) {
            log.error("ValidationException id");
            throw new ValidationException("Id должен быть указан");
        }
        if (newFilm.getName().isBlank()) {
            log.error("ValidationException name");
            throw new ValidationException("Описание должно содержать до 200 символов");
        }
        if (newFilm.getDescription().length() > 200) {
            log.error("ValidationException description");
            throw new ValidationException("Описание должно содержать до 200 символов");
        }
        if (newFilm.getReleaseDate().before(MY_CONSTANT)) {
            log.warn("ValidationException releaseDate");
            throw new ValidationException("Дата релиза — не раньше 28 декабря 1895 года.");
        }
        if (newFilm.getDuration() < 0) {
            log.error("ValidationException duration");
            throw new ValidationException("Продолжительность фильма должна быть положительным числом.");
        }
        if (films.containsKey(newFilm.getId())) {
            Film oldFilm = films.get(newFilm.getId());
            if (newFilm.getName() != null) {
                oldFilm.setName(newFilm.getName());
            }
            if (newFilm.getDescription() != null) {
                oldFilm.setDescription(newFilm.getDescription());
            }
            if (newFilm.getReleaseDate() != null) {
                oldFilm.setReleaseDate(newFilm.getReleaseDate());
            }
            if (newFilm.getDuration() != null) {
                oldFilm.setDuration(newFilm.getDuration());
            }
            // если user найдена и все условия соблюдены, обновляем её содержимое
            log.info("Данные фильма обновлены (id=" + oldFilm.getId() + ", name='" + oldFilm.getName() + "')");
            return oldFilm;
        }
        throw new NotFoundException("Фильм с id = " + newFilm.getId() + " не найден");
    }
}
