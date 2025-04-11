package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.director.DirectorStorage;

import java.util.*;

@Service
@Slf4j
public class DirectorService {
    private final DirectorStorage directorStorage;

    @Autowired
    public DirectorService(DirectorStorage directorStorage) {
        this.directorStorage = directorStorage;
    }

    public Director createDirector(Director director) {
        return directorStorage.create(director);
    }

    public Director updateDirector(Director director) {
        log.info("D-S Director updateDirector({})",director);
        getDirectorById(director.getId());
        return directorStorage.update(director);
    }

    public Director getDirectorById(Long id) {
        log.info("D-S Director getDirectorById({})",id);
        Optional<Director> findDirector = directorStorage.findDirectorById(id);
        if (findDirector.isEmpty()) {
            throw new NotFoundException("Директор с id = " + id + " не найден");
        }
        log.info("Получен Director getDirectorById({})",id);
        return findDirector.get();
    }

    public Map<Long, Director> getDirectorByListId(List<Long> dIdL) {
        log.info("D-S Director getDirectorByListId({})",dIdL);
        Collection<Director> findDirector = directorStorage.findDirectorByListId(dIdL);

        Map<Long,Director> directorMap = new HashMap<>();
        findDirector.forEach(d -> directorMap.put(d.getId(),d));

        log.info("Получен Director getDirectorByListId()");
        return directorMap;
    }

    public Collection<Director> getAllDirectors() {
        log.info("D-S Director getAllDirectors()");
        return directorStorage.findAll();
    }

    public void deleteDirectorById(Long id) {
        log.info("D-S Director deleteDirectorById({})",id);
        getDirectorById(id);
        if (directorStorage.delDirectorById(id)) {
            log.info("Директор с id = " + id + " удален");
        }
    }

    public void findNotValid(Collection<Long> directorId) {
        log.info("Start D-S findNotValid(directors: {})",directorId);

        List<Long> result = directorStorage.findNotValid(directorId);
        if (result.size() != 0) {
            String msg = result.toString();
            throw new NotFoundException("Директор directors: {id:" + msg + "} не найден");
        }
        log.info("D-S findNotValid(). Проверено directors!");
    }
}
