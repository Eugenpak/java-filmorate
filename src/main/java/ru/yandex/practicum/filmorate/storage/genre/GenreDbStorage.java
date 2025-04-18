package ru.yandex.practicum.filmorate.storage.genre;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.BaseDbStorage;

import java.util.*;

@Repository
@Slf4j
public class GenreDbStorage extends BaseDbStorage<Genre> implements GenreStorage {
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM genres WHERE id = ?";
    private static final String FIND_ALL_QUERY = "SELECT * FROM genres";


    public GenreDbStorage(JdbcTemplate jdbc, RowMapper<Genre> mapper) {
        super(jdbc, mapper, Genre.class);
    }

    @Override
    public Optional<Genre> findGenreById(long id) {
        log.debug("GenreDbStorage findGenreById(id:{}).", id);
        return findOne(FIND_BY_ID_QUERY, id);
    }

    @Override
    public Collection<Genre> findAll() {
        log.debug("GenreDbStorage findAll().");
        return findMany(FIND_ALL_QUERY);
    }

    @Override
    public void findNotValid(Set<Genre> genres) {
        log.debug("GenreDbStorage findNotValid({}).",genres);
        // bug-10
        Map<Long,Genre> mg = getGenreById(genres.stream().map(Genre::getId).toList());
        for (Genre el : genres) {
            if (!mg.containsKey(el.getId())) {
                log.debug("GenreDbStorage findNotValid(). Жанр id={} не найден. genres: {}",el.getId(),genres);
                throw new NotFoundException("Жанр genres: {id:" + el.getId() + "} не найден");
            }
        }
    }

    @Override
    public Map<Long,Genre> getGenreById(List<Long> genreId) {
        NamedParameterJdbcTemplate template = new NamedParameterJdbcTemplate(jdbc);
        String sql = "SELECT * FROM GENRES WHERE ID IN (:genreId)";

        MapSqlParameterSource parameters = new MapSqlParameterSource("genreId", genreId);
        List<Genre> result = template.query(sql, parameters,new BeanPropertyRowMapper<>(Genre.class));
        log.info("GenreDbStorage >------> {})",result);
        Map<Long,Genre> genreMap = new HashMap<>();
        result.forEach(g -> genreMap.put(g.getId(),g));
        return genreMap;
        //--------------------- awa ----------------- awa -------------------------------
    }
}
