package com.camunda.camundachallenge.model;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
public class Picture {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String animal; 
    private String contentType;
    private Instant createdAt = Instant.now();

    @Lob
    @Column(columnDefinition = "BLOB")
    private byte[] data;

    public Long getId() {
        return id;
    }

    public String getAnimal() {
        return animal;
    }

    public String getContentType() {
        return contentType;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public byte[] getData() {
        return data;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setAnimal(String animal) {
        this.animal = animal;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    // getters & setters
    // (omitted for brevity — add standard ones)
}
