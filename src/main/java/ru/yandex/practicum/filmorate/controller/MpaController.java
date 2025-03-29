package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.MpaService;
import java.util.Collection;

@RestController
@RequestMapping("/mpa")
@Slf4j
public class MpaController {
    private final MpaService mpaService;

    @Autowired
    public MpaController(MpaService mpaService) {
        this.mpaService = mpaService;
    }

    @GetMapping
    public Collection<Mpa> findAll() {
        log.info("Start Mpa findAll()");
        return mpaService.findAll();
    }

    @GetMapping(value = "/{id}")
    public Mpa findMpaById(@NotNull @PathVariable long id) {
        log.info("MC Mpa findMpaById(id=" + id + ")");
        Mpa findMpa = mpaService.findMpaById(id);
        log.info("MC findMpaById(id=" + id + "): " + findMpa);
        return findMpa;
    }
}
