package ru.yandex.practicum.filmorate.storage.mpa;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Mpa;
import java.sql.ResultSet;
import java.sql.SQLException;

@Component
@Qualifier("MpaRowMapper")
public class MpaRowMapper implements RowMapper<Mpa> {
    @Override
    public Mpa mapRow(ResultSet rs, int rowNum) throws SQLException {
        Mpa fromMapRow = new Mpa()
                .toBuilder()
                .id(rs.getLong("id"))
                .name(rs.getString("name"))
                .build();

        return fromMapRow;
    }
}