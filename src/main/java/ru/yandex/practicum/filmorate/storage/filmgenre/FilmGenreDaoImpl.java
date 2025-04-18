package ru.yandex.practicum.filmorate.storage.filmgenre;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.*;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.FilmGenre;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.BaseDbStorage;

import java.sql.*;
import java.util.*;

@Repository
@Slf4j
public class FilmGenreDaoImpl  extends BaseDbStorage<FilmGenre> implements FilmGenreDao {
    private static final String INSERT_QUERY = "INSERT INTO film_genres (film_id,genre_id) " +
            "VALUES (?, ?)";
    private static final String DELETE_QUERY = "DELETE FROM film_genres WHERE film_id = ?" +
            " AND genre_id = ?";
    private static final String DELETE_BY_FILM_ID_QUERY = "DELETE FROM film_genres WHERE film_id = ?";

    private static final String FIND_BY_FILM_ID_QUERY = "SELECT * FROM film_genres WHERE film_id IN (?)";

    public FilmGenreDaoImpl(NamedParameterJdbcTemplate npJdbc, RowMapper<FilmGenre> mapper) {
        super(npJdbc, mapper, FilmGenre.class);
    }

    @Override
    public void add(long filmId, long genreId) {
        log.debug("FilmGenreDaoImpl add({}, {}).",filmId, genreId);
        int rowsAdd = jdbc.update(INSERT_QUERY, filmId, genreId);
        log.trace("Фильму ID_{} добавлен жанр ID_{}.", filmId, genreId);
    }

    @Override
    public void addSet(long filmId, Set<Genre> genres) {
        log.debug("FilmGenreDaoImpl addSet({}, {}).", filmId, genres);
        //>-----------awa -----
        final List<FilmGenre> filmGengeList = new ArrayList<>();
        for (Genre el: genres) {
            filmGengeList.add(new FilmGenre(filmId,el.getId()));
        }
        int[] rowsAdd = jdbc.batchUpdate("INSERT INTO film_genres (film_id,genre_id) VALUES (?, ?)",
                new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setLong(1, filmGengeList.get(i).getFilmId());
                ps.setLong(2, filmGengeList.get(i).getGenreId());
            }

            @Override
            public int getBatchSize() {
                return filmGengeList.size();
            }
        });
        //-------------------------------------
        log.info("FilmGenreDaoImpl addSet({}, {})-finish.(rowsAdd:{})", filmId, genres,rowsAdd);
        log.trace("Фильму ID_{} добавлены жанры {}.", filmId, genres);
    }

    @Override
    public List<FilmGenre> getFilmGenreByFilmId(List<Long> values) {
        NamedParameterJdbcTemplate template = new NamedParameterJdbcTemplate(jdbc);
        String sql = "SELECT * FROM FILM_GENRES WHERE FILM_ID IN (:values)";

        MapSqlParameterSource parameters = new MapSqlParameterSource("values", values);
        List<FilmGenre> result = template.query(sql, parameters,new BeanPropertyRowMapper<>(FilmGenre.class));
        log.info("FilmGenreDaoImpl >------> {})",result);
        return result;
        //--------------------- awa ----------------- awa -------------------------------
    }

    @Override
    public void updateFilmGenres(long filmId, Set<Genre> genres) {
        log.debug("FilmGenreDaoImpl updateFilmGenres({}, {}).", filmId, genres);
        // bug-7 (r2049402162)
        deleteSetByFilmId(filmId);
        log.debug("FilmGenreDaoImpl - updateFilmGenres() -> Подоперация deleteSetByFilmId(): выполнена.");
        addSet(filmId,genres);
        log.debug("FilmGenreDaoImpl - updateFilmGenres() ->> Подоперация addSet(): выполнена.");
        log.trace("Фильму ID_{} обновлены жанры {}.", filmId, genres);
    }

    private void deleteSetByFilmId(long filmId) {
        log.debug("FilmGenreDaoImpl deleteSetByFilmId({}).", filmId);
        jdbc.update(DELETE_BY_FILM_ID_QUERY, filmId);
    }

    @Override
    public void delete(long filmId, long genreId) {
        log.debug("FilmGenreDaoImpl delete({}, {}).", filmId, genreId);
        jdbc.update(DELETE_QUERY, filmId, genreId);
        log.trace("У фильма ID_{} удалён жанр ID_{}.", filmId, genreId);
    }
}
