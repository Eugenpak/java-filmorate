package ru.yandex.practicum.filmorate.storage.director;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.BaseDbStorage;

import java.util.*;

@Repository
@Slf4j
public class DirectorDbStorage extends BaseDbStorage<Director> implements DirectorStorage {
    private static final String FIND_ALL_QUERY = "SELECT * FROM directors";
    private static final String INSERT_QUERY = "INSERT INTO directors (name) " +
            "VALUES (?)";
    private static final String UPDATE_QUERY = "UPDATE directors SET name = ?" +
            " WHERE id = ?";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM directors WHERE id = ?";
    private static final String DELETE_QUERY = "DELETE FROM directors WHERE id = ?";
    private static final String FIND_MANY_BY_DIRECTOR_ID_LIST_QUERY = "SELECT * FROM DIRECTORS WHERE ID IN (:values)";

    public DirectorDbStorage(NamedParameterJdbcTemplate npJdbc, DirectorRowMapper mapper) {
        super(npJdbc, mapper, Director.class);
    }

    @Override
    public Collection<Director> findAll() {
        log.info("DirectorDbStorage start findAll()");
        Collection<Director> listDirector = findMany(FIND_ALL_QUERY);
        log.info("FilmDbStorage end findAll()");
        return listDirector;
    }

    @Override
    public Director create(Director director) {
        log.info("DirectorDbStorage start create()");
        long id = insert(
                INSERT_QUERY,
                director.getName()
        );
        director.setId(id);
        return director;
    }

    @Override
    public Director update(Director director) {
        log.info("DirectorDbStorage start update()");
        update(
                UPDATE_QUERY,
                director.getName(),
                director.getId()
        );
        return director;
    }

    @Override
    public Optional<Director> findDirectorById(long id) {
        log.info("DirectorDbStorage start findDirectorOptById({}})",id);
        Optional<Director> directorOpt = findOne(FIND_BY_ID_QUERY, id);
        return directorOpt;
    }

    @Override
    public boolean delDirectorById(long id) {
        log.info("DirectorDbStorage start delDirectorById({})",id);
        return delete(DELETE_QUERY, id);
    }

    @Override
    public List<Long> findNotValid(Collection<Long> dIdL) {
        log.debug("DirectorDbStorage findNotValid({}).",dIdL);
        List<Long> values = dIdL.stream().toList();
        //--------------------- awa ----------------- awa -------------------------------
        MapSqlParameterSource parameters = new MapSqlParameterSource("values", values);
        List<Long> findDirectors = findMany(FIND_MANY_BY_DIRECTOR_ID_LIST_QUERY, parameters)
                .stream()
                .map(Director::getId)
                .toList();

        return findNotValidLong(values,findDirectors);
    }

    private List<Long> findNotValidLong(List<Long> listValue,List<Long> findValue) {
        List<Long> result = new ArrayList<>();
        for (Long el:listValue) {
            boolean flag = findValue.contains(el);
            if (!flag) {
                result.add(el);
            }
        }
        return result;
    }

    @Override
    public Collection<Director> findDirectorByListId(Collection<Long> dIdL) {
        log.info("DirectorDbStorage start findDirectorOptById({}})",dIdL);
        List<Long> values = dIdL.stream().toList();
        //--------------------- awa ----------------- awa -------------------------------
        MapSqlParameterSource parameters = new MapSqlParameterSource("values", values);
        List<Director> result = findMany(FIND_MANY_BY_DIRECTOR_ID_LIST_QUERY, parameters);
        log.info("DirectorDbStorage >------> {})",result);
        //--------------------- awa ----------------- awa -------------------------------
        return result;
    }
}
