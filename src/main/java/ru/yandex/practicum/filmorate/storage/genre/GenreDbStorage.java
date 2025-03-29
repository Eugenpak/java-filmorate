package ru.yandex.practicum.filmorate.storage.genre;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.BaseDbStorage;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;

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
        for (Genre el : genres) {
            if (findGenreById(el.getId()).isEmpty()) {
                log.debug("GenreDbStorage findNotValid({}). Жанр genres: не найден",genres);
                throw new NotFoundException("Жанр genres: {id:" + el.getId() + "} не найден");
            }
        }

    }
}
