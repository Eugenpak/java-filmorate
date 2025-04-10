package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.storage.BaseDbStorage;
import java.util.*;


@Repository
@Qualifier("FilmDbStorage")
@Slf4j
public class FilmDbStorage extends BaseDbStorage<Film> implements FilmStorage {
    private static final String FIND_ALL_QUERY = "SELECT * FROM films";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM films WHERE id = ?";
    private static final String INSERT_QUERY = "INSERT INTO films (name,description,release_date,duration) " +
            "VALUES (?, ?, ?, ?)";
    private static final String UPDATE_QUERY = "UPDATE films SET name = ?, description = ?, release_date = ?, duration = ?" +
            " WHERE id = ?";
    private static final String DELETE_QUERY = "DELETE FROM films WHERE id = ?";

    private static final String DELETE_ALL_QUERY = "DELETE FROM films";


    public FilmDbStorage(JdbcTemplate jdbc, RowMapper<Film> mapper) {
        super(jdbc, mapper, Film.class);
    }

    @Override
    public Collection<Film> findAll() {
        log.info("FilmDbStorage start findAll()");
        Collection<Film> listFilm = findMany(FIND_ALL_QUERY);
        log.info("FilmDbStorage end findAll()");
        return listFilm;
    }

    @Override
    public Film create(Film film) {
        log.info("FilmDbStorage start create()");
        long id = insert(
                INSERT_QUERY,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration()
        );
        log.info("FilmDbStorage create(id={})", id);
        film.setId(id);
        return film;
    }

    @Override
    public Film update(Film film) {
        log.info("FilmDbStorage start update()");
        update(
                UPDATE_QUERY,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getId()
        );
        return film;
    }

    @Override
    public Optional<Film> findFilmById(long id) {
        log.info("FilmDbStorage start findFilmById({}})",id);
        Optional<Film> filmOpt = findOne(FIND_BY_ID_QUERY, id);
        return filmOpt;
    }

    @Override
    public boolean deleteAll() {
        log.info("FilmDbStorage start deleteAll()");
        int rowsDeleted = jdbc.update(DELETE_ALL_QUERY);
        return rowsDeleted > 0;
    }

    @Override
    public boolean delByFilmId(long id) {
        log.info("FilmDbStorage start delByFilmId({})",id);
        return delete(DELETE_QUERY, id);
    }

    @Override
    public List<Film> getFilmsByListFilmId(List<Long> filmId) {
        NamedParameterJdbcTemplate template = new NamedParameterJdbcTemplate(jdbc);
        String sql = "SELECT * FROM FILMS WHERE ID IN (:values)";

        MapSqlParameterSource parameters = new MapSqlParameterSource("values", filmId);
        List<Film> result = template.query(sql, parameters,new BeanPropertyRowMapper<>(Film.class));
        log.info("FilmDbStorage >------> {})",result);
        return result;
        //--------------------- awa ----------------- awa -------------------------------
    }

    @Override
    public List<Long> getFilmDirectorSort(long directorId, SortBy sort) {
        log.info("FilmDbStorage start findFilmById(directorId:{}, sortBy: {})",directorId, sort);
        NamedParameterJdbcTemplate template = new NamedParameterJdbcTemplate(jdbc);
        // Вывод всех фильмов режиссёра, отсортированных по годам.
        String sql1 = "SELECT f.id AS film_id,fd.DIRECTOR_ID AS director_id, EXTRACT(YEAR FROM f.RELEASE_DATE) AS God " +
        "FROM FILMS f LEFT JOIN FILM_DIRECTORS fd ON f.id = fd.FILM_ID " +
        "WHERE fd.DIRECTOR_ID = :values " +
        "GROUP BY f.ID,fd.DIRECTOR_ID ORDER BY god";

        // Вывод всех фильмов режиссёра, отсортированных по количеству лайков.
        String sql2 = "SELECT f.id AS film_id,fd.DIRECTOR_ID AS director_id,Count(l.user_id) AS Pop " +
        "FROM FILMS f LEFT JOIN LIKES l ON l.FILM_ID = f.ID " +
        "LEFT JOIN FILM_DIRECTORS fd ON f.id = fd.FILM_ID " +
        "WHERE fd.DIRECTOR_ID = :values " +
        "GROUP BY f.ID,fd.DIRECTOR_ID " +
        "ORDER BY pop DESC";

        String sql;
        if (sort == SortBy.YEAR) {
            sql = sql1;
        } else {
            sql = sql2;
        }
        MapSqlParameterSource parameters = new MapSqlParameterSource("values", directorId);
        List<FilmDirector> result = template.query(sql, parameters,new BeanPropertyRowMapper<>(FilmDirector.class));
        return result.stream().map(FilmDirector::getFilmId).toList();
    }
}


