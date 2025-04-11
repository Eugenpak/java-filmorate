package ru.yandex.practicum.filmorate.storage.director;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface DirectorStorage {
    Collection<Director> findAll();

    Director create(Director director);

    Director update(Director director);

    Optional<Director> findDirectorById(long id);

    boolean delDirectorById(long id);

    List<Long> findNotValid(Collection<Long> dIdL);

    Collection<Director> findDirectorByListId(Collection<Long> dIdL);
}
