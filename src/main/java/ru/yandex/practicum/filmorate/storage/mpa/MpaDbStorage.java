package ru.yandex.practicum.filmorate.storage.mpa;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.BaseDbStorage;

import java.util.*;

@Repository
@Slf4j
public class MpaDbStorage extends BaseDbStorage<Mpa> implements MpaStorage {
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM mpas WHERE id = ?";
    private static final String FIND_ALL_QUERY = "SELECT * FROM mpas";
    private static final String FIND_BY_FILM_ID_QUERY = "SELECT * FROM mpas WHERE id IN " +
            "(SELECT mpa_id FROM film_mpas WHERE film_id = ?)";

    public MpaDbStorage(JdbcTemplate jdbc,@Qualifier("MpaRowMapper") RowMapper<Mpa> mapper) {
        super(jdbc, mapper, Mpa.class);
    }

    @Override
    public Optional<Mpa> findMpaById(long id) {
        log.debug("MpaDbStorage findMpaById({}).", id);
        return findOne(FIND_BY_ID_QUERY, id);
    }

    @Override
    public Collection<Mpa> findAll() {
        log.debug("MpaDbStorage findAll().");
        return findMany(FIND_ALL_QUERY);
    }

    @Override
    public Map<Long,Mpa> getMpaById(List<Long> mpaId) {
        NamedParameterJdbcTemplate template = new NamedParameterJdbcTemplate(jdbc);
        String sql = "SELECT * FROM MPAS WHERE ID IN (:mpaId)";

        MapSqlParameterSource parameters = new MapSqlParameterSource("mpaId", mpaId);
        List<Mpa> result = template.query(sql, parameters,new BeanPropertyRowMapper<>(Mpa.class));
        log.info("GenreDbStorage >------> {})",result);
        Map<Long,Mpa> mpaMap = new HashMap<>();
        result.forEach(g -> mpaMap.put(g.getId(),g));
        return mpaMap;
        //--------------------- awa ----------------- awa -------------------------------
    }

    @Override
    public List<Mpa> getMpasByFilmId(long filmId) {
        log.debug("MpaDbStorage getMpasByFilmId({}).", filmId);
        List<Mpa> mpaList = jdbc.query(FIND_BY_FILM_ID_QUERY, mapper,filmId);
        log.trace("У фильма ID_{} получен рейтинг.", filmId);
        return mpaList;
    }
}

