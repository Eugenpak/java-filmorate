package ru.yandex.practicum.filmorate.storage.friend;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.BaseDbStorage;
import java.util.Collection;

@Repository
@Slf4j
public class FriendDbStorage extends BaseDbStorage<User>  implements FriendStorage {
    private static final String FIND_ALL_QUERY = "SELECT * FROM users u " +
            "WHERE u.id in (SELECT f.friend_id FROM friends as f WHERE f.user_id =?)";
    private static final String FIND_COMMON_FRIENDS_ID_OTHERID_QUERY = "SELECT * " +
            "FROM users WHERE id IN (SELECT str1.friend_id FROM " +
            "(SELECT f.friend_id FROM friends as f WHERE f.user_id =?) str1 " +
            "JOIN (SELECT f.friend_id FROM friends as f WHERE f.user_id =?) str2 " +
            "ON str1.friend_id = str2.friend_id)";
    private static final String INSERT_QUERY = "INSERT INTO friends (user_id,friend_id) " +
            "VALUES (?, ?)";
    private static final String DELETE_ALL_QUERY = "DELETE FROM friends";
    private static final String DELETE_QUERY = "DELETE FROM friends WHERE user_id = ? " +
            "AND friend_id = ?";

    public FriendDbStorage(JdbcTemplate jdbc, RowMapper<User> mapper) {
        super(jdbc, mapper, User.class);
    }

    @Override
    public Collection<User> getFriendsAll(long userId) {
        log.debug("FriendDbStorage getFriendsAll(userId:{}).", userId);
        return findMany(FIND_ALL_QUERY, userId);
    }

    @Override
    public Collection<User> getFriendsCommon(long userId,long otherId) {
        log.debug("FriendDbStorage getFriendsCommon(userId:{},otherId:{}).", userId, otherId);
        return findMany(FIND_COMMON_FRIENDS_ID_OTHERID_QUERY, userId, otherId);
    }

    @Override
    public boolean addFriend(long userId,long friendId) {
        log.debug("FriendDbStorage addFriend(userId:{},friendId:{}).", userId, friendId);
        int rowsAdd = jdbc.update(INSERT_QUERY, userId, friendId);
        return rowsAdd > 0;
    }

    @Override
    public boolean removeFromFriends(long userId,long friendId) {
        log.debug("FriendDbStorage removeFromFriends(userId:{},friendId:{}).", userId, friendId);
        int rowsDeleted = jdbc.update(DELETE_QUERY, userId, friendId);
        return rowsDeleted > 0;
    }

    @Override
    public boolean delAllFriends() {
        log.debug("FriendDbStorage delAllFriends().");
        int rowsDeleted = jdbc.update(DELETE_ALL_QUERY);
        return rowsDeleted > 0;
    }
}
