package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ReviewValidException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.OperationType;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.ReviewUser;
import ru.yandex.practicum.filmorate.storage.feed.FeedStorage;
import ru.yandex.practicum.filmorate.storage.review.ReviewStorage;
import ru.yandex.practicum.filmorate.storage.reviewuser.ReviewUserDao;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ReviewService {
    private final ReviewStorage reviewStorage;
    private final UserService userService;
    private final FilmService filmService;
    private final ReviewUserDao reviewUserDao;
    private final FeedStorage feedStorage;

    @Autowired
    public ReviewService(@Qualifier("ReviewDbStorage") ReviewStorage reviewStorage,
                         UserService userService,
                         FilmService filmService,
                         ReviewUserDao reviewUserDao,
                         FeedStorage feedStorage
    ) {
        this.reviewStorage = reviewStorage;
        this.userService = userService;
        this.filmService = filmService;
        this.reviewUserDao = reviewUserDao;
        this.feedStorage = feedStorage;
    }

    public List<Review> getAll(long filmId, int count) {
        List<Review> reviews;
        if (filmId == -1) {
            log.debug("Запрошены все отзывы для всех фильмов.");
            reviews = reviewStorage.findAll();
        } else {
            log.debug("Запрошены все отзывы фильма с id = {}", filmId);
            reviews = reviewStorage.findReviewsByFilmId(filmId);
        }
        addBatchRatingByListReview(reviews);
        log.debug("Количество отзывов: {}", reviews.size());
        log.trace("Список отзывов: {}", reviews.stream().map(Review::toString));
        if (reviews.size() > count) {
            reviews = reviews.stream().limit(count).collect(Collectors.toList());
            log.debug("Количество отзывов ограничено {}", count);
            log.trace("Итоговый список отзывов: {}", reviews.stream().map(Review::toString));
        }
        return reviews;
    }

    public Review add(Review filmReview) {
        log.debug("Rev-S add({})", filmReview);
        // проверка полей Review
        checksReview(filmReview);
        Review review = reviewStorage.create(filmReview);
        review.setUseful(reviewUserDao.getUsefulByReviewId(review.getReviewId()));
        log.info("Добавлен отзыв с id = {}", review.getReviewId());

        log.info("Start R-S add(userId:{},reviewId:{})", filmReview.getUserId(), filmReview.getReviewId());
        feedStorage.addFeed(filmReview.getUserId(), "REVIEW", "ADD", filmReview.getReviewId());
        log.info("Finish R-S -> add");
        return review;
    }

    public Review update(Review filmReview) {
        log.debug("Rev-S update({})", filmReview);
        // проверка полей Review
        checkExistsReview(filmReview.getReviewId());
        checksReview(filmReview);
        // Корректировка к тесту
        Review oldReview = reviewStorage.findReviewById(filmReview.getReviewId()).get();
        filmReview.setUserId(oldReview.getUserId());
        filmReview.setFilmId(oldReview.getFilmId());

        Review review = reviewStorage.update(filmReview);
        review.setUseful(reviewUserDao.getUsefulByReviewId(review.getReviewId()));
        log.info("Обновлён отзыв с id = {}", review.getReviewId());

        log.info("Start R-S update(userId:{},reviewId:{})", filmReview.getUserId(), filmReview.getReviewId());
        feedStorage.addFeed(filmReview.getUserId(), "REVIEW", "UPDATE", filmReview.getReviewId());
        log.info("Finish R-S -> update");
        return review;
    }

    public Review getById(long id) {
        log.debug("Rev-S getById(id: {})", id);
        Review review = checkExistsReview(id);
        review.setUseful(reviewUserDao.getUsefulByReviewId(review.getReviewId()));
        log.info("Найден отзыв с id = {}", review.getReviewId());
        return review;
    }

    public void delete(long id) {
        log.debug("Rev-S delete(id: {})", id);
        Review review = checkExistsReview(id);
        reviewStorage.delByReviewId(id);
        log.debug("Удалён отзыв {}", review);

        log.info("Start R-S delete(userId:{},reviewId:{})", review.getUserId(), review.getReviewId());
        feedStorage.addFeed(review.getUserId(), "REVIEW", "REMOVE", review.getReviewId());
        log.info("Finish R-S -> delete");
    }

    public void addLikeToFilmReview(long reviewId, long userId) {
        log.debug("Rev-S addLikeToFilmReview(reviewId: {}, userId: {})", reviewId, userId);
        setReviewUserAction(reviewId, userId, true);
        log.info("Добавлен лайка отзыву с id = {} от пользователя с id = {}", reviewId, userId);
    }

    public void addDislikeToFilmReview(long reviewId, long userId) {
        log.debug("Rev-S addDislikeToFilmReview(reviewId: {}, userId: {})", reviewId, userId);
        setReviewUserAction(reviewId, userId, false);
        log.info("Добавлен дизлайка отзыву с id = {} от пользователя с id = {}", reviewId, userId);
    }

    public void deleteLikeFromFilmReview(long reviewId, long userId) {
        log.debug("Rev-S deleteLikeFromFilmReview(reviewId: {}, userId: {})", reviewId, userId);
        delReviewUserAction(reviewId, userId, true);
        log.info("Удаление лайка к отзыву с id = {} от пользователя с id = {}", reviewId, userId);
    }

    public void deleteDislikeFromFilmReview(long reviewId, long userId) {
        log.debug("Rev-S deleteDislikeFromFilmReview(reviewId: {}, userId: {})", reviewId, userId);
        delReviewUserAction(reviewId, userId, false);
        log.info("Удаление дизлайка к отзыву с id = {} от пользователя с id = {}", reviewId, userId);
    }

    private void checksReview(Review filmReview) {
        log.debug("Rev-Service проверок отзыва сhecksReview()");
        if (filmReview.getContent() == null || filmReview.getContent().isBlank()) {
            log.warn("Ошибка проверки значений текста отзыва");
            throw new ReviewValidException("Текст отзыва заполнен некорректно");
        }
        if (filmReview.getUserId() == null || userService.findUserById(filmReview.getUserId()) == null) {
            log.warn("Не обнаружен пользователь с id = {}", filmReview.getUserId());
            throw new ReviewValidException("Данные о пользователе заполнены некорректно");
        }
        if (filmReview.getFilmId() == null || filmService.findFilmById(filmReview.getFilmId()) == null) {
            log.warn("Не обнаружен фильм с id = {}", filmReview.getFilmId());
            throw new ReviewValidException("Данные о фильме заполнены некорректно");
        }
        if (filmReview.getIsPositive() == null) {
            log.warn("Ошибка проверки значения типа отзыва");
            throw new ReviewValidException("Данные о типе отзыва заполнены некорректно");
        }
        filmReview.setUseful(0);
        log.debug("Проверки пройдены успешно");
    }

    private Review checkExistsReview(long reviewId) {
        Review review = reviewStorage.findReviewById(reviewId)
                .orElseThrow(() -> new NotFoundException("Отзыв с id: " + reviewId + "не обнаружен"));
        return review;
    }

    private void setReviewUserAction(long reviewId, long userId, boolean isUseful) {
        // Проверка существования пользователя с userId
        userService.findUserById(userId);
        checkExistsReview(reviewId);
        // Добавление лайк/дизлайк в review_users. isUseful = true -> лайк. isUseful = false -> дизлайк.
        checkReviewUserEntity(OperationType.ADD, reviewId, userId, isUseful);
        reviewUserDao.add(reviewId, userId, isUseful);
    }

    private void delReviewUserAction(long reviewId, long userId, boolean isUseful) {
        userService.findUserById(userId);
        checkExistsReview(reviewId);
        // Удаление лайк/дизлайк в review_users.
        checkReviewUserEntity(OperationType.DELETE, reviewId, userId, isUseful);
        reviewUserDao.delete(reviewId, userId);
    }

    private List<Review> addBatchRatingByListReview(List<Review> reviews) {
        if (reviews.size() == 0) return reviews;
        List<Long> reviewId = reviews.stream().map(Review::getReviewId).toList();
        List<ReviewUser> ruList = reviewUserDao.getReviewUserByReviewId(reviewId);

        for (Review el : reviews) {
            int rating = ruList.stream().filter(r -> r.getReviewId().equals(el.getReviewId()))
                    .mapToInt(ReviewUser::getUsefulValue).sum();
            el.setUseful(rating);
        }
        return reviews;
    }

    private void checkReviewUserEntity(OperationType operation, long reviewId, long userId, boolean isUseful) {
        Optional<ReviewUser> ruOpt = reviewUserDao.getReviewUserEntity(reviewId, userId);
        // isUseful = true -> лайк. isUseful = false -> дизлайк.

        if (ruOpt.isPresent() & operation == OperationType.ADD) {

            if (isUseful == ruOpt.get().getIsUseful()) {
                String msg = "У отзыва с reviewId=" + reviewId +
                        " уже есть " + getStringByIsUseful(isUseful) + " от пользователя userId=" + userId;
                log.info(msg);
                throw new ValidationException(msg);
            } else {
                reviewUserDao.delete(reviewId, userId);
                log.info("Удаление сущности ReviewUser для инвертации лайк/дизлайк");
            }
        }
        if (ruOpt.isEmpty() & operation == OperationType.DELETE) {
            String msg = "У отзыва с reviewId=" + reviewId +
                    " нет " + getStringByIsUseful(isUseful) + "а от пользователя userId=" + userId;

            log.info(msg);
            throw new ValidationException(msg);
        }
    }


    private String getStringByIsUseful(boolean isUseful) {
        if (isUseful) return "лайк";
        else return "дизлайк";
    }

}
