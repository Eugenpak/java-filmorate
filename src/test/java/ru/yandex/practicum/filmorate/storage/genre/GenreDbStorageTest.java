package ru.yandex.practicum.filmorate.storage.genre;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class GenreDbStorageTest {
    private final GenreDbStorage genreStorage;

    private List<Genre> getTestGenres() {
        Genre genreTrue = Genre.builder().id(1L).build();
        Genre genreFalse = Genre.builder().id(100L).build();
        return List.of(genreTrue,genreFalse);
    }

    @Test
    void findGenreById() {
        Optional<Genre> genreOptional = genreStorage.findGenreById(1L);
        assertThat(genreOptional).isPresent()
                .hasValueSatisfying(genre ->
                        assertThat(genre).hasFieldOrPropertyWithValue("id", 1L)
                );
    }

    @Test
    void findAll() {
        Collection<Genre> list = genreStorage.findAll();
        assertEquals(6, list.size());
    }

    @Test
    void findValid() {
        assertDoesNotThrow(() -> genreStorage.findNotValid(Set.of(getTestGenres().get(0))));
    }

    @Test
    void findNotValid() {

        Throwable exception = assertThrows(NotFoundException.class, () -> {
            genreStorage.findNotValid(Set.of(getTestGenres().get(1)));
        });
        assertEquals("Жанр genres: {id:100} не найден", exception.getMessage());

    }
}