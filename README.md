# Animal Picture App 🐱🐶🦆  

A simple **Spring Boot microservice** (Java 17, Maven) that fetches random animal pictures (cats, dogs, ducks), stores them in an **H2 in-memory database**, and exposes REST APIs to retrieve them.  

The database is **volatile** (cleared when the app stops).  

---

## ✨ Features  

- **Fetch & Save** pictures by animal type (`cat`, `dog`, `duck`)  
- **Retrieve last saved picture** per animal   
- **H2 in-memory database** for persistence (`jdbc:h2:mem:testdb`)  
- Minimal **UI pages** (`index.html`, `gallery.html`) for quick testing  

---

## 🛠️ Tech Stack  

- Java 17  
- Spring Boot  
- Spring Data JPA  
- H2 Database (in-memory)  
- Maven (build tool)  
- Docker (containerization)  

---

## ✅ Prerequisites  

- **Java 17 JDK** installed → check with `java -version`  
- **JAVA_HOME** must point to JDK 17 (set env variable accordingly)  
- **Maven not required** (use included wrapper: `./mvnw` or `mvnw.cmd`)  

---

## 📦 Run Instructions  

### 1. Run locally with Maven  

```bash
# Build
./mvnw clean package

# Start app
./mvnw spring-boot:run
```  

Service will be available at: [http://localhost:8080](http://localhost:8080)  

⚠️ Make sure your system is using **Java 17** (`java -version`).  

---

### 2. Run locally without Maven (via IDE)  

- Open the project in IntelliJ / Eclipse / VS Code  
- Run the class:  
  ```java
  com.camunda.camundachallenge.AnimalPictureAppApplication
  ```  
- Spring Boot will start on [http://localhost:8080](http://localhost:8080)  

---

### 3. Run with Docker  

```bash
# Build Docker image
docker build -t animal-picture-app .

# Run container
docker run -p 8080:8080 animal-picture-app
```  

---

## 🔗 REST API Endpoints  

### Fetch and save pictures  
`POST /api/pictures?animal={animal}&count={n}`  

Example:  

```bash
curl -X POST "http://localhost:8080/api/pictures?animal=cat&count=3"
```  

Returns a list of stored picture IDs.  

---

### Get last saved picture of an animal  
`GET /api/pictures/last?animal={animal}`  

Example:  

```bash
curl http://localhost:8080/api/pictures/last?animal=dog --output dog.jpg
```  

---

### Get picture by ID  
`GET /api/pictures/{id}`  

Example:  

```bash
curl http://localhost:8080/api/pictures/1 --output cat.jpg
```  

---

## 🗄️ Database  

- Type: **H2 (in-memory)**  
- Console: [http://localhost:8080/h2-console](http://localhost:8080/h2-console)  
- JDBC URL: `jdbc:h2:mem:testdb`  
- Username: `sa`  
- Password: *(empty)*  

The repository interface:  

```java
public interface PictureRepository extends JpaRepository<Picture, Long> {
    Optional<Picture> findFirstByAnimalOrderByCreatedAtDesc(String animal);
}
```  

---

## 🧪 Automated Tests  

This project includes **unit and integration tests** for the **Controller** and **Service** layers.  

### Controller Tests  

- Uses `@SpringBootTest` and `@AutoConfigureMockMvc`  
- Tests endpoints:  
  - `POST /api/pictures` → verifies IDs returned and DB persistence  
  - `GET /api/pictures/last` → verifies correct picture returned with proper headers  
  - `GET /api/pictures/{id}` → verifies picture returned by ID or 404 if not found  

Example snippet:

```java
mockMvc.perform(post("/api/pictures")
        .param("animal", "cat")
        .param("count", "1"))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$[0]").exists());
```  

### Service Tests  

- Uses Mockito to mock `PictureRepository`  
- Tests methods:  
  - `getById()` → returns correct picture or null  
  - `getLast()` → returns latest picture per animal or null  
  - `fetchAndSave()` → persists correct number of pictures with proper attributes  

Example snippet:

```java
when(pictureRepository.findById(42L)).thenReturn(Optional.of(p));

Picture result = pictureService.getById(42L);
assertThat(result).isNotNull();
assertThat(result.getId()).isEqualTo(42L);
```  

