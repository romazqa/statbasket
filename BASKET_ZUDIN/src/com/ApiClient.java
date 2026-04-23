package com;

import com.data.*;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;

public class ApiClient {

    private static final String BASE_URL = "http://localhost:8080/api";
    private final HttpClient client = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public ApiClient() {
        objectMapper.registerModule(new JavaTimeModule());
        // Игнорируем неизвестные поля, если сервер пришлет лишнее
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        // Отправляем даты как строки "YYYY-MM-DD"
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    private HttpResponse<String> sendRequest(HttpRequest request) throws IOException, InterruptedException {
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() >= 400) {
            throw new IOException("Ошибка сервера: " + response.statusCode() + " " + response.body());
        }
        return response;
    }

    // --- Универсальный метод SAVE (для простых сущностей) ---
    private <T> T saveEntity(T entity, String path) throws IOException, InterruptedException {
        String requestBody = objectMapper.writeValueAsString(entity);
        HttpRequest.Builder builder = HttpRequest.newBuilder().header("Content-Type", "application/json");
        
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
    public void deleteMatch(int id) throws IOException, InterruptedException { deleteEntity(id, "/matches"); }

    // --- Специальный безопасный метод для сохранения матча ---
    public Matches saveMatch(Matches match) throws IOException, InterruptedException {
        Map<String, Object> payload = new HashMap<>();
        
        if (match.getId() != null) payload.put("id", match.getId());
        if (match.getDate() != null) payload.put("date", match.getDate().toString()); 
        payload.put("playground", match.getPlayground());
        payload.put("team1Score", match.getTeam1Score() != null ? match.getTeam1Score() : 0);
        payload.put("team2Score", match.getTeam2Score() != null ? match.getTeam2Score() : 0);
        
        if (match.getEvent() != null) payload.put("event", Map.of("id", match.getEvent().getId()));
        if (match.getTeam1() != null) payload.put("team1", Map.of("id", match.getTeam1().getId()));
        if (match.getTeam2() != null) payload.put("team2", Map.of("id", match.getTeam2().getId()));
        
        List<Map<String, Object>> statsPayload = new ArrayList<>();
        if (match.getPlayerStats() != null) {
            for (Stat s : match.getPlayerStats()) {
                Map<String, Object> sp = new HashMap<>();
                if (s.getIdPlayerStats() != null) sp.put("id", s.getIdPlayerStats());
                
                sp.put("pointsScored", s.getPointScored() != null ? s.getPointScored() : 0);
                sp.put("assists", s.getAssists() != null ? s.getAssists() : 0);
                sp.put("steals", s.getSteal() != null ? s.getSteal() : 0);
                sp.put("turnovers", s.getTurnover() != null ? s.getTurnover() : 0);
                sp.put("blockedShots", s.getBlockedShot() != null ? s.getBlockedShot() : 0);
                sp.put("fouls", s.getFoul() != null ? s.getFoul() : 0);
                sp.put("twoPointers", s.getDoubleDouble() != null ? s.getDoubleDouble() : 0);
                sp.put("threePointers", s.getTriple() != null ? s.getTriple() : 0);
                sp.put("freeThrows", s.getFreeThrow() != null ? s.getFreeThrow() : 0);
                sp.put("defensiveRebounds", s.getDr() != null ? s.getDr() : 0);
                sp.put("offensiveRebounds", s.getOr() != null ? s.getOr() : 0);
                
                if (s.getPlayer() != null) {
                    sp.put("player", Map.of("id", s.getPlayer().getId()));
                }
                statsPayload.add(sp);
            }
        }
        payload.put("playerStats", statsPayload);

        String requestBody = objectMapper.writeValueAsString(payload);
        
        HttpRequest.Builder builder = HttpRequest.newBuilder().header("Content-Type", "application/json");
        if (match.getId() == null || match.getId() == 0) {
            builder.uri(URI.create(BASE_URL + "/matches")).POST(HttpRequest.BodyPublishers.ofString(requestBody));
        } else {
            builder.uri(URI.create(BASE_URL + "/matches/" + match.getId())).PUT(HttpRequest.BodyPublishers.ofString(requestBody));
        }

        HttpResponse<String> response = client.send(builder.build(), HttpResponse.BodyHandlers.ofString());
        
        if (response.statusCode() >= 400) {
            throw new IOException("Ошибка сервера: " + response.statusCode() + " " + response.body());
        }
        
        return objectMapper.readValue(response.body(), Matches.class);
    }
}