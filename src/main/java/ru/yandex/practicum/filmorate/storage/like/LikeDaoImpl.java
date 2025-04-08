package ru.yandex.practicum.filmorate.storage.like;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.PopularFilm;

import java.util.Collection;

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
        return jdbcTemplate.query(FIND_POPULAR_FILM_QUERY, mapperPop,count);
    }

    @Override
    public void deleteAllPopularFilms() {
        log.debug("LikeDaoImpl deleteAllPopularFilms().");
        jdbcTemplate.update(DELETE_ALL_QUERY);
        log.trace("Список популярных фильмов удалён.");
    }
}
