package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.mpa.MpaStorage;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MpaServiceTest {
    @Mock
    private MpaStorage testMpaStorage;

    @InjectMocks
    private MpaService testMpaService;

    private List<Mpa> getMpas() {
        Mpa one = new Mpa(1L, "G");
        Mpa two = new Mpa(2L, "PG");
        return List.of(one, two);
    }

    @Test
    void findAll() {
        List<Mpa> expectedMpas = getMpas();
        Mockito.when(this.testMpaStorage.findAll()).thenReturn(expectedMpas);

        List<Mpa> actualList = testMpaService.findAll().stream().toList();
        verify(testMpaStorage, times(1)).findAll();
        assertEquals(expectedMpas.size(), actualList.size());
        assertSame(expectedMpas.get(0), actualList.get(0));
    }

    @Test
    void findMpaById() {
        Mpa expectedMpa = getMpas().get(0);
        when(testMpaStorage.findMpaById(expectedMpa.getId())).thenReturn(Optional.of(expectedMpa));

        Mpa actualMpa = testMpaService.findMpaById(expectedMpa.getId());

        verify(testMpaStorage, times(1)).findMpaById(expectedMpa.getId());
        assertEquals(expectedMpa.getName(), actualMpa.getName());
        assertSame(expectedMpa, actualMpa);
    }
}