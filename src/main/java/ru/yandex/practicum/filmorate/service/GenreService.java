package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;

import java.util.Collection;
import java.util.Optional;

@Service
@Slf4j
public class GenreService {
    private final GenreStorage genreStorage;

    @Autowired
    public GenreService(GenreStorage genreStorage) {
        this.genreStorage = genreStorage;
    }

    public Collection<Genre> findAll() {
        log.info("Start Genre findAll()");
        return genreStorage.findAll();
    }

    public Genre findGenreById(long id) {
        log.info("Start Genre findGenreById({})",id);
        Optional<Genre> findGenre = genreStorage.findGenreById(id);
        if (findGenre.isEmpty()) {
            throw new NotFoundException("Жанр с id = " + id + " не найден");
        }
        log.info("Получен Genre findGenreById({})",id);
        return findGenre.get();
    }

}
