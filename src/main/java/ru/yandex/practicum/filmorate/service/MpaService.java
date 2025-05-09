package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.mpa.MpaStorage;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Slf4j
public class MpaService {
    private final MpaStorage mpaStorage;

    @Autowired
    public MpaService(MpaStorage mpaStorage) {
        this.mpaStorage = mpaStorage;
    }

    public Collection<Mpa> findAll() {
        log.info("Mpa-S findAll()");
        return mpaStorage.findAll();
    }

    public Mpa findMpaById(long id) {
        log.info("Mpa-S findMpaById({})",id);
        Optional<Mpa> findMpa = mpaStorage.findMpaById(id);
        if (findMpa.isEmpty()) {
            throw new NotFoundException("Рейтинг с id = " + id + " не найден");
        }
        log.info("Получен M-S findMpaById({})",id);
        return findMpa.get();
    }

    public Map<Long,Mpa> getMpaById(List<Long> mpaId) {
        log.info("Mpa-S getMpaById(): {}",mpaId);
        return mpaStorage.getMpaById(mpaId);
    }

    public Optional<Mpa> getValidMpaByFilmId(Long filmId) {
        log.info("Mpa-S getMpaById(): {}",filmId);
        List<Mpa> ml = mpaStorage.getMpasByFilmId(filmId);
        if (ml.size() == 0) {
            return Optional.empty();
        }
        return Optional.of(ml.get(0));
    }
}
