package ru.yandex.practicum.filmorate.storage.feed;

import ru.yandex.practicum.filmorate.model.UserFeed;

import java.util.List;

public interface FeedStorage {
    List<UserFeed> getUserFeedById(long id);

    UserFeed addFeed(long userId, String eventType, String operation, long eventId);
}
