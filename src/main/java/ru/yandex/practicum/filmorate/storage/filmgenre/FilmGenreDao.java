package ru.yandex.practicum.filmorate.storage.filmgenre;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Set;

public interface FilmGenreDao {
    void delete(long filmId, long genreId);

    void add(long filmId, long genreId);

    void addSet(long filmId, Set<Genre> genres);

    Set<Genre> findGenresById(long filmId);

    Set<Genre> updateFilmGenres(long filmId, Set<Genre> genres);
}
