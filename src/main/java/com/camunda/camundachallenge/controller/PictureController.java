package com.camunda.camundachallenge.controller;

import com.camunda.camundachallenge.model.Picture;
import com.camunda.camundachallenge.service.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pictures")
public class PictureController {
    private final PictureService service;

    public PictureController(PictureService service) {
        this.service = service;
    }

    // POST /api/pictures?animal=cat&count=3
    @PostMapping
    public ResponseEntity<?> fetchAndSave(
            @RequestParam String animal,
            @RequestParam(defaultValue = "1") int count) {
        try {
            List<Picture> saved = service.fetchAndSave(animal, count);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(saved.stream().map(Picture::getId).toList());
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    // GET /api/pictures/last?animal=cat
    @GetMapping("/last")
    public ResponseEntity<byte[]> getLast(@RequestParam String animal) {
        Picture p = service.getLast(animal);
        if (p == null) {
            return ResponseEntity.notFound().build();
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(p.getContentType()));
        headers.setContentLength(p.getData().length);
        headers.set("Cache-Control", "no-store");
        return new ResponseEntity<>(p.getData(), headers, HttpStatus.OK);
    }

    // GET /api/pictures/{id}
    @GetMapping("/{id}")
    public ResponseEntity<byte[]> getById(@PathVariable Long id) {
        Picture p = service.getById(id);  // add this method in PictureService
        if (p == null) return ResponseEntity.notFound().build();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(p.getContentType()));
        headers.setContentLength(p.getData().length);
        headers.set("Cache-Control", "no-store");
        return new ResponseEntity<>(p.getData(), headers, HttpStatus.OK);
    }
}
