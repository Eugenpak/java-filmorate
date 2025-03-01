package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import java.util.*;

@Service
@Slf4j
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserService userService;
    private static final Date MY_CONSTANT = new GregorianCalendar(1895, Calendar.DECEMBER, 28)
            .getTime();

    @Autowired
    public FilmService(FilmStorage filmStorage,UserService userService) {
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
        if (film.getReleaseDate().before(MY_CONSTANT)) {
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
        if (newFilm.getReleaseDate().before(MY_CONSTANT)) {
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
        // если film найдена и все условия соблюдены, обновляем её содержимое
        log.info("Данные фильма обновляются (id=" + oldFilm.getId() + ", name='" + oldFilm.getName() + "')");
        return filmStorage.update(oldFilm);
    }

    public Film findFilmById(long id) {
        Film findFilm = filmStorage.findFilmById(id);
        if (findFilm == null) {
            throw new NotFoundException("Фильм с id = " + id + " не найден");
        }
        return findFilm;
    }

    public void addLike(long filmId, long userId) {
        findFilmById(filmId);
        userService.findUserById(userId);
        filmStorage.addLike(filmId,userId);
    }

    public void deleteLike(long filmId, long userId) {
        findFilmById(filmId);
        userService.findUserById(userId);
        filmStorage.deleteLike(filmId,userId);
    }

    public List<Film> getPopularFilms(int count) {
        return filmStorage.findAll().stream()
                .sorted((f1, f2) -> f2.getLikes().size() - f1.getLikes().size())
                .limit(count)
                .toList();
    }
}
