package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/films")
@Validated
@Slf4j
public class FilmController {
    private final FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping
    public Collection<Film> findAll() {
        log.info("Start Film findAll()");
        return filmService.findAll();
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        // проверяем выполнение необходимых условий
        log.info("Start Film create()");
        log.info("POST->Body Film = " + film);

        // сохраняем новую публикацию в памяти приложения
        final Film createdFilm = filmService.create(film);
        log.info("Новый фильм сохранен (id=" + createdFilm.getId() + ", name='" + createdFilm.getName() + "')");
        System.out.println("FC-create " + createdFilm);
        return createdFilm;
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film film) {
        // проверяем необходимые условия
        log.info("Start Film update()");
        log.info("PUT->Body Film = " + film);
        return filmService.update(film);
    }

    @PutMapping(value = "/{id}/like/{userId}")
    public void addLike(@NotNull @PathVariable long id, @NotNull @PathVariable long userId) {
        filmService.addLike(id, userId);
        log.info("UC Film addLike(id=" + id + ",userId=" + userId + ")");
    }

    @DeleteMapping(value = "/{id}/like/{userId}")
    public void deleteLike(@NotNull @PathVariable long id, @NotNull @PathVariable long userId) {
        filmService.deleteLike(id, userId);
        log.info("UC Film deleteLike(id=" + id + ",userId=" + userId + ")");
    }

    @GetMapping(value = "/popular")
    public ResponseEntity<List<Film>> getPopularFilms(
            @RequestParam(name = "count", defaultValue = "10") int count,
            @RequestParam(name = "genreId", required = false) Long genreId,
            @RequestParam(name = "year", required = false) Integer year) {
        log.info("Получение популярных фильмов: count={}, genreId={}, year={}", count, genreId, year);
        List<Film> films;
        if (genreId != null && year != null) {
            films = filmService.getPopularFilmsByGenreAndYear(count, genreId, year);
        } else if (genreId != null) {
            films = filmService.getPopularFilmsByGenre(count, genreId);
        } else if (year != null) {
            films = filmService.getPopularFilmsByYear(count, year);
        } else {
            // в случае, если фильтры не указаны, возвращаем общий список популярных фильмов
            films = new ArrayList<>(filmService.getPopularFilms(count));
        }
        return ResponseEntity.ok(films);
    }

    @GetMapping(value = "/{id}")
    public Film findFilmById(@NotNull @PathVariable long id) {
        log.info("UC Film findFilmById(id=" + id + ")");
        return filmService.findFilmById(id);
    }

    @DeleteMapping(value = "/{filmId}")
    public void deleteFilm(@PathVariable Long filmId) {
        log.info("Delete film with ID = {}", filmId);
        filmService.deleteFilm(filmId);
    }

    @GetMapping(value = "/director/{directorId}")
    public List<Film> getDirectorFilms(@NotNull @PathVariable long directorId,
                                       @RequestParam(defaultValue = "like") String sortBy) {
        log.info("UC Film getDirectorFilms(directorId={}, sortBy={})", directorId, sortBy);
        return filmService.getDirectorFilms(directorId, sortBy);
    }

    @GetMapping("/search")
    public Collection<Film> searchFilmOrDirector(@RequestParam(defaultValue = "Empty") String query,
                                                 @RequestParam(defaultValue = "Not argument") String by) {
        return filmService.searchFilmOrDirector(query, by);
    }

    @GetMapping(value = "/common")
    public List<Film> getCommonFilmUserAndHisFriend(@RequestParam long userId,
                                                    @RequestParam long friendId) {
        log.info("Пришел запрос в контролер на  получение списока общих фильмов");
        return filmService.getCommonFilmUserAndHisFriend(userId, friendId);
    }
}
