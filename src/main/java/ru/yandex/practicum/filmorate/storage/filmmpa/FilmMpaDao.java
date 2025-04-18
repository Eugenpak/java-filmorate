package ru.yandex.practicum.filmorate.storage.filmmpa;

import ru.yandex.practicum.filmorate.model.FilmMpa;
import java.util.List;


public interface FilmMpaDao {
    void add(long filmId, long mpaId);

    void delete(long filmId, long mpaId);

    void updateFilmMpa(long filmId, long mpaId);

    List<FilmMpa> getFilmMpaByFilmId(List<Long> values);
}
