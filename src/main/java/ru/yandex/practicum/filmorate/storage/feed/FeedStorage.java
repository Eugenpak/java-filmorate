package ru.yandex.practicum.filmorate.storage.feed;

import ru.yandex.practicum.filmorate.model.UserFeed;

import java.util.Collection;

public interface FeedStorage {
    Collection<UserFeed> findAll();
    UserFeed add(long userId);
}
