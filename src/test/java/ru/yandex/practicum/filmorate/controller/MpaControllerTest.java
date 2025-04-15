package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.MpaService;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


//@WebMvcTest(MpaController.class)
class MpaControllerTest {
    /*@Autowired
    private MockMvc mvc;

    @MockBean
    private MpaService  testMpaService;*/

    private List<Mpa> getMpas() {
        // Mpa: (4,'R'), (5,'NC-17');
        Mpa one = new Mpa(1L,"G");
        Mpa two = new Mpa(2L,"PG");
        Mpa three = new Mpa(3L,"PG-13");
        return List.of(one, two, three);
    }

    @Test
    void findAllShouldReturnAllMpas() throws Exception {
        /*Mockito.when(this.testMpaService.findAll()).thenReturn(getMpas());

        mvc.perform(get("/mpa").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3));*/
    }

    @Test
    void findMpaByIdShouldReturnValidMpa() throws Exception  {
        /*Mockito.when(this.testMpaService.findMpaById(1L)).thenReturn(getMpas().get(0));

        mvc.perform(get("/mpa/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("G"));*/
    }
}