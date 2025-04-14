package ru.yandex.practicum.filmorate.storage.like;

import ru.yandex.practicum.filmorate.model.PopularFilm;
import java.util.Collection;
import java.util.List;
import java.util.Set;

public interface LikeDao {
    void add(long filmId, long userId);

    void delete(long filmId, long userId);

    Collection<PopularFilm> findPopularFilmsId(int count);

    void deleteAllPopularFilms();

    Set<Long> getLikedFilmsIdsByUser(long userId);

    Set<Long> getUserIdsByLikedFilm(long filmId);

    List<Long> findPopularFilmsByGenreYear(int count, long genreId, int year);
}
