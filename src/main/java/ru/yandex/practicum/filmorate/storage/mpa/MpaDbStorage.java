package ru.yandex.practicum.filmorate.storage.mpa;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.BaseDbStorage;

import java.util.Collection;
import java.util.Optional;

@Repository
@Slf4j
public class MpaDbStorage extends BaseDbStorage<Mpa> implements MpaStorage {
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM mpas WHERE id = ?";
    private static final String FIND_ALL_QUERY = "SELECT * FROM mpas";

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
}

