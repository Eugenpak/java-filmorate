package ru.yandex.practicum.filmorate.storage.genre;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;

public interface GenreStorage {
    Optional<Genre> findGenreById(long id);

    Collection<Genre> findAll();

    void findNotValid(Set<Genre> genres);
}
