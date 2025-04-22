package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmDbStorageTest {
    private final FilmDbStorage filmDbStorage;

    private List<User> getTestUser() {
        User user1 = User.builder().email("test-one@mail.ru").login("login-A")
                .name("name-1").birthday(LocalDate.of(1980,3,5)).build();
        User user2 = User.builder().email("test-two@mail.ru").login("login-B")
                .name("name-2").birthday(LocalDate.of(1970,7,17)).build();
        return List.of(user1,user2);
    }

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
    void findAll() {
        filmDbStorage.deleteAll();
        assertEquals(0, filmDbStorage.findAll().size());
        Film expectedFilm = getTestFilm().get(0);
        Film actualFilm = filmDbStorage.create(expectedFilm);
        assertEquals(1, filmDbStorage.findAll().size());
    }

    @Test
    void create() {
        filmDbStorage.deleteAll();
        assertEquals(0, filmDbStorage.findAll().size());
        Film expectedFilm = getTestFilm().get(0);
        Film actualFilm = filmDbStorage.create(expectedFilm);
        assertEquals(1, filmDbStorage.findAll().size());
        assertThat(actualFilm).hasFieldOrPropertyWithValue("name", "name фильма А");
    }

    @Test
    void update() {
        filmDbStorage.deleteAll();
        assertEquals(0, filmDbStorage.findAll().size());
        Film expectedFilm = filmDbStorage.create(getTestFilm().get(0));
        String expectedName = expectedFilm.getName();
        assertEquals(1, filmDbStorage.findAll().size());
        assertEquals("name фильма А", expectedFilm.getName());
        assertEquals(100, expectedFilm.getDuration());
        expectedFilm.setName("Test update name in filmDbStorege");
        Film actualFilm = filmDbStorage.update(expectedFilm);
        assertEquals(1, filmDbStorage.findAll().size());
        assertEquals("Test update name in filmDbStorege", actualFilm.getName());
    }

    @Test
    void findFilmById() {
        filmDbStorage.deleteAll();
        assertEquals(0, filmDbStorage.findAll().size());
        Film expectedFilm = filmDbStorage.create(getTestFilm().get(0));
        Optional<Film> actualFilm = filmDbStorage.findFilmById(expectedFilm.getId());

        assertThat(actualFilm).isPresent()
                .hasValueSatisfying(film ->
                        assertThat(film).hasFieldOrPropertyWithValue("description", "описание фильма А")
                );
    }

    @Test
    void testSearchByTitle() {
        filmDbStorage.deleteAll();
        assertEquals(0, filmDbStorage.findAll().size());
        List<Film> allFilm = getTestFilm();
        Film firstFilm = allFilm.get(0);
        Film secondFilm = allFilm.get(1);
        filmDbStorage.create(firstFilm);
        filmDbStorage.create(secondFilm);
        List<Film> searchFilms = filmDbStorage.searchFilmByTitle("фильма В");
        Assertions.assertEquals(secondFilm.getId(), searchFilms.get(0).getId());
    }

    @Test
    void testDeleteFilm() {
        filmDbStorage.deleteAll();
        assertEquals(0, filmDbStorage.findAll().size());
        List<Film> allFilm = getTestFilm();
        Film firstFilm = allFilm.get(0);
        Film secondFilm = allFilm.get(1);
        filmDbStorage.create(firstFilm);
        filmDbStorage.create(secondFilm);
        filmDbStorage.delByFilmId(firstFilm.getId());
        Assertions.assertEquals(secondFilm.getId(), new ArrayList<>(filmDbStorage.findAll()).get(0).getId());
    }
}