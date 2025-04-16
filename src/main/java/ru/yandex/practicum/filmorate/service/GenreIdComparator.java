package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Comparator;

@Component
public class GenreIdComparator implements Comparator<Genre> {
    @Override
    public int compare(Genre genre1, Genre genre2) {
        return (int) (genre1.getId() - genre2.getId());
    }
}
