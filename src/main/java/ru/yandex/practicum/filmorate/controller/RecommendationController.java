package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.constraints.Positive;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.RecommendationService;

import java.util.List;

@RestController
@RequestMapping("/users")
@Slf4j
@Validated
public class RecommendationController {
    private final RecommendationService recommendationService;

    @Autowired
    public RecommendationController(RecommendationService recommendationService) {
        this.recommendationService = recommendationService;
    }

    @GetMapping(value = "/{id}/recommendations")
    public ResponseEntity<List<Film>> getRecommendations(
            @PathVariable("id") @Positive(message = "Id пользователя должен быть положительным числом") long userId) {
        log.info("Запрос рекомендаций для пользователя с id: {}", userId);
        List<Film> recommendations = recommendationService.getRecommendations(userId);
        log.info("Найдены {} рекомендации для пользователя с id: {}", recommendations.size(), userId);
        return ResponseEntity.ok(recommendations);
    }
}
