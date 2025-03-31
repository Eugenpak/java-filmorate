package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;

@Component("inMemoryFilmStorage")
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Long, Film> films = new HashMap<>();

    @Override
    public Collection<Film> findAll() {
        return films.values();
    }

    @Override
    public Film create(Film film) {
        // формируем дополнительные данные
        film.setId(getNextId());
        // сохраняем новую публикацию в памяти приложения
        films.put(film.getId(), film);
        return film;
    }

    private long getNextId() {
        long currentMaxId = films.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

    @Override
    public Film update(Film film) {
        return films.put(film.getId(),film);
    }

    @Override
    public Optional<Film> findFilmById(long id) {
        return Optional.ofNullable(films.get(id));
    }

    @Override
    public void addLike(long filmId, long userId) {
        Optional<Film> film = findFilmById(filmId);
        //film.get().getLikes().add(userId);
    }

    @Override
    public void deleteLike(long filmId, long userId) {
        Optional<Film> film = findFilmById(filmId);
       // film.getLikes().remove(userId);
    }

    @Override
    public List<Film> getPopularFilms(int count) {
        return null;
    }

    @Override
    public boolean deleteAll() {
        return false;
    }

    @Override
    public boolean delByFilmId(long id)  {
        return false;
    }
}
