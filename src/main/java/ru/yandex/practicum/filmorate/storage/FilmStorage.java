package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;

public interface FilmStorage {
    Collection<Film> findAll();

    Film create(Film film);

    Film update(Film film);

    public Film findFilmById(long id);

    public void addLike(long filmId, long userId);

    public void deleteLike(long filmId, long userId);
}
