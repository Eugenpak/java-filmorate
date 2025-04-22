package ru.yandex.practicum.filmorate.storage.filmmpa;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.FilmMpa;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class FilmMpaRowMapper implements RowMapper<FilmMpa> {
    @Override
    public FilmMpa mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new FilmMpa()
                .toBuilder()
                .filmId(rs.getLong("film_id"))
                .mpaId(rs.getLong("mpa_id"))
                .build();
    }
}