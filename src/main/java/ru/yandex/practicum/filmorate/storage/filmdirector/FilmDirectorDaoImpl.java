package ru.yandex.practicum.filmorate.storage.filmdirector;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.util.StopWatch;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.FilmDirector;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


@Repository
@Slf4j
public class FilmDirectorDaoImpl implements FilmDirectorDao {
    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<FilmDirector> mapper;

    private static final String INSERT_QUERY = "INSERT INTO film_directors (film_id,director_id) " +
            "VALUES (?, ?)";
    private static final String DELETE_QUERY = "DELETE FROM film_directors WHERE film_id = ?" +
            " AND director_id = ?";
    private static final String DELETE_BY_FILM_QUERY = "DELETE FROM film_directors WHERE film_id = ?";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM film_directors WHERE film_id = ?";

    public FilmDirectorDaoImpl(JdbcTemplate jdbcTemplate, RowMapper<FilmDirector> mapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.mapper = mapper;
    }

    @Override
    public List<FilmDirector> getFilmDirectorByFilmId(List<Long> values) {
        NamedParameterJdbcTemplate template = new NamedParameterJdbcTemplate(jdbcTemplate);
        String sql = "SELECT * FROM FILM_DIRECTORS WHERE FILM_ID IN (:values)";

        MapSqlParameterSource parameters = new MapSqlParameterSource("values", values);
        List<FilmDirector> result = template.query(sql, parameters,new BeanPropertyRowMapper<>(FilmDirector.class));
        log.info("FilmDirectorDaoImpl >------> {})",result);
        return result;
        //--------------------- awa ----------------- awa -------------------------------
    }

    @Override
    public void deleteDirectorByFilm(long filmId) {
        log.debug("FilmDirectorDaoImpl deleteDirectorByFilm({}).", filmId);
        jdbcTemplate.update(DELETE_BY_FILM_QUERY, filmId);
        log.trace("У фильма ID_{} удалён директор.", filmId);
    }

    //@Override
    public void add(long filmId, long directorId) {
        log.debug("FilmDirectorDaoImpl add({}, {}).", filmId, directorId);
        int rowsAdd = jdbcTemplate.update(INSERT_QUERY, filmId, directorId);
        log.trace("Фильму ID_{} добавлен директор ID_{}.", filmId, directorId);
    }

    @Override
    public void addSet(long filmId, Collection<Director> directors) {
        log.debug("FilmDirectorDaoImpl addSet({}, {}).", filmId, directors);
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        //>-----------awa -----
        final List<FilmDirector> filmDirectorList = new ArrayList<>();
        for (Director el: directors) {
            filmDirectorList.add(new FilmDirector(filmId,el.getId()));
        }
        int[] rowsAdd = jdbcTemplate.batchUpdate("INSERT INTO film_directors (film_id,director_id) VALUES (?, ?)",
                new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        ps.setLong(1, filmDirectorList.get(i).getFilmId());
                        ps.setLong(2, filmDirectorList.get(i).getDirectorId());
                    }

                    @Override
                    public int getBatchSize() {
                        return filmDirectorList.size();
                    }
                });
        //-------------------------------------
        stopWatch.stop();
        log.info("FilmDirectorDaoImpl addSet({}, {})-finish.(StopWatch: {} ms| rowsAdd:{})", filmId, directors,stopWatch.getTotalTimeMillis(),rowsAdd);
        log.trace("Фильму ID_{} добавлены директоры {}.", filmId, directors);
    }
}
