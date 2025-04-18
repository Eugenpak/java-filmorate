package ru.yandex.practicum.filmorate.storage.genre;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.*;

public interface GenreStorage {
    Optional<Genre> findGenreById(long id);

    Collection<Genre> findAll();

    void findNotValid(Set<Genre> genres);

    Map<Long,Genre> getGenreById(List<Long> genreId);

    Set<Genre> findGenresByFilmId(long filmId);
}
