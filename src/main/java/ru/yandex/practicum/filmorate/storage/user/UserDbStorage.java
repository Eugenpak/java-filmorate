package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.BaseDbStorage;

import java.util.Collection;
import java.util.Optional;

@Repository
@Qualifier("UserDbStorage")
public class UserDbStorage extends BaseDbStorage<User> implements UserStorage {
    private static final String FIND_ALL_QUERY = "SELECT * FROM users";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM users WHERE id = ?";
    private static final String INSERT_QUERY = "INSERT INTO users (email, login, name, birthday) " +
            "VALUES (?, ?, ?, ?)"; // returning id
    private static final String UPDATE_QUERY = "UPDATE users SET email = ?, login = ?, name = ?, birthday = ?" +
            " WHERE id = ?";
    private static final String DELETE_QUERY = "DELETE FROM users WHERE id = ?";

    public UserDbStorage(JdbcTemplate jdbc, RowMapper<User> mapper) {
        super(jdbc, mapper, User.class);
    }

    @Override
    public Collection<User> findAll() {
        return findMany(FIND_ALL_QUERY);
    }

    @Override
    public User create(User user) {
        long id = insert(
                INSERT_QUERY,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday()
        );
        user.setId(id);
        return user;
    }

    @Override
    public User update(User user) {
        update(
                UPDATE_QUERY,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday(),
                user.getId()
        );
        return user;
    }

    @Override
    public Optional<User> findUserById(long id) {
        return findOne(FIND_BY_ID_QUERY, id);
    }

    @Override
    public boolean delUserById(long id) {
        return delete(DELETE_QUERY, id);
    }

    @Override
    public void addFriend(long userId, long friendId) {}

    @Override
    public void removeFromFriends(long userId, long friendId) {}

    @Override
    public Collection<User> getAllFriends(long userId) {return null;}

    @Override
    public Collection<User> getCommonFriends(long userId,long otherId) {return null;}
}
