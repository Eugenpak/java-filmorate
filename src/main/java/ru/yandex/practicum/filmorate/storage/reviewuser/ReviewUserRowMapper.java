package ru.yandex.practicum.filmorate.storage.reviewuser;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.ReviewUser;
import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class ReviewUserRowMapper implements RowMapper<ReviewUser> {
    @Override
    public ReviewUser mapRow(ResultSet rs, int rowNum) throws SQLException {
        ReviewUser fromMapRow = new ReviewUser()
                .toBuilder()
                .reviewId(rs.getLong("review_id"))
                .userId(rs.getLong("user_id"))
                .isUseful(rs.getBoolean("is_useful"))
                .build();
        return fromMapRow;
    }
}
