package com.data;

// Мы больше не будем использовать BigDecimal для ID на клиенте, чтобы избежать путаницы.
// Integer вполне достаточно.
public class Team {
     private Integer id;
     private String city;
     private String name; // <-- Имя поля изменено на "name"
     private String gender; // <-- Имя поля изменено на "gender"

    // Пустой конструктор обязателен для Jackson
    public Team() {
    }

    // --- Геттеры и Сеттеры ---

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    @Override
    public String toString() {
        return name + ", " + gender;
    }
    
    private java.util.List<Player> players = new java.util.ArrayList<>();

    public java.util.List<Player> getPlayers() {
        return players;
    }

    public void setPlayers(java.util.List<Player> players) {
        this.players = players;
    }
}