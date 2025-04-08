package ru.yandex.practicum.filmorate.storage.like;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.PopularFilm;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class PopularFilmRowMapper  implements RowMapper<PopularFilm> {
    @Override
    public PopularFilm mapRow(ResultSet rs, int rowNum) throws SQLException {
        PopularFilm fromMapRow = new PopularFilm()
                .toBuilder()
                .filmId(rs.getLong("film_id"))
                .countLike(rs.getLong("like_count"))
                .build();

        return fromMapRow;
    }
}
