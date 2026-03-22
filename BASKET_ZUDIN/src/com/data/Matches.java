package com.data;

import java.time.LocalDate;


public class Matches {
    private Integer id;
    private LocalDate date;
    private Integer team1Score;
    private Integer team2Score;
    private String playground;
    
    // Ссылки на полные объекты, а не на их ID
    private Event event;
    private Team team1;
    private Team team2;

    public Matches() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Integer getTeam1Score() {
        return team1Score;
    }

    public void setTeam1Score(Integer team1Score) {
        this.team1Score = team1Score;
    }

    public Integer getTeam2Score() {
        return team2Score;
    }

    public void setTeam2Score(Integer team2Score) {
        this.team2Score = team2Score;
    }

    public String getPlayground() {
        return playground;
    }

    public void setPlayground(String playground) {
        this.playground = playground;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public Team getTeam1() {
        return team1;
    }

    public void setTeam1(Team team1) {
        this.team1 = team1;
    }

    public Team getTeam2() {
        return team2;
    }

    public void setTeam2(Team team2) {
        this.team2 = team2;
    }

    @Override
    public String toString() {
        String t1 = (team1 != null) ? team1.getName() : "Команда 1";
        String t2 = (team2 != null) ? team2.getName() : "Команда 2";
        return t1 + " vs " + t2 + " (" + date + ")";
    }
    
    private java.util.List<Stat> playerStats = new java.util.ArrayList<>();
    public java.util.List<Stat> getPlayerStats() { return playerStats; }
    public void setPlayerStats(java.util.List<Stat> playerStats) { this.playerStats = playerStats; }
}