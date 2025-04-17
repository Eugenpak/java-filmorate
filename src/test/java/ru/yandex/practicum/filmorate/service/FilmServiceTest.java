package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.storage.feed.FeedStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.filmdirector.FilmDirectorDao;
import ru.yandex.practicum.filmorate.storage.filmgenre.FilmGenreDao;
import ru.yandex.practicum.filmorate.storage.filmmpa.FilmMpaDao;
import ru.yandex.practicum.filmorate.storage.like.LikeDao;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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
    private FilmDirectorDao filmDirectorDao;

    @Mock
    private GenreService genreService;
    @Mock
    private MpaService mpaService;
    @Mock
    private DirectorService directorService;

    @Mock
    private LikeDao likeDao;

    @Mock
    private FeedStorage feedStorage;

    @InjectMocks
    private FilmService filmService;

    private Film getTestFilm() {
        return Film.builder().id(1L).name("name фильма").description("описание фильма")
                .releaseDate(LocalDate.of(1970,1,1)).duration(100).build();
    }

    private List<Genre> getTestGenre() {
        return List.of(new Genre(1L,"G"));
    }

    private Mpa getTestMpa() {
        return new Mpa(1L,"Комедия");
    }

    private List<Director> getTestDirector() {
        return List.of(new Director(1L,"Director"));
    }

    private User getTestUser() {
        return User.builder().id(1L).email("test@mail.ru").login("login")
                .name("name").birthday(LocalDate.of(1970,1,1)).build();
    }


    @Test
    void findAll() {
        List<Film> expectedFilms = List.of(getTestFilm());
        List<Genre> expectedGenres = getTestGenre();
        List<Director> expectedDirectors = getTestDirector();
        Mpa expectedMpa = getTestMpa();
        Map<Long, Genre> gl = new HashMap<>();
        gl.put(1L,expectedGenres.get(0));
        Map<Long, Mpa> ml = new HashMap<>();
        ml.put(1L,expectedMpa);
        Map<Long, Director> dl = new HashMap<>();
        dl.put(1L,expectedDirectors.get(0));

        when(filmStorage.findAll()).thenReturn(expectedFilms);
        when(filmGenreDao.getFilmGenreByFilmId(List.of(1L)))
                .thenReturn(List.of(new FilmGenre(1L,1L)));
        when(genreService.getGenreById(List.of(1L))).thenReturn(gl);

        when(filmMpaDao.getFilmMpaByFilmId(List.of(1L)))
                .thenReturn(List.of(new FilmMpa(1L,1L)));
        when(mpaService.getMpaById(List.of(1L))).thenReturn(ml);

        when(filmDirectorDao.getFilmDirectorByFilmId(List.of(1L)))
                .thenReturn(List.of(new FilmDirector(1L,1L)));
        when(directorService.getDirectorByListId(List.of(1L))).thenReturn(dl);

        List<Film> actualFilms = filmService.findAll().stream().toList();
        verify(filmStorage, times(1)).findAll();
        verify(filmGenreDao, times(1)).getFilmGenreByFilmId(List.of(1L));

        verify(genreService, times(1)).getGenreById(List.of(1L));
        verify(filmMpaDao, times(1)).getFilmMpaByFilmId(List.of(1L));
        verify(mpaService, times(1)).getMpaById(List.of(1L));
        verify(filmDirectorDao, times(1)).getFilmDirectorByFilmId(List.of(1L));
        verify(directorService, times(1)).getDirectorByListId(List.of(1L));
        assertEquals(expectedFilms.size(), actualFilms.size());
        assertSame(expectedFilms.get(0), actualFilms.get(0));
    }

    @Test
    void create() {
        Film expectedFilm = getTestFilm();
        expectedFilm.setGenres(new HashSet<>(getTestGenre()));
        expectedFilm.setMpa(getTestMpa());
        expectedFilm.setDirectors(getTestDirector());
        when(filmStorage.create(expectedFilm)).thenReturn(expectedFilm);
        when(mpaService.findMpaById(expectedFilm.getMpa().getId())).thenReturn(getTestMpa());

        Film actualFilm = filmService.create(expectedFilm);

        verify(filmStorage, times(1)).create(expectedFilm);
        assertEquals(expectedFilm.getName(), actualFilm.getName());
        assertSame(expectedFilm, actualFilm);
    }

    @Test
    void update() {
        Film expectedFilm = getTestFilm();
        when(filmStorage.update(expectedFilm)).thenReturn(expectedFilm);
        when(filmStorage.findFilmById(expectedFilm.getId())).thenReturn(Optional.of(expectedFilm));

        Film actualFilm = filmService.update(expectedFilm);

        verify(filmStorage, times(1)).update(expectedFilm);
        verify(filmStorage, times(1)).findFilmById(expectedFilm.getId());
        assertEquals(expectedFilm.getName(), actualFilm.getName());
        assertSame(expectedFilm, actualFilm);
    }

    @Test
    void addLike() {
        Film expectedFilm = getTestFilm();
        when(filmStorage.findFilmById(1)).thenReturn(Optional.of(expectedFilm));
        User expectedUser = getTestUser();
        when(userService.findUserById(expectedUser.getId())).thenReturn(expectedUser);

        filmService.addLike(1,1);
        verify(likeDao, times(1)).add(1L,1L);
        verify(filmStorage, times(1)).findFilmById(1L);
        verify(userService, times(1)).findUserById(1L);

    }

    @Test
    void deleteLike() {
        Film expectedFilm = getTestFilm();
        when(filmStorage.findFilmById(expectedFilm.getId())).thenReturn(Optional.of(expectedFilm));
        User expectedUser = getTestUser();
        when(userService.findUserById(1)).thenReturn(expectedUser);

        filmService.deleteLike(1L,1L);
        verify(likeDao, times(1)).delete(1L,1L);
        verify(filmStorage, times(1)).findFilmById(1L);
        verify(userService, times(1)).findUserById(1L);

    }

    @Test
    void getPopularFilms() {
        int count = 10;
        Film expectedFilms = getTestFilm();
        Collection<PopularFilm> popFilm = List.of(new PopularFilm(expectedFilms.getId(),10L));
        when(likeDao.findPopularFilmsId(count)).thenReturn(popFilm);
        when(filmStorage.findFilmById(1L)).thenReturn(Optional.of(expectedFilms));
        when(filmStorage.findAllFilmWithLimit(count-1)).thenReturn(List.of(getTestFilm()));

        Collection<Film> actualFilms = filmService.getPopularFilms(count);

        verify(likeDao, times(1)).findPopularFilmsId(count);
        verify(filmStorage, times(1)).findFilmById(1L);
        verify(filmStorage, times(1)).findAllFilmWithLimit(count-1);
        assertEquals(expectedFilms, actualFilms.stream().toList().get(0));
    }

    @Test
    void findFilmById() {
        Film expectedFilm = getTestFilm();
        when(filmStorage.findFilmById(expectedFilm.getId())).thenReturn(Optional.of(expectedFilm));

        Film actualFilm = filmService.findFilmById(expectedFilm.getId());

        verify(filmStorage, times(1)).findFilmById(expectedFilm.getId());
        assertEquals(expectedFilm.getName(), actualFilm.getName());
        assertSame(expectedFilm, actualFilm);
    }
}