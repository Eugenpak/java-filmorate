package ru.yandex.practicum.filmorate.storage.feed;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.UserFeed;
import ru.yandex.practicum.filmorate.storage.BaseDbStorage;

import java.time.LocalDateTime;
import java.util.List;

@Repository
@Slf4j
public class FeedDbStorage extends BaseDbStorage<UserFeed> implements FeedStorage {

    public FeedDbStorage(JdbcTemplate jdbc, RowMapper<UserFeed> mapper) {
        super(jdbc, mapper, UserFeed.class);
    }

    @Override
    public List<UserFeed> getUserFeedById(long id) {
        log.info("Started getUserFeedById");

        String sql = "SELECT * FROM feed WHERE user_id = ?";

        List<UserFeed> result = jdbc.query(sql, mapper, id);
        log.info("Finish getUserFeedById >------> {})", result);
        return result;
    }


    @Override
    public UserFeed addFeed(long userId, String eventType, String operation, long eventId) {
        String sql = "INSERT INTO feed (user_id, event_type, operation, event_id, timestamp) VALUES (?, ?, ?, ?, ?)";
        //Date date = new Date();
        LocalDateTime localDateTime = LocalDateTime.now();

        try {
            jdbc.update(sql, userId, eventType, operation, eventId, localDateTime);
            log.info("Feed added successfully for user ID: {}", userId);
        } catch (Exception e) {
            log.error("Error adding feed for user ID: {}, {}", userId, e.getMessage());
            throw e;
        }

        return UserFeed.builder()
                .userId(userId)
                .eventType(eventType)
                .operation(operation)
                .eventId(eventId)
                .timestamp(localDateTime)
                .build();
    }
}
