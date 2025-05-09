package ru.yandex.practicum.filmorate.storage.mpa;

import ru.yandex.practicum.filmorate.model.Mpa;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface MpaStorage {
    Optional<Mpa> findMpaById(long id);

    Collection<Mpa> findAll();

    Map<Long,Mpa> getMpaById(List<Long> mpaId);

    List<Mpa> getMpasByFilmId(long filmId);
}
