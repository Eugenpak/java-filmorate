package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.BaseDbStorage;

import java.util.Collection;
import java.util.Optional;

@Repository
@Qualifier("UserDbStorage")
@Slf4j
public class UserDbStorage extends BaseDbStorage<User> implements UserStorage {
    private static final String FIND_ALL_QUERY = "SELECT * FROM users";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM users WHERE id = ?";
    private static final String INSERT_QUERY = "INSERT INTO users (email, login, name, birthday) " +
            "VALUES (?, ?, ?, ?)"; // returning id
    private static final String UPDATE_QUERY = "UPDATE users SET email = ?, login = ?, name = ?, birthday = ?" +
            " WHERE id = ?";
    private static final String DELETE_QUERY = "DELETE FROM users WHERE id = ?";

    private static final String DELETE_ALL_QUERY = "DELETE FROM users";

    public UserDbStorage(NamedParameterJdbcTemplate npJdbc, RowMapper<User> mapper) {
        super(npJdbc, mapper, User.class);
    }

    @Override
    public Collection<User> findAll() {
        log.debug("UserDbStorage findAll().");
        return findMany(FIND_ALL_QUERY);
    }

    @Override
    public User create(User user) {
        log.debug("UserDbStorage create(user:{}).", user);
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
        log.debug("UserDbStorage update(user:{}).", user);
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
        log.debug("UserDbStorage findUserById(id:{}).", id);
        return findOne(FIND_BY_ID_QUERY, id);
    }

    @Override
    public boolean delUserById(long id) {
        log.debug("UserDbStorage delUserById(id:{}).", id);
        return delete(DELETE_QUERY, id);
    }

    @Override
    public boolean delAllUsers() {
        log.debug("UserDbStorage delAllUsers().");
        int rowsDeleted = jdbc.update(DELETE_ALL_QUERY);
        return rowsDeleted > 0;
    }

}
