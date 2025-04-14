package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/reviews")
public class ReviewController {
    private final ReviewService reviewService;
    @Autowired
    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping
    public Review add(@Valid @RequestBody Review filmReview) {
        return reviewService.add(filmReview);
    }

    @PutMapping
    public Review update(@Valid @RequestBody Review filmReview) {
        return reviewService.update(filmReview);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") long id) {
        reviewService.delete(id);
    }

    @GetMapping
    public List<Review> getAll(
            @RequestParam(name = "filmId", defaultValue = "-1", required = false) long filmId,
            @RequestParam(name = "count", defaultValue = "10", required = false) int count
    ) {
        return reviewService.getAll(filmId, count);
    }

    @GetMapping("/{id}")
    public Review getById(@PathVariable("id") long id) {
        return reviewService.getById(id);
    }

    @PutMapping("/{id}/like/{userId}")
    public void addUserLike(@PathVariable("id") long id, @PathVariable("userId") long userId) {
        reviewService.addLikeToFilmReview(id, userId);
    }

    @PutMapping("/{id}/dislike/{userId}")
    public void addUserDislike(@PathVariable("id") long id, @PathVariable("userId") long userId) {
        reviewService.addDislikeToFilmReview(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void deleteUserLike(@PathVariable("id") long id, @PathVariable("userId") long userId) {
        reviewService.deleteLikeFromFilmReview(id, userId);
    }

    @DeleteMapping("/{id}/dislike/{userId}")
    public void deleteUserDislike(@PathVariable("id") long id, @PathVariable("userId") long userId) {
        reviewService.deleteDislikeFromFilmReview(id, userId);
    }
}
