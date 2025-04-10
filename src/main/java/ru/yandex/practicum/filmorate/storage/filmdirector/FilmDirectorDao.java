package ru.yandex.practicum.filmorate.storage.filmdirector;

import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.FilmDirector;

import java.util.Collection;
import java.util.List;

public interface FilmDirectorDao {
    List<FilmDirector> getFilmDirectorByFilmId(List<Long> values);

    void deleteDirectorByFilm(long filmId);

    void addSet(long filmId, Collection<Director> directors);
}
