package com.data;

public class Event {
    private Integer id;
    private String name;
    private String location;
    private Integer year;
    
    // ВАЖНО: Имя поля должно быть competitionLevel, 
    // чтобы Jackson правильно сопоставил его с JSON от сервера
    private Level competitionLevel; 

    public Event() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public Level getCompetitionLevel() {
        return competitionLevel;
    }

    public void setCompetitionLevel(Level competitionLevel) {
        this.competitionLevel = competitionLevel;
    }

    @Override
    public String toString() {
        return name + " (" + location + ", " + year + ")";
    }
}