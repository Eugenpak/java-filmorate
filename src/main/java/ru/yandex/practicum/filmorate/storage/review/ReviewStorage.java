package ru.yandex.practicum.filmorate.storage.review;

import ru.yandex.practicum.filmorate.model.Review;
import java.util.List;
import java.util.Optional;

public interface ReviewStorage {
    List<Review> findAll();

    List<Review> findReviewsByFilmId(long filmId);

    Review create(Review filmReview);

    Review update(Review filmReview);

    Optional<Review> findReviewById(long id);

    boolean delByReviewId(long id);

    void deleteAll();
}
