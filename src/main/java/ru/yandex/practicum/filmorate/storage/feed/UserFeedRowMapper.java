package ru.yandex.practicum.filmorate.storage.feed;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.UserFeed;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class UserFeedRowMapper implements RowMapper<UserFeed> {
    @Override
    public UserFeed mapRow(ResultSet rs, int rowNum) throws SQLException {
        UserFeed fromMapRow = new UserFeed()
                .toBuilder()
                .id(rs.getLong("id"))
                .userId(rs.getLong("user_id"))
                .eventType(rs.getString("event_type"))
                .operation(rs.getString("operation"))
                .eventId(rs.getLong("event_id"))
                .timestamp(rs.getTimestamp("timestamp"))
                .build();
        return fromMapRow;
    }
}
