package ru.yandex.practicum.filmorate.storage.like;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.PopularFilm;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Repository
@Slf4j
public class LikeDaoImpl implements LikeDao {
    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<PopularFilm> mapperPop;
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

    public LikeDaoImpl(JdbcTemplate jdbcTemplate,RowMapper<PopularFilm> mapperPop) {
        this.jdbcTemplate = jdbcTemplate;
        this.mapperPop = mapperPop;
    }

    @Override
    public void add(long filmId, long userId) {
        log.debug("LikeDaoImpl add({}, {}).", filmId, userId);
        int rowsAdd = jdbcTemplate.update(INSERT_QUERY, filmId, userId);

        log.trace("Фильму ID_{} добавлен лайк от пользователя ID_{}.", filmId, userId);
    }

    @Override
    public void delete(long filmId, long userId) {
        log.debug("LikeDaoImpl delete({}, {}).", filmId, userId);
        jdbcTemplate.update(DELETE_QUERY, filmId, userId);
        log.trace("У фильма ID_{} удалён лайк от пользователя ID_{}.", filmId, userId);
    }

    @Override
    public Collection<PopularFilm> findPopularFilmsId(int count) {
        log.debug("LikeDaoImpl findPopularFilmsId().");
        return jdbcTemplate.query(FIND_POPULAR_FILM_QUERY, mapperPop, count);
    }

    @Override
    public void deleteAllPopularFilms() {
        log.debug("LikeDaoImpl deleteAllPopularFilms().");
        jdbcTemplate.update(DELETE_ALL_QUERY);
        log.trace("Список популярных фильмов удалён.");
    }

    @Override
    public Set<Long> getLikedFilmsIdsByUser(long userId) {
        log.debug("LikeDaoImpl getLikedFilmIdsByUser для пользователя с id: {}.", userId);
        Set<Long> filmIds = new HashSet<>(jdbcTemplate.queryForList(GET_FILMS_BY_USER_QUERY, Long.class, userId));
        log.trace("Получен список id фильмов, которые лайкнул пользователь {}: {}.", userId, filmIds);
        return filmIds;
    }

    @Override
    public Set<Long> getUserIdsByLikedFilm(long filmId) {
        log.debug("LikeDaoImpl getUserIdsByLikedFilm для фильма с id: {}.", filmId);
        Set<Long> userIds = new HashSet<>(jdbcTemplate.queryForList(GET_USERS_BY_FILM_QUERY, Long.class, filmId));
        log.trace("Получен список id пользователей, поставивших лайк фильму {}: {}.", filmId, userIds);
        return userIds;
    }
}
