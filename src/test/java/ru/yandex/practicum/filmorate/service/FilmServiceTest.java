package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.yandex.practicum.filmorate.model.Film;

import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.filmgenre.FilmGenreDao;
import ru.yandex.practicum.filmorate.storage.filmmpa.FilmMpaDao;
import ru.yandex.practicum.filmorate.storage.like.LikeDao;

import java.time.LocalDate;
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FilmServiceTest {
    @Mock
    private FilmStorage filmStorage;

    @Mock
    private UserService userService;

    @Mock
    private FilmGenreDao filmGenreDao;

    @Mock
    private FilmMpaDao filmMpaDao;

    @Mock
    private LikeDao likeDao;

    @InjectMocks
    private FilmService filmService;

    private Film getTestFilm() {
        return Film.builder().id(1L).name("name фильма").description("описание фильма")
                .releaseDate(LocalDate.of(1970,1,1)).duration(100).build();
    }

    private User getTestUser() {
        return User.builder().id(1L).email("test@mail.ru").login("login")
                .name("name").birthday(LocalDate.of(1970,1,1)).build();
    }


    @Test
    void findAll() {/*
        List<Film> expectedFilms = List.of(getTestFilm());
        when(filmStorage.findAll()).thenReturn(expectedFilms);

        List<Film> actualFilms = filmService.findAll().stream().toList();
        verify(filmStorage, times(1)).findAll();
        assertEquals(expectedFilms.size(), actualFilms.size());
        assertSame(expectedFilms.get(0), actualFilms.get(0));
        */
    }

    @Test
    void create() { /*
        Film expectedFilm = getTestFilm();
        when(filmStorage.create(expectedFilm)).thenReturn(expectedFilm);



        Film actualFilm = filmService.create(expectedFilm);

        //when(genreService.findNotValid())
        //when(filmMpaDao.add(expectedFilm)).thenReturn(expectedFilm);
        //when(filmGenreDao.add(expectedFilm)).

        verify(filmStorage, times(1)).create(expectedFilm);
        assertEquals(expectedFilm.getName(), actualFilm.getName());
        assertSame(expectedFilm, actualFilm);
        */
    }

    @Test void update() { /*
        Film expectedFilm = getTestFilm();
        when(filmStorage.update(expectedFilm)).thenReturn(expectedFilm);
        when(filmStorage.findFilmById(expectedFilm.getId())).thenReturn(Optional.of(expectedFilm));

        Film actualFilm = filmService.update(expectedFilm);

        verify(filmStorage, times(1)).update(expectedFilm);
        verify(filmStorage, times(1)).findFilmById(expectedFilm.getId());
        assertEquals(expectedFilm.getName(), actualFilm.getName());
        assertSame(expectedFilm, actualFilm);
        */
    }

    @Test void addLike() { /*
        Film expectedFilm = getTestFilm();
        when(filmStorage.findFilmById(1)).thenReturn(Optional.of(expectedFilm));
        User expectedUser = getTestUser();
        when(userService.findUserById(expectedUser.getId())).thenReturn(expectedUser);

        filmService.addLike(1,1);
        verify(likeDao, times(1)).add(1L,1L);
        verify(filmStorage, times(1)).findFilmById(1L);
        verify(userService, times(1)).findUserById(1L);
        */
    }

    @Test void deleteLike() {/*
        Film expectedFilm = getTestFilm();
        when(filmStorage.findFilmById(expectedFilm.getId())).thenReturn(Optional.of(expectedFilm));
        User expectedUser = getTestUser();
        when(userService.findUserById(1)).thenReturn(expectedUser);

        filmService.deleteLike(1L,1L);
        verify(likeDao, times(1)).delete(1L,1L);
        verify(filmStorage, times(1)).findFilmById(1L);
        verify(userService, times(1)).findUserById(1L);
        */
    }

    @Test void getPopularFilms() {
        /*
        int count = 1000;
        Film expectedFilms = getTestFilm();
        Collection<PopularFilm> popFilm = List.of(new PopularFilm(expectedFilms.getId(),10L));
        when(likeDao.findPopularFilmsId(count)).thenReturn(popFilm);
        when(filmStorage.findFilmById(popFilm.getFilmId()

        List<PopularFilm> actualFilms = filmService.getPopularFilms(count);
        verify(likeDao, times(1)).findPopularFilmsId(count);
        assertEquals(expectedFilms.size(), actualFilms.size());
        assertSame(expectedFilms.get(0), actualFilms.get(0));
        */
    }

    @Test void findFilmById() {
        /*
        Film expectedFilm = getTestFilm();
        when(filmStorage.findFilmById(expectedFilm.getId())).thenReturn(Optional.of(expectedFilm));

        Film actualFilm = filmService.findFilmById(expectedFilm.getId());

        verify(filmStorage, times(1)).findFilmById(expectedFilm.getId());
        assertEquals(expectedFilm.getName(), actualFilm.getName());
        assertSame(expectedFilm, actualFilm);
        */
    }
}