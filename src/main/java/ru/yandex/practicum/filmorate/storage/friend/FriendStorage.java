package ru.yandex.practicum.filmorate.storage.friend;

import ru.yandex.practicum.filmorate.model.User;
import java.util.Collection;

public interface FriendStorage {
    Collection<User> getFriendsAll(long userId);

    Collection<User> getFriendsCommon(long userId,long otherId);

    boolean removeFromFriends(long userId,long friendId);

    boolean addFriend(long userId,long friendId);

    boolean delAllFriends();
}


