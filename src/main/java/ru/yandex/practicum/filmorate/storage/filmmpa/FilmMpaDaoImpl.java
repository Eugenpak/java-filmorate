package ru.yandex.practicum.filmorate.storage.filmmpa;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.FilmMpa;
import ru.yandex.practicum.filmorate.storage.BaseDbStorage;

import java.util.List;

@Repository
@Slf4j
public class FilmMpaDaoImpl extends BaseDbStorage<FilmMpa> implements FilmMpaDao {
    private static final String INSERT_QUERY = "INSERT INTO film_mpas (film_id,mpa_id) " +
            "VALUES (?, ?)";
    private static final String DELETE_QUERY = "DELETE FROM film_mpas WHERE film_id = ?" +
            " AND mpa_id = ?";
    private static final String DELETE_BY_FILM_QUERY = "DELETE FROM film_mpas WHERE film_id = ?";

    public FilmMpaDaoImpl(NamedParameterJdbcTemplate npJdbc,RowMapper<FilmMpa> mapper) {
        super(npJdbc, mapper, FilmMpa.class);
    }

    @Override
    public void add(long filmId, long mpaId) {
        log.debug("FilmMpaDaoImpl add({}, {}).", filmId, mpaId);
        int rowsAdd = jdbc.update(INSERT_QUERY, filmId, mpaId);
        log.trace("Фильму ID_{} добавлен рейтинг ID_{}.", filmId, mpaId);
    }

    @Override
    public List<FilmMpa> getFilmMpaByFilmId(List<Long> values) {
        NamedParameterJdbcTemplate template = new NamedParameterJdbcTemplate(jdbc);
        String sql = "SELECT * FROM FILM_MPAS WHERE FILM_ID IN (:values)";

        MapSqlParameterSource parameters = new MapSqlParameterSource("values", values);
        List<FilmMpa> result = template.query(sql, parameters,new BeanPropertyRowMapper<>(FilmMpa.class));
        log.info("FilmGenreDaoImpl >------> {})",result);
        return result;
        //--------------------- awa ----------------- awa -------------------------------
    }

    @Override
    public void delete(long filmId, long mpaId) {
        log.debug("FilmMpaDaoImpl delete({}, {}).", filmId, mpaId);
        jdbc.update(DELETE_QUERY, filmId, mpaId);
        log.trace("У фильма ID_{} удалён рейтинг ID_{}.", filmId, mpaId);
    }

    @Override
    public void updateFilmMpa(long filmId, long mpaId) {
        log.debug("FilmMpaDaoImpl updateFilmMpa(filmId:{}, mpaId{}) begin.", filmId, mpaId);
        deleteMpaFilm(filmId);
        add(filmId,mpaId);
        log.info("FilmMpaDaoImpl updateFilmMpa(filmId:{}, mpaId{}) выполнен.", filmId, mpaId);
    }

    public void deleteMpaFilm(long filmId) {
        log.debug("FilmMpaDaoImpl deleteMpaFilm({}).", filmId);
        jdbc.update(DELETE_BY_FILM_QUERY, filmId);
        log.trace("У фильма ID_{} удалён рейтинг.", filmId);
    }
}
