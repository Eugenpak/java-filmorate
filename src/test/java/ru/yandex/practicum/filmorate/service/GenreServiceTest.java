package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GenreServiceTest {
    @Mock
    private GenreStorage testGenreStorage;

    @InjectMocks
    private GenreService testGenreService;

    private List<Genre> getGenres() {
        // Mpa: (4,'R'), (5,'NC-17');
        Genre one = new Genre(1L,"Комедия");
        Genre two = new Genre(2L,"Драма");
        return List.of(one, two);
    }

    @Test
    void findAll() {
        List<Genre> expectedGenre = getGenres();
        Mockito.when(this.testGenreStorage.findAll()).thenReturn(expectedGenre);

        List<Genre> actualList = testGenreService.findAll().stream().toList();
        verify(testGenreStorage, times(1)).findAll();
        assertEquals(expectedGenre.size(), actualList.size());
        assertSame(expectedGenre.get(0), actualList.get(0));
    }

    @Test
    void findGenreById() {
        Genre expectedGenre = getGenres().get(0);
        when(testGenreStorage.findGenreById(expectedGenre.getId())).thenReturn(Optional.of(expectedGenre));

        Genre actualGenre = testGenreService.findGenreById(expectedGenre.getId());

        verify(testGenreStorage, times(1)).findGenreById(expectedGenre.getId());
        assertEquals(expectedGenre.getName(), actualGenre.getName());
        assertSame(expectedGenre, actualGenre);
    }
}