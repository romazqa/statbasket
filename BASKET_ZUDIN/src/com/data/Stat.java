package com.data;


import java.math.BigDecimal;


public class Stat {
    private BigDecimal idPlayerStats;
    private Integer pointScored = 0; // Инициализировано 0
    private Integer assists = 0;  // Инициализировано 0
    private Integer steal = 0;   // Инициализировано 0
    private Integer turnover = 0;  // Инициализировано 0
    private Integer blockedShot = 0; // Инициализировано 0
    private Integer foul = 0;    // Инициализировано 0
    private Integer doubleDouble = 0; // Инициализировано 0
    private Integer triple = 0;   // Инициализировано 0
    private Integer freeThrow = 0;  // Инициализировано 0
    private Integer dr = 0;     // Инициализировано 0
    private Integer or = 0;     // Инициализировано 0
    private Integer idPlayer;
    private BigDecimal idMatch;
    private Player player; // Переименовано в lowercase для соответствия Java conventions

    // Геттеры и Сеттеры

    public Integer getTriple() {
        return triple;
    }

    public void setTriple(Integer triple) {
        this.triple = triple;
    }

    public Integer getDr() {
        return dr;
    }

    public void setDr(Integer dr) {
        this.dr = dr;
    }

    public Integer getOr() {
        return or;
    }

    public void setOr(Integer or) {
        this.or = or;
    }

    public BigDecimal getIdPlayerStats() {
        return idPlayerStats;
    }

    public void setIdPlayerStats(BigDecimal idPlayerStats) {
        this.idPlayerStats = idPlayerStats;
    }

    public Integer getPointScored() {
        return pointScored;
    }

    public void setPointScored(Integer pointScored) {
        this.pointScored = pointScored;
    }

    public Integer getAssists() {
        return assists;
    }

    public void setAssists(Integer assists) {
        this.assists = assists;
    }

    public Integer getSteal() {
        return steal;
    }

    public void setSteal(Integer steal) {
        this.steal = steal;
    }

    public Integer getTurnover() {
        return turnover;
    }

    public void setTurnover(Integer turnover) {
        this.turnover = turnover;
    }

    public Integer getBlockedShot() {
        return blockedShot;
    }

    public void setBlockedShot(Integer blockedShot) {
        this.blockedShot = blockedShot;
    }

    public Integer getFoul() {
        return foul;
    }

    public void setFoul(Integer foul) {
        this.foul = foul;
    }

    public Integer getDoubleDouble() {
        return doubleDouble;
    }

    public void setDoubleDouble(Integer doubleDouble) {
        this.doubleDouble = doubleDouble;
    }

    public Integer getFreeThrow() {
        return freeThrow;
    }

    public void setFreeThrow(Integer freeThrow) {
        this.freeThrow = freeThrow;
    }

    public Integer getIdPlayer() {
        return idPlayer;
    }

    public void setIdPlayer(Integer idPlayer) {
        this.idPlayer = idPlayer;
    }

    public BigDecimal getIdMatch() {
        return idMatch;
    }

    public void setIdMatch(BigDecimal idMatch) {
        this.idMatch = idMatch;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

	
}