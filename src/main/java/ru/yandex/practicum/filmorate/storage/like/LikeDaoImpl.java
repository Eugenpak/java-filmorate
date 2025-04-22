package ru.yandex.practicum.filmorate.storage.like;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.PopularFilm;
import ru.yandex.practicum.filmorate.storage.BaseDbStorage;

import java.util.*;
import java.util.stream.Collectors;

@Repository
@Slf4j
public class LikeDaoImpl extends BaseDbStorage<PopularFilm> implements LikeDao {
    private static final String INSERT_QUERY = "INSERT INTO likes (film_id,user_id) " +
            "VALUES (?, ?)";
    private static final String DELETE_QUERY = "DELETE FROM likes WHERE film_id = ?" +
            " AND user_id = ?";

    private static final String DELETE_ALL_QUERY = "DELETE FROM likes";

    private static final String FIND_POPULAR_FILM_QUERY = "SELECT film_id, " +
            "COUNT(user_id) AS like_count FROM likes GROUP BY film_id " +
            "ORDER BY like_count DESC LIMIT ?";

    private static final String GET_FILMS_BY_USER_QUERY = "SELECT film_id FROM likes WHERE user_id = ?";

    private static final String GET_USERS_BY_FILM_QUERY = "SELECT user_id FROM likes WHERE film_id = ?";

    private static final String FIND_POPULAR_FILMS_BY_GENRE_YEAR_QUERY =
            "SELECT f.id AS film_id, COUNT(l.user_id) AS like_count " +
                    "FROM films f " +
                    "JOIN film_genres fg ON f.id = fg.film_id " +
                    "LEFT JOIN likes l ON f.id = l.film_id " +
                    "WHERE fg.genre_id = ? AND YEAR(f.release_date) = ? " +
                    "GROUP BY f.id " +
                    "ORDER BY like_count DESC " +
                    "LIMIT ?";

    private static final String GET_COMMON_LIKES_SQL =
            "SELECT l2.user_id AS other_user, COUNT(*) AS common_count " +
                    "FROM likes l1 " +
                    "JOIN likes l2 ON l1.film_id = l2.film_id " +
                    "WHERE l1.user_id = ? AND l2.user_id <> ? " +
                    "GROUP BY l2.user_id";

    public LikeDaoImpl(NamedParameterJdbcTemplate npJdbc, RowMapper<PopularFilm> mapperPop) {
        super(npJdbc, mapperPop, PopularFilm.class);
    }

    @Override
    public void add(long filmId, long userId) {
        log.debug("LikeDaoImpl add({}, {}).", filmId, userId);

        update(INSERT_QUERY, filmId, userId);
        log.trace("Фильму ID_{} добавлен лайк от пользователя ID_{}.", filmId, userId);
    }

    @Override
    public void delete(long filmId, long userId) {
        log.debug("LikeDaoImpl delete({}, {}).", filmId, userId);
        jdbc.update(DELETE_QUERY, filmId, userId);
        log.trace("У фильма ID_{} удалён лайк от пользователя ID_{}.", filmId, userId);
    }

    @Override
    public Collection<PopularFilm> findPopularFilmsId(int count) {
        log.debug("LikeDaoImpl findPopularFilmsId().");
        return findMany(FIND_POPULAR_FILM_QUERY, count);
    }

    @Override
    public void deleteAllPopularFilms() {
        log.debug("LikeDaoImpl deleteAllPopularFilms().");
        jdbc.update(DELETE_ALL_QUERY);
        log.trace("Список популярных фильмов удалён.");
    }

    @Override
    public Set<Long> getLikedFilmsIdsByUser(long userId) {
        log.debug("LikeDaoImpl getLikedFilmIdsByUser для пользователя с id: {}.", userId);
        Set<Long> filmIds = new HashSet<>(jdbc.queryForList(GET_FILMS_BY_USER_QUERY, Long.class, userId));
        log.trace("Получен список id фильмов, которые лайкнул пользователь {}: {}.", userId, filmIds);
        return filmIds;
    }

    @Override
    public Set<Long> getUserIdsByLikedFilm(long filmId) {
        log.debug("LikeDaoImpl getUserIdsByLikedFilm для фильма с id: {}.", filmId);
        Set<Long> userIds = new HashSet<>(jdbc.queryForList(GET_USERS_BY_FILM_QUERY, Long.class, filmId));
        log.trace("Получен список id пользователей, поставивших лайк фильму {}: {}.", filmId, userIds);
        return userIds;
    }

    @Override
    public List<Long> findPopularFilmsByGenreYear(int count, long genreId, int year) {
        log.debug("LikeDaoImpl findPopularFilmsByGenreYear: count={}, genreId={}, year={}", count, genreId, year);
        List<PopularFilm> popularFilms = findMany(FIND_POPULAR_FILMS_BY_GENRE_YEAR_QUERY,genreId, year, count);
        return popularFilms.stream()
                .map(PopularFilm::getFilmId)
                .collect(Collectors.toList());
    }

    @Override
    public Map<Long, Integer> getCommonLikesCount(long userId) {
        log.debug("LikeDaoImpl getCommonLikesCount для пользователя {}", userId);
        return jdbc.query(GET_COMMON_LIKES_SQL,
                rs -> {
                    Map<Long, Integer> result = new HashMap<>();
                    while (rs.next()) {
                        long otherUser = rs.getLong("other_user");
                        int commonCount = rs.getInt("common_count");
                        result.put(otherUser, commonCount);
                    }
                    return result;
                },
                userId, userId
        );
    }
}
