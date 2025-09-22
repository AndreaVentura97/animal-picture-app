package com.camunda.camundachallenge.service;

import com.camunda.camundachallenge.jpaRepository.PictureRepository;
import com.camunda.camundachallenge.model.Picture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class PictureServiceTest {

    private PictureRepository pictureRepository;
    private PictureService pictureService;

    @BeforeEach
    void setup() {
        pictureRepository = mock(PictureRepository.class);
        pictureService = new PictureService(pictureRepository);
    }

    @Test
    void getById_shouldReturnPicture() {
        Picture p = new Picture();
        p.setId(42L);
        when(pictureRepository.findById(42L)).thenReturn(Optional.of(p));

        Picture result = pictureService.getById(42L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(42L);
    }

    @Test
    void getById_shouldReturnNullIfNotFound() {
        when(pictureRepository.findById(99L)).thenReturn(Optional.empty());

        Picture result = pictureService.getById(99L);

        assertThat(result).isNull();
    }

    @Test
    void getLast_shouldReturnLatestPicture() {
        Picture p = new Picture();
        p.setAnimal("dog");
        p.setCreatedAt(Instant.now());
        when(pictureRepository.findFirstByAnimalOrderByCreatedAtDesc("dog"))
                .thenReturn(Optional.of(p));

        Picture result = pictureService.getLast("dog");

        assertThat(result).isNotNull();
        assertThat(result.getAnimal()).isEqualTo("dog");
    }

    @Test
    void getLast_shouldReturnNullIfEmpty() {
        when(pictureRepository.findFirstByAnimalOrderByCreatedAtDesc("cat"))
                .thenReturn(Optional.empty());

        Picture result = pictureService.getLast("cat");

        assertThat(result).isNull();
    }

    

    @Test
    void fetchAndSave_shouldPersistPictures() throws Exception {
        // Arrange: fake picture returned from repository
        when(pictureRepository.save(any(Picture.class))).thenAnswer(invocation -> {
            Picture p = invocation.getArgument(0);
            p.setId(1L);
            return p;
        });

        // Act
        var result = pictureService.fetchAndSave("duck", 1);

        // Assert
        assertThat(result).hasSize(1);
        Picture saved = result.get(0);

        assertThat(saved.getAnimal()).isEqualTo("duck");
        assertThat(saved.getId()).isEqualTo(1L);

        ArgumentCaptor<Picture> captor = ArgumentCaptor.forClass(Picture.class);
        verify(pictureRepository, atLeastOnce()).save(captor.capture());
    }
}
