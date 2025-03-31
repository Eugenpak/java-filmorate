package ru.yandex.practicum.filmorate.storage.filmgenre;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmGenreDaoImplTest {
    private final FilmGenreDaoImpl filmGenreDao;
    private final FilmDbStorage filmDbStorage;

    private List<Film> getTestFilm() {
        Film one = Film.builder().name("name фильма А").description("описание фильма А")
                .releaseDate(LocalDate.of(1970,1,1)).duration(100)
                .genres(Set.of(new Genre(1L,""))).mpa(new Mpa(1L,"")).build();
        Film two = Film.builder().name("name фильма В").description("описание фильма В")
                .releaseDate(LocalDate.of(1970,1,1)).duration(100)
                .genres(Set.of(new Genre(1L,""),new Genre(4L,"")))
                .mpa(new Mpa(2L,"")).build();
        Film del = Film.builder().name("name фильма del").description("описание фильма del")
                .releaseDate(LocalDate.of(2000,1,1)).duration(100)
                .genres(Set.of(new Genre(1L,""))).mpa(new Mpa(2L,"")).build();
        Film fin = Film.builder().name("name фильма fin").description("описание фильма fin")
                .releaseDate(LocalDate.of(2010,1,1)).duration(100)
                .genres(Set.of(new Genre(1L,""))).mpa(new Mpa(2L,"")).build();
        return List.of(one,two,del,fin);
    }

    @Test
    void add() {
        filmDbStorage.deleteAll();
        Film film = filmDbStorage.create(getTestFilm().get(0));
        Set<Genre> genres = filmGenreDao.findGenresById(film.getId());
        assertEquals(1, genres.size());
        assertEquals(1L, genres.stream().toList().get(0).getId());
        filmGenreDao.add(film.getId(),5L);
        genres = filmGenreDao.findGenresById(film.getId());
        assertEquals(2, genres.size());
    }

    @Test
    void addSet() {
        filmDbStorage.deleteAll();
        Film film = filmDbStorage.create(getTestFilm().get(1));
        Set<Genre> genres = filmGenreDao.findGenresById(film.getId());
        assertEquals(2, genres.size());
        filmGenreDao.addSet(film.getId(),
                Set.of(new Genre(2L,""),new Genre(5L,"")));
        genres = filmGenreDao.findGenresById(film.getId());
        assertEquals(4, genres.size());
    }

    @Test
    void delete() {
        filmDbStorage.deleteAll();
        Film film = filmDbStorage.create(getTestFilm().get(2));
        Set<Genre> genres = filmGenreDao.findGenresById(film.getId());
        assertEquals(1, genres.size());
        filmGenreDao.delete(film.getId(),genres.stream().toList().get(0).getId());
        genres = filmGenreDao.findGenresById(film.getId());
        assertEquals(0, genres.size());
    }

    @Test
    void findGenresById() {
        filmDbStorage.deleteAll();
        Film film = filmDbStorage.create(getTestFilm().get(3));
        Set<Genre> genres = filmGenreDao.findGenresById(film.getId());
        assertEquals(1, genres.size());
    }
}