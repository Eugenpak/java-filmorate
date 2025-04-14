package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;


import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FilmController2MockitoTest {
    @Mock
    private FilmService filmService;

    @InjectMocks
    private FilmController filmController;

    private Film getTestFilm() {
        return Film.builder().id(1L).name("name фильма").description("описание фильма")
                .releaseDate(LocalDate.of(1970,1,1)).duration(100).build();
    }

    @Test
    void findAll() {
        List<Film> expectedFilms = List.of(getTestFilm());
        when(filmService.findAll()).thenReturn(expectedFilms);

        List<Film> actualFilms = filmController.findAll().stream().toList();
        verify(filmService, times(1)).findAll();
        assertEquals(expectedFilms.size(), actualFilms.size());
        assertSame(expectedFilms.get(0), actualFilms.get(0));
    }

    @Test
    void create() {
        Film expectedFilm = getTestFilm();
        when(filmService.create(expectedFilm)).thenReturn(expectedFilm);

        Film actualFilm = filmController.create(expectedFilm);

        verify(filmService, times(1)).create(expectedFilm);
        assertEquals(expectedFilm.getName(), actualFilm.getName());
        assertSame(expectedFilm, actualFilm);
    }

    @Test
    void update() {
        Film expectedFilm = getTestFilm();
        when(filmService.update(expectedFilm)).thenReturn(expectedFilm);

        Film actualFilm = filmController.update(expectedFilm);

        verify(filmService, times(1)).update(expectedFilm);
        assertEquals(expectedFilm.getName(), actualFilm.getName());
        assertSame(expectedFilm, actualFilm);
    }

    @Test
    void addLike() {
        filmController.addLike(1,2);
        verify(filmService, times(1)).addLike(1,2);
    }

    @Test
    void deleteLike() {
        filmController.deleteLike(1,2);
        verify(filmService, times(1)).deleteLike(1,2);
    }

    @Test
    void getPopularFilms() {
        List<Film> expectedFilms = List.of(getTestFilm());
        when(filmService.getPopularFilms(10)).thenReturn(expectedFilms);

        ResponseEntity<List<Film>> response = filmController.getPopularFilms(10, null, null);
        List<Film> actualFilms = response.getBody();
        verify(filmService, times(1)).getPopularFilms(10);
        assertEquals(expectedFilms.size(), actualFilms.size());
        assertSame(expectedFilms.get(0), actualFilms.get(0));
    }

    @Test
    void findFilmById() {
        Film expectedFilm = getTestFilm();
        when(filmService.findFilmById(1)).thenReturn(expectedFilm);

        Film actualFilm = filmController.findFilmById(1);

        verify(filmService, times(1)).findFilmById(1);
        assertEquals(expectedFilm.getName(), actualFilm.getName());
        assertSame(expectedFilm, actualFilm);
    }
}
