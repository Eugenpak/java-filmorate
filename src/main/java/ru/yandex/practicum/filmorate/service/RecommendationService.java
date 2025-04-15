package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.like.LikeDao;

import java.util.*;

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
        Map<Long, Integer> commonLikesCount = new HashMap<>();
        for (Long filmId : likedFilms) {
            Set<Long> usersWhoLikedFilm = likeDao.getUserIdsByLikedFilm(filmId);
            log.debug("Фильм {} лайкнули пользователи: {}", filmId, usersWhoLikedFilm);

            for (Long otherUser : usersWhoLikedFilm) {
                if (!otherUser.equals(userId)) {
                    commonLikesCount.put(otherUser, commonLikesCount.getOrDefault(otherUser, 0) + 1);
                }
            }
        }

        if (commonLikesCount.isEmpty()) {
            log.debug("Не найдено похожих пользователей {}", userId);
            return Collections.emptyList();
        }
        log.debug("Количество общих лайков с другими пользователями: {}", commonLikesCount);

        // находим максимальное пересечение лайков
        int maxCommonLikes = Collections.max(commonLikesCount.values());
        Set<Long> similarUsers = new HashSet<>();
        for (Map.Entry<Long, Integer> entry : commonLikesCount.entrySet()) {
            if (entry.getValue() == maxCommonLikes) {
                similarUsers.add(entry.getKey());
            }
        }
        log.debug("Пользователи с максимально общими лайками: {}", similarUsers);

        // находим фильмы, которые оценили похожие пользователи, но не оценил сам пользователь
        Set<Long> recommendedFilmIds = new HashSet<>();
        for (Long similarUserId : similarUsers) {
            Set<Long> similarUserLikes = likeDao.getLikedFilmsIdsByUser(similarUserId);
            // исключаем фильмы, которые уже оценены самим пользователем
            similarUserLikes.removeAll(likedFilms);
            recommendedFilmIds.addAll(similarUserLikes);
        }
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
