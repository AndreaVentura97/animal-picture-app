package com.camunda.camundachallenge.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StreamUtils;

import com.camunda.camundachallenge.jpaRepository.PictureRepository;
import com.camunda.camundachallenge.model.Picture;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
public class PictureService {
    private final PictureRepository repo;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public PictureService(PictureRepository repo) {
        this.repo = repo;
    }

    @Transactional
    public List<Picture> fetchAndSave(String animal, int count) {
        List<Picture> saved = new ArrayList<>();
        for (int i = 0; i < Math.max(1, count); i++) {
            try {
                String imageUrl = resolveImageUrl(animal);
                HttpURLConnection conn = (HttpURLConnection) new URL(imageUrl).openConnection();
                conn.setInstanceFollowRedirects(true);
                conn.setRequestProperty("User-Agent", "animal-picture-app");
                conn.connect();

                String contentType = conn.getContentType();
                try (InputStream in = conn.getInputStream()) {
                    byte[] bytes = StreamUtils.copyToByteArray(in);
                    Picture p = new Picture();
                    p.setAnimal(animal.toLowerCase());
                    p.setContentType(contentType == null ? "image/jpeg" : contentType);
                    p.setData(bytes);
                    p.setCreatedAt(Instant.now());
                    saved.add(repo.save(p));
                }
            } catch (Exception e) {
                e.printStackTrace(); // log warning and continue
            }
        }
        return saved;
    }

    // NEW: fetch a picture by its ID
    public Picture getById(Long id) {
        return repo.findById(id).orElse(null);
    }

    public Picture getLast(String animal) {
        return repo.findFirstByAnimalOrderByCreatedAtDesc(animal.toLowerCase())
                   .orElse(null);
    }

    private String resolveImageUrl(String animal) throws Exception {
    switch (animal.toLowerCase()) {
        case "cat":
            // Cataas returns the image directly, so the URL itself is enough
            return "https://cataas.com/cat?position=center";
        case "dog": {
            URL api = new URL("https://dog.ceo/api/breeds/image/random");
            try (InputStream in = api.openStream()) {
                JsonNode node = objectMapper.readTree(in);
                return node.get("message").asText();
            }
        }
        case "duck": {
            URL api = new URL("https://random-d.uk/api/v2/random");
            try (InputStream in = api.openStream()) {
                JsonNode node = objectMapper.readTree(in);
                return node.get("url").asText();
            }
        }
        default:
            throw new IllegalArgumentException("Unsupported animal: " + animal);
    }
}



}
