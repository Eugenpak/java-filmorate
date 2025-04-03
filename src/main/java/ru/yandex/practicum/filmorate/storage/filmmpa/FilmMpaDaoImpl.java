package ru.yandex.practicum.filmorate.storage.filmmpa;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.FilmMpa;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.mpa.MpaStorage;

import java.util.List;
import java.util.Optional;

@Repository
@Slf4j
public class FilmMpaDaoImpl implements FilmMpaDao {
    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<Mpa> mapper;
    private final MpaStorage mpaStorage;
    private static final String INSERT_QUERY = "INSERT INTO film_mpas (film_id,mpa_id) " +
            "VALUES (?, ?)";
    private static final String DELETE_QUERY = "DELETE FROM film_mpas WHERE film_id = ?" +
            " AND mpa_id = ?";
    private static final String DELETE_BY_FILM_QUERY = "DELETE FROM film_mpas WHERE film_id = ?";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM mpas WHERE id IN " +
            "(SELECT mpa_id FROM film_mpas WHERE film_id = ?)";

    public FilmMpaDaoImpl(JdbcTemplate jdbcTemplate,@Qualifier("MpaRowMapper") RowMapper<Mpa> mapper,
                          MpaStorage mpaStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.mapper = mapper;
        this.mpaStorage = mpaStorage;
    }

    @Override
    public void add(long filmId, long mpaId) {
        log.debug("FilmMpaDaoImpl add({}, {}).", filmId, mpaId);
        int rowsAdd = jdbcTemplate.update(INSERT_QUERY, filmId, mpaId);
        log.trace("Фильму ID_{} добавлен рейтинг ID_{}.", filmId, mpaId);
    }

    @Override
    public List<FilmMpa> getFilmMpaByFilmId(List<Long> values) {
        NamedParameterJdbcTemplate template = new NamedParameterJdbcTemplate(jdbcTemplate);
        String sql = "SELECT * FROM FILM_MPAS WHERE FILM_ID IN (:values)";
        //List<Long> values = filmIdList;
        MapSqlParameterSource parameters = new MapSqlParameterSource("values", values);
        List<FilmMpa> result = template.query(sql, parameters,new BeanPropertyRowMapper<>(FilmMpa.class));
        log.info("FilmGenreDaoImpl >------> {})",result);
        return result;
        //--------------------- awa ----------------- awa -------------------------------
    }

    @Override
    public void delete(long filmId, long mpaId) {
        log.debug("FilmMpaDaoImpl delete({}, {}).", filmId, mpaId);
        jdbcTemplate.update(DELETE_QUERY, filmId, mpaId);
        log.trace("У фильма ID_{} удалён рейтинг ID_{}.", filmId, mpaId);
    }

    @Override
    public Optional<Mpa> get(long filmId) {
        log.debug("FilmMpaDaoImpl get({}).", filmId);

        List<Mpa> mpaList = jdbcTemplate.query(FIND_BY_ID_QUERY, mapper,filmId);
        Optional<Mpa> el;
        if (mpaList.size() == 1) {
            el = Optional.of(mpaList.get(0));
        } else {
            el = Optional.empty();
        }
        log.trace("У фильма ID_{} получен рейтинг.", filmId);
        return el;
    }

    @Override
    public Mpa updateFilmMpa(long filmId, long mpaId) {
        log.debug("FilmMpaDaoImpl updateFilmMpa(filmId:{}, mpaId{}).", filmId, mpaId);
        final Optional<Mpa> mpaOpt = mpaStorage.findMpaById(mpaId);
        if (mpaOpt.isPresent()) {
            deleteMpaFilm(filmId);
            add(filmId,mpaOpt.get().getId());
            return mpaOpt.get();
        }
        log.trace("Такого рейтинга нет (mpaId=" + mpaId + ")");
        throw new ValidationException("Такого рейтинга нет (mpaId=" + mpaId + ")");
    }

    public void deleteMpaFilm(long filmId) {
        log.debug("FilmMpaDaoImpl deleteMpaFilm({}).", filmId);
        jdbcTemplate.update(DELETE_BY_FILM_QUERY, filmId);
        log.trace("У фильма ID_{} удалён рейтинг.", filmId);
    }
}
