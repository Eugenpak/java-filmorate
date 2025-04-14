package ru.yandex.practicum.filmorate.storage.director;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class DirectorDbStorageTest {
    private final FilmDbStorage filmDbStorage;
    private final DirectorDbStorage directorDbStorage;
    private final FilmService filmService;

    private List<Film> getTestFilm() {
        Film one = Film.builder().name("name фильма А").description("описание фильма А")
                .releaseDate(LocalDate.of(1970,1,1)).duration(100)
                .genres(Set.of(new Genre(1L,""))).mpa(new Mpa(1L,"")).build();
        Film two = Film.builder().name("name фильма В").description("описание фильма В")
                .releaseDate(LocalDate.of(1970,1,1)).duration(100)
                .genres(Set.of(new Genre(1L,""),new Genre(4L,"")))
                .mpa(new Mpa(2L,"")).build();
        return List.of(one,two);
    }

    @Test
    void testSearchByDirector() {
        filmDbStorage.deleteAll();
        assertEquals(0, filmDbStorage.findAll().size());
        List<Film> allFilm = getTestFilm();
        Film firstFilm = allFilm.getFirst();
        Film secondFilm = allFilm.getLast();

        Director director1 = new Director();
        director1.setName("TestDirector1");
        directorDbStorage.create(director1);
        Collection<Director> directorsFirstFilm = new ArrayList<>();
        directorsFirstFilm.add(director1);
        firstFilm.setDirectors(directorsFirstFilm);

        Director director2 = new Director();
        director2.setName("NewDirector1");
        directorDbStorage.create(director2);
        Collection<Director> directorsSecondFilm = new ArrayList<>();
        directorsSecondFilm.add(director2);
        secondFilm.setDirectors(directorsSecondFilm);

        filmService.create(firstFilm);
        filmService.create(secondFilm);
        List<Film> searchFilms = filmDbStorage.searchFilmByDirector("TestDirector1");
        Assertions.assertEquals(firstFilm.getId(), searchFilms.getFirst().getId());
    }

    @Test
    void testSearchByTitleOrDirector() {
        filmDbStorage.deleteAll();
        assertEquals(0, filmDbStorage.findAll().size());
        List<Film> allFilm = getTestFilm();
        Film firstFilm = allFilm.getFirst();
        Film secondFilm = allFilm.getLast();

        Director director1 = new Director();
        director1.setName("режиссер фильма А");
        directorDbStorage.create(director1);
        Collection<Director> directorsFirstFilm = new ArrayList<>();
        directorsFirstFilm.add(director1);
        firstFilm.setDirectors(directorsFirstFilm);

        Director director2 = new Director();
        director2.setName("режиссер фильма Б");
        directorDbStorage.create(director2);
        Collection<Director> directorsSecondFilm = new ArrayList<>();
        directorsSecondFilm.add(director2);
        secondFilm.setDirectors(directorsSecondFilm);

        filmService.create(firstFilm);
        filmService.create(secondFilm);
        List<Film> searchFilms = filmDbStorage.searchFilmByTitleAndDirector("фильма А");
        Assertions.assertEquals(firstFilm.getId(), searchFilms.getFirst().getId());
    }

}
