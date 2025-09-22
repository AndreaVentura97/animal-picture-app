package com.camunda.camundachallenge.controller;

import com.camunda.camundachallenge.model.Picture;
import com.camunda.camundachallenge.jpaRepository.PictureRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;


import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class PictureControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PictureRepository pictureRepository;

    @BeforeEach
    void setup() {
        pictureRepository.deleteAll();
    }

    @Test
    void shouldSaveAndReturnIds() throws Exception {
        mockMvc.perform(post("/api/pictures")
                        .param("animal", "cat")
                        .param("count", "1"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$[0]").exists());

        assertThat(pictureRepository.count()).isEqualTo(1);
    }

    @Test
    void shouldReturnLastPicture() throws Exception {
        Picture p = new Picture();
        p.setAnimal("dog");
        p.setContentType("image/jpeg");
        p.setData("doggo".getBytes());
        pictureRepository.save(p);

        mockMvc.perform(get("/api/pictures/last")
                        .param("animal", "dog"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", "image/jpeg"))
                .andExpect(content().bytes("doggo".getBytes()));
    }

    @Test
    void shouldReturnPictureById() throws Exception {
        Picture p = new Picture();
        p.setAnimal("duck");
        p.setContentType("image/png");
        p.setData("quack".getBytes());
        p = pictureRepository.save(p);

        mockMvc.perform(get("/api/pictures/" + p.getId()))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", "image/png"))
                .andExpect(content().bytes("quack".getBytes()));
    }

    @Test
    void shouldReturn404IfIdNotFound() throws Exception {
        mockMvc.perform(get("/api/pictures/9999"))
                .andExpect(status().isNotFound());
    }

}
