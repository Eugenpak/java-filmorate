package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.GenreService;

import java.util.List;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(GenreController.class)
class GenreControllerTest {
    @Autowired
    private MockMvc mvc;

    @MockBean
    private GenreService testGenreService;

    private List<Genre> getGenres() {
        // Mpa: (4,'R'), (5,'NC-17');
        Genre one = new Genre(1L, "Комедия");
        Genre two = new Genre(2L, "Драма");
        return List.of(one, two);
    }

    @Test
    void findAllShouldReturnAllGenres() throws Exception {
        Mockito.when(this.testGenreService.findAll()).thenReturn(getGenres());

        mvc.perform(get("/genres").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void findGenreByIdShouldReturnValidGenre() throws Exception {
        Mockito.when(this.testGenreService.findGenreById(1L)).thenReturn(getGenres().get(0));

        mvc.perform(get("/genres/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Комедия"));
    }
}