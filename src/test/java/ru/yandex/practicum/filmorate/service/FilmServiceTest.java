package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.sql.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FilmServiceTest {
    @Mock
    private FilmStorage filmStorage;

    @Mock
    private UserService userService;

    @InjectMocks
    private FilmService filmService;

    private Film getTestFilm() {
        return Film.builder().id(1L).name("name фильма").description("описание фильма")
                .releaseDate(new Date(0L)).duration(100).build();
    }

    private User getTestUser() {
        return User.builder().id(1L).email("test@mail.ru").login("login")
                .name("name").birthday(new Date(0)).build();
    }


    @Test
    void findAll() {
        List<Film> expectedFilms = List.of(getTestFilm());
        when(filmStorage.findAll()).thenReturn(expectedFilms);

        List<Film> actualFilms = filmService.findAll().stream().toList();
        verify(filmStorage, times(1)).findAll();
        assertEquals(expectedFilms.size(), actualFilms.size());
        assertSame(expectedFilms.get(0), actualFilms.get(0));
    }

    @Test
    void create() {
        Film expectedFilm = getTestFilm();
        when(filmStorage.create(expectedFilm)).thenReturn(expectedFilm);

        Film actualFilm = filmService.create(expectedFilm);

        verify(filmStorage, times(1)).create(expectedFilm);
        assertEquals(expectedFilm.getName(), actualFilm.getName());
        assertSame(expectedFilm, actualFilm);
    }

    @Test void update() {
        Film expectedFilm = getTestFilm();
        when(filmStorage.update(expectedFilm)).thenReturn(expectedFilm);
        when(filmStorage.findFilmById(1)).thenReturn(expectedFilm);

        Film actualFilm = filmService.update(expectedFilm);

        verify(filmStorage, times(1)).update(expectedFilm);
        assertEquals(expectedFilm.getName(), actualFilm.getName());
        assertSame(expectedFilm, actualFilm);
    }

    @Test void addLike() {
        Film expectedFilm = getTestFilm();
        when(filmStorage.findFilmById(1)).thenReturn(expectedFilm);
        User expectedUser = getTestUser();
        when(userService.findUserById(1)).thenReturn(expectedUser);

        filmService.addLike(1,1);
        verify(filmStorage, times(1)).addLike(1,1);
        verify(filmStorage, times(1)).findFilmById(1);
        verify(userService, times(1)).findUserById(1);
    }

    @Test void deleteLike() {
        Film expectedFilm = getTestFilm();
        when(filmStorage.findFilmById(1)).thenReturn(expectedFilm);
        User expectedUser = getTestUser();
        when(userService.findUserById(1)).thenReturn(expectedUser);

        filmService.deleteLike(1,1);
        verify(filmStorage, times(1)).deleteLike(1,1);
        verify(filmStorage, times(1)).findFilmById(1);
        verify(userService, times(1)).findUserById(1);
    }

    @Test void getPopularFilms() {
        List<Film> expectedFilms = List.of(getTestFilm());
        when(filmStorage.findAll()).thenReturn(expectedFilms);

        List<Film> actualFilms = filmService.getPopularFilms(10).stream().toList();
        verify(filmStorage, times(1)).findAll();
        assertEquals(expectedFilms.size(), actualFilms.size());
        assertSame(expectedFilms.get(0), actualFilms.get(0));
    }

    @Test void findFilmById() {
        Film expectedFilm = getTestFilm();
        when(filmStorage.findFilmById(1)).thenReturn(expectedFilm);

        Film actualFilm = filmService.findFilmById(1);

        verify(filmStorage, times(1)).findFilmById(1);
        assertEquals(expectedFilm.getName(), actualFilm.getName());
        assertSame(expectedFilm, actualFilm);
    }
}