package ru.yandex.practicum.filmorate.storage.filmdirector;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.FilmDirector;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class FilmDirectorRowMapper implements RowMapper<FilmDirector> {
    @Override
    public FilmDirector mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new FilmDirector()
                .toBuilder()
                .filmId(rs.getLong("film_id"))
                .directorId(rs.getLong("director_id"))
                .build();
    }
}
