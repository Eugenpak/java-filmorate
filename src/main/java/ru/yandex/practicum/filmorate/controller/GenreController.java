package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.GenreService;
import java.util.Collection;

@RestController
@RequestMapping("/genres")
@Slf4j
public class GenreController {
    private final GenreService genreService;

    @Autowired
    public GenreController(GenreService genreService) {
        this.genreService = genreService;
    }

    @GetMapping
    public Collection<Genre> findAll() {
        log.info("Start Genre findAll()");
        return genreService.findAll();
    }

    @GetMapping(value = "/{id}")
    public Genre findGenreById(@NotNull @PathVariable long id) {
        log.info("GC Genre findGenreById(id=" + id + ")");
        Genre findGenre = genreService.findGenreById(id);
        //System.out.println("GC findGenreById= " + findGenre);
        log.info("GC findGenreById(id=" + id + "):" + findGenre);
        return findGenre;
    }
}
