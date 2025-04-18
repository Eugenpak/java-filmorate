package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FilmController.class)
public class FilmControllerWebMvcTest {
    @Autowired
    private MockMvc mvc;

    @MockBean
    private FilmService testFilmService;

    private List<Film> getFilms() {
        Film one = new Film(1L, "name-1", "description-1", LocalDate.of(2000,7,1),100,
                new HashSet<>(getGenres()),new Mpa(1L,"G"),
                List.of(new Director(1L,"director-1")));
        Film two = new Film(2L, "name-2", "description-2", LocalDate.of(19780,7,1),100,
                new HashSet<>(getGenres()),new Mpa(2L,"PG"),
                List.of(new Director(2L,"director-2")));
        return List.of(one, two);
    }

    private List<Genre> getGenres() {
        // Mpa: (4,'R'), (5,'NC-17');
        Genre one = new Genre(1L, "Комедия");
        Genre two = new Genre(2L, "Драма");
        return List.of(one, two);
    }

    @Test
    void findAllShouldReturnAllFilms() throws Exception {
        Mockito.when(this.testFilmService.findAll()).thenReturn(getFilms());

        mvc.perform(get("/films").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void findFilmByIdShouldReturnValidFilm() throws Exception {
        Mockito.when(this.testFilmService.findFilmById(1L)).thenReturn(getFilms().get(0));

        mvc.perform(get("/films/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("name-1"));
    }

    @Test
    void createFilmShouldReturnValidFilm() throws Exception {
        /*
        Mockito.when(testFilmService.create(getFilms().get(0))).thenReturn(getFilms().get(0));

        String jsonString = "{\"name\":\"name-1\",\"description\":\"description-1\",\"releaseDate\":\"2000-07-01\"," +
                "\"duration\":100,\"genres\":[{\"id\":1},{\"id\":2}],\"mpa\":{\"id\":1},\"directors\":[{\"id\":1}]}";
        //{"name":"New film","releaseDate":"1999-04-30","description":"New film about friends","duration":120,"rate":4,"mpa":{"id":3},"genres":[{"id":1}]}

        mvc.perform(post("/films").content(jsonString)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("name-1"))
                .andExpect(jsonPath("$.description").value("description-1"));

         */
    }
}
