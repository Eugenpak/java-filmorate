package ru.yandex.practicum.filmorate.storage.filmmpa;

import ru.yandex.practicum.filmorate.model.FilmMpa;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;
import java.util.Optional;

public interface FilmMpaDao {
    void add(long filmId, long mpaId);

    void delete(long filmId, long mpaId);

    Optional<Mpa> get(long filmId);

    Mpa updateFilmMpa(long filmId, long mpaId);

    List<FilmMpa> getFilmMpaByFilmId(List<Long> values);
}
