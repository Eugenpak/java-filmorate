package ru.yandex.practicum.filmorate.storage.filmgenre;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.FilmGenre;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class FilmGenreRowMapper implements RowMapper<FilmGenre> {
    @Override
    public FilmGenre mapRow(ResultSet rs, int rowNum) throws SQLException {
        FilmGenre fromMapRow = new FilmGenre()
                .toBuilder()
                .filmId(rs.getLong("film_id"))
                .genreId(rs.getLong("genre_id"))
                .build();
        return fromMapRow;
    }
}
