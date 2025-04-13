package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.SortBy;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface FilmStorage {
    Collection<Film> findAll();

    Film create(Film film);

    Film update(Film film);

    Optional<Film> findFilmById(long id);

    boolean deleteAll();

    boolean delByFilmId(long id);

    List<Film> getFilmsByListFilmId(List<Long> filmId);

    List<Long> getFilmDirectorSort(long directorId, SortBy sort);

    List<Film> searchFilmByTitle(String query);

    List<Film> searchFilmByDirector(String query);

    List<Film> searchFilmByTitleAndDirector(String query);
}
