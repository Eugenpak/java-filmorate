package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.DirectorService;

import java.util.Collection;

@RestController
@RequestMapping("/directors")
@Validated
@Slf4j
public class DirectorController {
    private final DirectorService directorService;

    @Autowired
    public DirectorController(DirectorService directorService) {
        this.directorService = directorService;
    }

    @GetMapping
    public Collection<Director> getAllDirectors() {
        log.debug("Получение всех режиссеров");
        return directorService.getAllDirectors();
    }

    @PostMapping
    public Director createDirector(@Valid @RequestBody Director director) {
        log.debug("Создается режиссер: {}", director);
        return directorService.createDirector(director);
    }

    @PutMapping
    public Director updateDirector(@Valid @RequestBody Director director) {
        log.debug("Обновляется режиссер: {}", director);
        return directorService.updateDirector(director);
    }

    @GetMapping("/{id}")
    public Director getDirectorById(@PathVariable Long id) {
        log.debug("Получение режиссера по id: {}", id);
        return directorService.getDirectorById(id);
    }

    @DeleteMapping("/{id}")
    public void deleteDirectorById(@PathVariable Long id) {
        log.debug("Удаление режиссера по id: {}", id);
        directorService.deleteDirectorById(id);
        log.debug("Режиссер с id: {} удалён", id);
    }
}
