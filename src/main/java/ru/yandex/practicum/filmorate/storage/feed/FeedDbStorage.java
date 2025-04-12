package ru.yandex.practicum.filmorate.storage.feed;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.UserFeed;
import ru.yandex.practicum.filmorate.storage.BaseDbStorage;

import java.util.Collection;
import java.util.List;

@Repository
@Slf4j
public class FeedDbStorage extends BaseDbStorage<UserFeed> implements FeedStorage {
    //public static final String FIND_ALL_QUERY = "SELECT "
    public FeedDbStorage(JdbcTemplate jdbc, RowMapper<UserFeed> mapper) {
        super(jdbc, mapper, UserFeed.class);
    }
    @Override
    public Collection<UserFeed> findAll() {
        return List.of();
    }

    @Override
    public UserFeed add(long userId) {
        return null;
    }
}
