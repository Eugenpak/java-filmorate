package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.FilmDirector;
import ru.yandex.practicum.filmorate.model.SortBy;
import ru.yandex.practicum.filmorate.storage.BaseDbStorage;

import java.util.Collection;
import java.util.List;
import java.util.Optional;


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

    private static final String FIND_MANY_BY_FILMID_LIST_QUERY = "SELECT * FROM FILMS WHERE ID IN (:values)";
    private static final String FIND_MANY_FILMID_BY_YEAR_QUERY = "SELECT f.id AS film_id,fd.DIRECTOR_ID AS director_id, EXTRACT(YEAR FROM f.RELEASE_DATE) AS God " +
            "FROM FILMS f LEFT JOIN FILM_DIRECTORS fd ON f.id = fd.FILM_ID " +
            "WHERE fd.DIRECTOR_ID = :values " +
            "GROUP BY f.ID,fd.DIRECTOR_ID ORDER BY god";
    private static final String FIND_MANY_FILMID_BY_POP_QUERY = "SELECT f.id AS film_id,fd.DIRECTOR_ID AS director_id,Count(l.user_id) AS Pop " +
            "FROM FILMS f LEFT JOIN LIKES l ON l.FILM_ID = f.ID " +
            "LEFT JOIN FILM_DIRECTORS fd ON f.id = fd.FILM_ID " +
            "WHERE fd.DIRECTOR_ID = :values " +
            "GROUP BY f.ID,fd.DIRECTOR_ID " +
            "ORDER BY pop DESC";
    private static final String FIND_MANY_BY_USERID_FRIENDID_QUERY = "SELECT f.* FROM FILMS AS f JOIN LIKES AS l ON f.id = l.film_id " +
            "WHERE l.user_id = ? AND l.film_id IN (SELECT film_id FROM LIKES " +
            "WHERE user_id = ?)";
    private static final String FIND_MANY_BY_TITLE_AND_DIRECTOR_QUERY = "SELECT DISTINCT f.* " +
            "FROM FILMS f LEFT JOIN FILM_DIRECTORS fd ON f.ID = fd.FILM_ID " +
            "LEFT JOIN DIRECTORS d ON fd.DIRECTOR_ID = d.ID " +
            "WHERE d.NAME ILIKE concat('%', ?, '%') OR f.NAME ILIKE concat('%', ?, '%')";
    private static final String FIND_MANY_BY_DIRECTOR_QUERY = "SELECT f.* " +
            "FROM FILMS f JOIN FILM_DIRECTORS fd ON f.ID = fd.FILM_ID " +
            "JOIN DIRECTORS d ON fd.DIRECTOR_ID = d.ID " +
            "WHERE d.NAME ILIKE concat('%', ?, '%')";
    private static final String FIND_MANY_BY_TITLE_QUERY = "SELECT * FROM films " +
            "WHERE name ILIKE concat('%', ?, '%')";

    public FilmDbStorage(NamedParameterJdbcTemplate npJdbc, RowMapper<Film> mapper) {
         super(npJdbc, mapper, Film.class);
    }

    @Override
    public Collection<Film> findAll() {
        log.info("FilmDbStorage start findAll()");
        Collection<Film> listFilm = findMany(FIND_ALL_QUERY);
        log.info("FilmDbStorage end findAll()");
        return listFilm;
    }

    @Override
    public Collection<Film> findAllFilmWithLimit(int limit) {
        return findMany("SELECT * FROM films LIMIT " + limit);
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
        log.info("FilmDbStorage start findFilmById({}})", id);
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
        log.info("FilmDbStorage start delByFilmId({})", id);
        return delete(DELETE_QUERY, id);
    }

    @Override
    public List<Film> getFilmsByListFilmId(List<Long> filmId) {
        MapSqlParameterSource parameters = new MapSqlParameterSource("values", filmId);
        List<Film> result = findMany(FIND_MANY_BY_FILMID_LIST_QUERY, parameters);
        log.info("FilmDbStorage >------> {})", result);
        return result;
        //--------------------- awa ----------------- awa -------------------------------
    }

    @Override
    public List<Long> getFilmDirectorSort(long directorId, SortBy sort) {
        log.info("FilmDbStorage start findFilmById(directorId:{}, sortBy: {})", directorId, sort);
        //FIND_MANY_FILMID_BY_YEAR_QUERY - Вывод всех фильмов режиссёра, отсортированных по годам.
        //FIND_MANY_FILMID_BY_POP_QUERY -  Вывод всех фильмов режиссёра, отсортированных по количеству лайков.
        String sql;
        if (sort == SortBy.YEAR) {
            sql = FIND_MANY_FILMID_BY_YEAR_QUERY;
        } else {
            sql = FIND_MANY_FILMID_BY_POP_QUERY;
        }
        MapSqlParameterSource parameters = new MapSqlParameterSource("values", directorId);
        List<FilmDirector> result = npJdbc.query(sql, parameters, new BeanPropertyRowMapper<>(FilmDirector.class));
        return result.stream().map(FilmDirector::getFilmId).toList();
    }

    public List<Film> searchFilmByTitle(String query) {
        return findMany(FIND_MANY_BY_TITLE_QUERY,query);
    }

    public List<Film> searchFilmByDirector(String query) {
        return findMany(FIND_MANY_BY_DIRECTOR_QUERY,query);
    }

    public List<Film> searchFilmByTitleAndDirector(String query) {
        return findMany(FIND_MANY_BY_TITLE_AND_DIRECTOR_QUERY,query,query);
    }

    @Override
    public List<Film> getCommonFilmUserAndHisFriend(long userId, long friendId) {
        log.info("Пришел запрос в FilmStorage получить список общих фильмов");
        try {
            List<Film> result = jdbc.query(FIND_MANY_BY_USERID_FRIENDID_QUERY, mapper, userId, friendId);
            log.info("result {}", result);
            return result;
        } catch (Exception e) {
            throw e;
        }
    }
}
