package ru.yandex.practicum.filmorate.storage.reviewuser;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.ReviewUser;
import ru.yandex.practicum.filmorate.storage.BaseDbStorage;

import java.util.List;
import java.util.Optional;

@Repository
@Slf4j
public class ReviewUserDaoImpl extends BaseDbStorage<ReviewUser> implements ReviewUserDao {
    private static final String INSERT_QUERY = "INSERT INTO review_users (review_id,user_id, is_useful) " +
            "VALUES (?, ?, ?)";
    private static final String DELETE_QUERY = "DELETE FROM review_users WHERE review_id = ? " +
            " AND user_id = ?";
    private static final String FIND_USEFUL_BY_ID_QUERY = "SELECT * FROM review_users WHERE review_id = ?";
    private static final String FIND_ENTITY_BY_REVIEWID_USERID_QUERY = "SELECT * FROM review_users " +
            "WHERE review_id = ? AND user_id = ?";

    public ReviewUserDaoImpl(NamedParameterJdbcTemplate npJdbc, RowMapper<ReviewUser> mapper) {
        super(npJdbc, mapper, ReviewUser.class);
    }

    @Override
    public void add(long reviewId, long userId, boolean isUseful) {
        log.debug("LikeDaoImpl add({}, {}).", reviewId, userId);
        update(INSERT_QUERY, reviewId, userId, isUseful);
        String str = "лайк";
        if (!isUseful) {
            str = "дизлайк";
        }
        log.trace("Отзыв ID_{} добавлен {} от пользователя ID_{}.", reviewId, str, userId);
    }

    @Override
    public void delete(long reviewId, long userId) {
        log.debug("LikeDaoImpl delete({}, {}).", reviewId, userId);
        update(DELETE_QUERY, reviewId, userId);
        log.trace("У отзыва ID_{} удалён лайк/дизлайк от пользователя ID_{}.", reviewId, userId);
    }

    @Override
    public Optional<ReviewUser> getReviewUserEntity(long reviewId, long userId) {
        log.debug("LikeDaoImpl getReviewUserEntity(reviewId: {}, userId: {}).", reviewId,userId);
        return findOne(FIND_ENTITY_BY_REVIEWID_USERID_QUERY, reviewId,userId);
    }

    @Override
    public int getUsefulByReviewId(long reviewId) {
        log.debug("LikeDaoImpl getUsefulByReviewId({}).", reviewId);
        List<ReviewUser> ruList = findMany(FIND_USEFUL_BY_ID_QUERY, reviewId);
        int rating = 0;
        if (ruList.size() != 0) {
            rating = ruList.stream().mapToInt(ReviewUser::getUsefulValue).sum();
        }
        log.trace("У отзыва ID_{} рейтинг = {}.", reviewId, rating);
        return rating;
    }

    @Override
    public List<ReviewUser> getReviewUserByReviewId(List<Long> reviewId) {
        NamedParameterJdbcTemplate template = new NamedParameterJdbcTemplate(jdbc);
        String sql = "SELECT * FROM review_users WHERE REVIEW_ID IN (:values)";

        MapSqlParameterSource parameters = new MapSqlParameterSource("values", reviewId);
        List<ReviewUser> result = template.query(sql, parameters,new BeanPropertyRowMapper<>(ReviewUser.class));
        log.info("LikeDaoImpl >--->> getReviewUserByReviewId(reviewId: {})",reviewId);
        return result;
        //--------------------- awa ----------------- awa -------------------------------
    }
}
