package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.time.LocalDate;
import java.util.*;

@Service
@Slf4j
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserService userService;
    private static final LocalDate MY_CONSTANT = LocalDate.of(1895,12,28);

    @Autowired
    public FilmService(@Qualifier("FilmDbStorage") FilmStorage filmStorage,
                       UserService userService) {
        this.filmStorage = filmStorage;
        this.userService = userService;
    }

    public Collection<Film> findAll() {
        log.info("Start Film findAll()");
        return filmStorage.findAll();
    }

    public Film create(Film film) {
        // проверяем выполнение необходимых условий
        log.info("Start Film create()");
        log.info("POST->Body Film = " + film);

        if (film.getReleaseDate().isBefore(MY_CONSTANT)) {
            log.error("ValidationException releaseDate");
            throw new ValidationException("Дата релиза — не раньше 28 декабря 1895 года.");
        }
        // сохраняем новую публикацию в памяти приложения
        filmStorage.create(film);
        log.info("Новый фильм сохраняется (id=" + film.getId() + ", name='" + film.getName() + "')");
        return film;
    }

    public Film update(Film newFilm) {
        // проверяем необходимые условия
        log.info("Start Film update()");
        log.info("PUT->Body Film = " + newFilm);
        if (newFilm.getId() == null) {
            log.error("ValidationException id");
            throw new ValidationException("Id должен быть указан");
        }
        if (newFilm.getReleaseDate().isBefore(MY_CONSTANT)) {
            log.warn("ValidationException releaseDate");
            throw new ValidationException("Дата релиза — не раньше 28 декабря 1895 года.");
        }
        Film oldFilm = findFilmById(newFilm.getId());
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
        // -> задача! Для оптимизации
        oldFilm.setGenres(newFilm.getGenres());
        oldFilm.setMpa(newFilm.getMpa());
        // если film найдена и все условия соблюдены, обновляем её содержимое
        log.info("Данные фильма обновляются (id=" + oldFilm.getId() + ", name='" + oldFilm.getName() + "')");
        return filmStorage.update(oldFilm);
    }

    public Film findFilmById(long id) {
        Optional<Film> findFilm = filmStorage.findFilmById(id);
        if (findFilm.isEmpty()) {
            throw new NotFoundException("Фильм с id = " + id + " не найден");
        }
        return findFilm.get();
    }

    public void addLike(long filmId, long userId) {
        log.info("Start FS addLike(filmId={}, userId={})",filmId,userId);
        findFilmById(filmId);
        userService.findUserById(userId);
        try {
            filmStorage.addLike(filmId, userId);
        } catch (Exception ex) {
            String msg = "Пользователь с userId=" + userId +
                    " поставил лайк к фильму с filmId=" + filmId;
            log.info(msg);
            throw new ValidationException(msg);
        }
    }

    public void deleteLike(long filmId, long userId) {
        log.info("Start FS deleteLike(filmId={}, userId={})",filmId,userId);
        findFilmById(filmId);
        userService.findUserById(userId);
        filmStorage.deleteLike(filmId,userId);
        log.info("Пользователь с userId=" + userId +
                " удалил лайк к фильму с filmId=" + filmId);
    }

    public List<Film> getPopularFilms(int count) {
        log.info("Start FS getPopularFilms()");
        return filmStorage.getPopularFilms(count);
    }
}
