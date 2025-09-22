package com.camunda.camundachallenge.jpaRepository;

import com.camunda.camundachallenge.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface PictureRepository extends JpaRepository<Picture, Long> {
    Optional<Picture> findFirstByAnimalOrderByCreatedAtDesc(String animal);
}
