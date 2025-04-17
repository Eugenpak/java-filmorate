package ru.yandex.practicum.filmorate.storage.review;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.BaseDbStorage;

import java.util.List;
import java.util.Optional;

@Repository
@Qualifier("ReviewDbStorage")
@Slf4j
public class ReviewDbStorage extends BaseDbStorage<Review> implements ReviewStorage {
    private static final String FIND_ALL_QUERY = "SELECT * FROM reviews";
    private static final String INSERT_QUERY = "INSERT INTO reviews (content,is_positive,user_id,film_id) " +
            "VALUES (?, ?, ?, ?)";
    private static final String UPDATE_QUERY = "UPDATE reviews SET content = ?, is_positive = ?, user_id = ?, " +
            "film_id = ? WHERE id = ?";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM reviews WHERE id = ?";
    private static final String FIND_BY_FILM_ID_QUERY = "SELECT * FROM reviews WHERE film_id = ?";
    private static final String DELETE_QUERY = "DELETE FROM reviews WHERE id = ?";

    public ReviewDbStorage(JdbcTemplate jdbc, RowMapper<Review> mapper) {
        super(jdbc, mapper, Review.class);
    }

    @Override
    public List<Review> findAll() {
        log.info("ReviewDbStorage start findAll()");
        List<Review> listReview = findMany(FIND_ALL_QUERY);
        log.info("ReviewDbStorage end findAll()");
        return listReview;
    }

    @Override
    public List<Review> findReviewsByFilmId(long filmId) {
        log.info("ReviewDbStorage start findReviewsDyFilmId(filmId: {})",filmId);
        List<Review> listReview = findMany(FIND_BY_FILM_ID_QUERY,filmId);
        log.info("ReviewDbStorage end findAll()");
        return listReview;
    }

    @Override
    public Review create(Review filmReview) {
        log.info("ReviewDbStorage start create()");
        long id = insert(
                INSERT_QUERY,
                filmReview.getContent(),
                filmReview.getIsPositive(),
                filmReview.getUserId(),
                filmReview.getFilmId()
        );
        log.info("ReviewDbStorage create(id={})", id);
        filmReview.setReviewId(id);
        return filmReview;
    }

    @Override
    public Review update(Review filmReview) {
        log.info("ReviewDbStorage start update()");
        update(
                UPDATE_QUERY,
                filmReview.getContent(),
                filmReview.getIsPositive(),
                filmReview.getUserId(),
                filmReview.getFilmId(),
                filmReview.getReviewId()
        );
        return filmReview;
    }

    @Override
    public Optional<Review> findReviewById(long id) {
        log.info("ReviewDbStorage start findFilmById({}})",id);
        Optional<Review> reviewOpt = findOne(FIND_BY_ID_QUERY, id);
        return reviewOpt;
    }

    @Override
    public boolean delByReviewId(long id) {
        log.info("ReviewDbStorage start delByFilmId({})",id);
        return delete(DELETE_QUERY, id);
    }

    @Override
    public void deleteAll() {
        jdbc.update("DELETE FROM reviews");
    }
}
