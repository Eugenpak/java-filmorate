package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.like.LikeDao;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class RecommendationService {
    private final LikeDao likeDao;
    private final FilmStorage filmStorage;
    private final FilmService filmService;

    @Autowired
    public RecommendationService(LikeDao likeDao, FilmStorage filmStorage, FilmService filmService) {
        this.likeDao = likeDao;
        this.filmStorage = filmStorage;
        this.filmService = filmService;
    }

    public List<Film> getRecommendations(Long userId) {
        log.info("Начало формирования рекомендаций для пользователя с id: {}", userId);

        // находим список фильмов, которые лайкнул пользователь
        Set<Long> likedFilms = likeDao.getLikedFilmsIdsByUser(userId);
        log.debug("Пользователь {} поставил лайки следующим фильмам: {}", userId, likedFilms);

        if (likedFilms.isEmpty()) {
            log.debug("Пользователь {} не поставил ни одного лайка, рекомендации нет", userId);
            return Collections.emptyList();
        }

        // считаем количество общих лайков между пользователем и другими
        Map<Long, Integer> commonLikesCount = likeDao.getCommonLikesCount(userId);
        log.debug("Количество общих лайков с другими пользователями: {}", commonLikesCount);

        if (commonLikesCount.isEmpty()) {
            log.debug("Не найдено похожих пользователей {}", userId);
            return Collections.emptyList();
        }
        log.debug("Количество общих лайков с другими пользователями: {}", commonLikesCount);

        // находим максимальное пересечение лайков
        int maxCommonLikes = Collections.max(commonLikesCount.values());
        Set<Long> similarUsers = commonLikesCount.entrySet().stream()
                .filter(entry -> entry.getValue() == maxCommonLikes)
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());
        log.debug("Пользователи с максимально общими лайками: {}", similarUsers);

        // находим фильмы, которые оценили похожие пользователи, но не оценил сам пользователь
        Set<Long> recommendedFilmIds = similarUsers.stream()
                .flatMap(similarUserId -> likeDao.getLikedFilmsIdsByUser(similarUserId).stream())
                .filter(filmId -> !likedFilms.contains(filmId))
                .collect(Collectors.toSet());
        log.debug("Рекомендуемые ID фильмов для пользователя {}: {}", userId, recommendedFilmIds);

        List<Film> recommendedFilms = new ArrayList<>();
        for (Long filmId : recommendedFilmIds) {
            filmStorage.findFilmById(filmId).ifPresent(film -> {
                log.debug("Добавлен фильм в рекомендации: {}", film);
                recommendedFilms.add(film);
            });
        }
        log.info("Рекомендации для пользователя {} собраны", userId);
        return new ArrayList<>(filmService.getFieldsFilm(recommendedFilms));
    }
}
