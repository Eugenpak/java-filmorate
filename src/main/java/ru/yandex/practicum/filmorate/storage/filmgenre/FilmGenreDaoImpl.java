package ru.yandex.practicum.filmorate.storage.filmgenre;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;

import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Repository
@Slf4j
public class FilmGenreDaoImpl  implements FilmGenreDao {
    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<Genre> mapper;
    private static final String INSERT_QUERY = "INSERT INTO film_genres (film_id,genre_id) " +
            "VALUES (?, ?)";
    private static final String DELETE_QUERY = "DELETE FROM film_genres WHERE film_id = ?" +
            " AND genre_id = ?";
    private static final String DELETE_BY_FILM_ID_QUERY = "DELETE FROM film_genres WHERE film_id = ?";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM genres WHERE id IN " +
            "(SELECT genre_id FROM film_genres WHERE film_id = ?)";
    private final GenreStorage genreStorage;

    public FilmGenreDaoImpl(JdbcTemplate jdbcTemplate,RowMapper<Genre> mapper,
                            GenreStorage genreStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.mapper = mapper;
        this.genreStorage = genreStorage;
    }

    @Override
    public void add(long filmId, long genreId) {
        log.debug("FilmGenreDaoImpl add({}, {}).", filmId, genreId);
        int rowsAdd = jdbcTemplate.update(INSERT_QUERY , filmId, genreId);
        log.trace("Фильму ID_{} добавлен жанр ID_{}.",filmId, genreId);
    }

    @Override
    public void addSet(long filmId, Set<Genre> genres) {
        log.debug("FilmGenreDaoImpl addSet({}, {}).", filmId, genres);
        for(Genre el : genres) {
            int rowsAdd = jdbcTemplate.update(INSERT_QUERY , filmId, el.getId());
        }
        log.trace("Фильму ID_{} добавлены жанры {}.", filmId, genres);
    }

    @Override
    public Set<Genre> updateFilmGenres(long filmId, Set<Genre> genres) {
        log.debug("FilmGenreDaoImpl updateFilmGenres({}, {}).", filmId, genres);
        genreStorage.findNotValid(genres);
        deleteSetByFilmId(filmId);
        addSet(filmId,genres);
        log.trace("Фильму ID_{} обновлены жанры {}.", filmId, genres);
        return findGenresById(filmId);
    }

    private void deleteSetByFilmId(long filmId) {
        log.debug("FilmGenreDaoImpl deleteSetByFilmId({}).", filmId);
        jdbcTemplate.update(DELETE_BY_FILM_ID_QUERY, filmId);
    }

    @Override
    public void delete(long filmId, long genreId) {
        log.debug("FilmGenreDaoImpl delete({}, {}).", filmId, genreId);
        jdbcTemplate.update(DELETE_QUERY, filmId, genreId);
        log.trace("У фильма ID_{} удалён жанр ID_{}.", filmId, genreId);
    }

    @Override
    public Set<Genre> findGenresById(long filmId) {
        log.debug("FilmGenreDaoImpl findGenresById({}).", filmId);
        List<Genre> list = jdbcTemplate.query(FIND_BY_ID_QUERY,
                mapper, filmId);
        return new HashSet<>(list);
    }
}
