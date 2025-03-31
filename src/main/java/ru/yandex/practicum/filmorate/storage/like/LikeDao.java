package ru.yandex.practicum.filmorate.storage.like;

import ru.yandex.practicum.filmorate.model.PopularFilm;
import java.util.Collection;

public interface LikeDao {
    void add(long filmId, long userId);

    void delete(long filmId, long userId);

    Collection<PopularFilm> findPopularFilmsId(int count);

    void deleteAllPopularFilms();
}
