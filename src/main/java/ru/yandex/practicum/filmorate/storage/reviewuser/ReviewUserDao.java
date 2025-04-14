package ru.yandex.practicum.filmorate.storage.reviewuser;

import ru.yandex.practicum.filmorate.model.ReviewUser;

import java.util.List;
import java.util.Optional;

public interface ReviewUserDao {
    void add(long reviewId, long userId, boolean isUseful);

    void delete(long reviewId, long userId);

    int getUsefulByReviewId(long reviewId);

    List<ReviewUser> getReviewUserByReviewId(List<Long> reviewId);

    Optional<ReviewUser> getReviewUserEntity(long reviewId, long userId);
}
