package com;

import com.data.*;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.databind.DeserializationFeature;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class ApiClient {

    private static final String BASE_URL = "http://localhost:8080/api";
    private final HttpClient client = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public ApiClient() {
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    private HttpResponse<String> sendRequest(HttpRequest request) throws IOException, InterruptedException {
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() >= 400) {
            throw new IOException("Server returned error: " + response.statusCode() + " " + response.body());
        }
        return response;
    }

    // --- Универсальный метод SAVE (для POST/PUT) ---
    private <T> T saveEntity(T entity, String path) throws IOException, InterruptedException {
        String requestBody = objectMapper.writeValueAsString(entity);
        HttpRequest.Builder builder = HttpRequest.newBuilder().header("Content-Type", "application/json");
        
        // Динамически получаем ID через рефлексию, чтобы не писать отдельный метод для каждой сущности
        Object id = null;
        try {
            id = entity.getClass().getMethod("getId").invoke(entity);
        } catch (Exception e) { /* ID нет или он null */ }

        if (id == null || (id instanceof Integer && (Integer)id == 0)) { // Создание
            builder.uri(URI.create(BASE_URL + path)).POST(HttpRequest.BodyPublishers.ofString(requestBody));
        } else { // Обновление
            builder.uri(URI.create(BASE_URL + path + "/" + id)).PUT(HttpRequest.BodyPublishers.ofString(requestBody));
        }

        HttpResponse<String> response = sendRequest(builder.build());
        return (T) objectMapper.readValue(response.body(), entity.getClass());
    }
    
    // --- Универсальный метод DELETE ---
     private void deleteEntity(int id, String path) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(BASE_URL + path + "/" + id)).DELETE().build();
        sendRequest(request);
    }

    // --- Уровни (Level) ---
    public List<Level> getAllLevels() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(BASE_URL + "/competition-levels")).GET().build();
        return objectMapper.readValue(sendRequest(request).body(), new TypeReference<>() {});
    }
    public Level saveLevel(Level level) throws IOException, InterruptedException { return saveEntity(level, "/competition-levels"); }
    public void deleteLevel(int id) throws IOException, InterruptedException { deleteEntity(id, "/competition-levels"); }

    // --- События (Event) ---
    public List<Event> getAllEvents() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(BASE_URL + "/events")).GET().build();
        return objectMapper.readValue(sendRequest(request).body(), new TypeReference<>() {});
    }
    public Event saveEvent(Event event) throws IOException, InterruptedException { return saveEntity(event, "/events"); }
    public void deleteEvent(int id) throws IOException, InterruptedException { deleteEntity(id, "/events"); }

    // --- Команды (Team) ---
    public List<Team> getAllTeams() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(BASE_URL + "/teams")).GET().build();
        return objectMapper.readValue(sendRequest(request).body(), new TypeReference<>() {});
    }
    public Team saveTeam(Team team) throws IOException, InterruptedException { return saveEntity(team, "/teams"); }
    public void deleteTeam(int id) throws IOException, InterruptedException { deleteEntity(id, "/teams"); }

    // --- Игроки (Player) ---
    public List<Player> getAllPlayers() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(BASE_URL + "/players")).GET().build();
        return objectMapper.readValue(sendRequest(request).body(), new TypeReference<>() {});
    }
    public Player savePlayer(Player player) throws IOException, InterruptedException { return saveEntity(player, "/players"); }
    public void deletePlayer(int id) throws IOException, InterruptedException { deleteEntity(id, "/players"); }

    // --- Матчи (Matches) ---
    public List<Matches> getAllMatches() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(BASE_URL + "/matches")).GET().build();
        return objectMapper.readValue(sendRequest(request).body(), new TypeReference<>() {});
    }
    public Matches saveMatch(Matches match) throws IOException, InterruptedException { return saveEntity(match, "/matches"); }
    public void deleteMatch(int id) throws IOException, InterruptedException { deleteEntity(id, "/matches"); }
}