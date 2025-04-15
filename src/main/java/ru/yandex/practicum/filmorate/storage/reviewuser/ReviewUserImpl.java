package ru.yandex.practicum.filmorate.storage.reviewuser;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.ReviewUser;

import java.util.List;
import java.util.Optional;

@Repository
@Slf4j
public class ReviewUserImpl implements ReviewUserDao {
    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<ReviewUser> mapper;
    private static final String INSERT_QUERY = "INSERT INTO review_users (review_id,user_id, is_useful) " +
            "VALUES (?, ?, ?)";
    private static final String DELETE_QUERY = "DELETE FROM review_users WHERE review_id = ? " +
            " AND user_id = ?";
    private static final String FIND_USEFUL_BY_ID_QUERY = "SELECT * FROM review_users WHERE review_id = ?";
    private static final String FIND_ENTITY_BY_REVIEWID_USERID_QUERY = "SELECT * FROM review_users " +
            "WHERE review_id = ? AND user_id = ?";

    public ReviewUserImpl(JdbcTemplate jdbcTemplate,RowMapper<ReviewUser> mapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.mapper = mapper;
    }

    @Override
    public void add(long reviewId, long userId, boolean isUseful) {
        log.debug("LikeDaoImpl add({}, {}).", reviewId, userId);
        int rowsAdd = jdbcTemplate.update(INSERT_QUERY, reviewId, userId, isUseful);
        String str = "лайк";
        if (!isUseful) {
            str = "дизлайк";
        }
        log.trace("Отзыв ID_{} добавлен {} от пользователя ID_{}.", reviewId, str, userId);
    }

    @Override
    public void delete(long reviewId, long userId) {
        log.debug("LikeDaoImpl delete({}, {}).", reviewId, userId);
        jdbcTemplate.update(DELETE_QUERY, reviewId, userId);
        log.trace("У отзыва ID_{} удалён лайк/дизлайк от пользователя ID_{}.", reviewId, userId);
    }

    @Override
    public Optional<ReviewUser> getReviewUserEntity(long reviewId, long userId) {
        log.debug("LikeDaoImpl getReviewUserEntity(reviewId: {}, userId: {}).", reviewId,userId);
        try {
            ReviewUser result = jdbcTemplate.queryForObject(FIND_ENTITY_BY_REVIEWID_USERID_QUERY, mapper, reviewId,userId);
            return Optional.ofNullable(result);
        } catch (EmptyResultDataAccessException ignored) {
            return Optional.empty();
        }
    }

    @Override
    public int getUsefulByReviewId(long reviewId) {
        log.debug("LikeDaoImpl getUsefulByReviewId({}).", reviewId);
        List<ReviewUser> ruList = jdbcTemplate.query(FIND_USEFUL_BY_ID_QUERY, mapper, reviewId);
        int rating = 0;
        if (ruList.size() != 0) {
            rating = ruList.stream().mapToInt(ReviewUser::getUsefulValue).sum();
        }
        log.trace("У отзыва ID_{} рейтинг = {}.", reviewId, rating);
        return rating;
    }

    @Override
    public List<ReviewUser> getReviewUserByReviewId(List<Long> reviewId) {
        NamedParameterJdbcTemplate template = new NamedParameterJdbcTemplate(jdbcTemplate);
        String sql = "SELECT * FROM review_users WHERE REVIEW_ID IN (:values)";

        MapSqlParameterSource parameters = new MapSqlParameterSource("values", reviewId);
        List<ReviewUser> result = template.query(sql, parameters,new BeanPropertyRowMapper<>(ReviewUser.class));
        log.info("LikeDaoImpl >--->> getReviewUserByReviewId(reviewId: {})",reviewId);
        return result;
        //--------------------- awa ----------------- awa -------------------------------
    }
}
