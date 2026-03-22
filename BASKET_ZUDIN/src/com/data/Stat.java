package com.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Stat {

    @JsonProperty("id")
    private Integer idPlayerStats;

    @JsonProperty("pointsScored")
    private Integer pointScored;

    @JsonProperty("assists")
    private Integer assists;

    @JsonProperty("steals")
    private Integer steal;

    @JsonProperty("turnovers")
    private Integer turnover;

    @JsonProperty("blockedShots")
    private Integer blockedShot;

    @JsonProperty("fouls")
    private Integer foul;

    @JsonProperty("twoPointers")
    private Integer doubleDouble;

    @JsonProperty("threePointers")
    private Integer triple;

    @JsonProperty("freeThrows")
    private Integer freeThrow;

    @JsonProperty("defensiveRebounds")
    private Integer dr;

    @JsonProperty("offensiveRebounds")
    private Integer or;

    private Player player;
    private Integer idMatch;
    private Integer idPlayer;

    public Stat() {
        // Инициализируем нулями, чтобы избежать ошибок NullPointerException при сложении
        this.pointScored = 0;
        this.assists = 0;
        this.steal = 0;
        this.turnover = 0;
        this.blockedShot = 0;
        this.foul = 0;
        this.doubleDouble = 0;
        this.triple = 0;
        this.freeThrow = 0;
        this.dr = 0;
        this.or = 0;
    }

    // --- Геттеры и Сеттеры (сохранены ваши старые названия) ---

    public Integer getIdPlayerStats() { return idPlayerStats; }
    public void setIdPlayerStats(Integer idPlayerStats) { this.idPlayerStats = idPlayerStats; }

    public Integer getPointScored() { return pointScored; }
    public void setPointScored(Integer pointScored) { this.pointScored = pointScored; }

    public Integer getAssists() { return assists; }
    public void setAssists(Integer assists) { this.assists = assists; }

    public Integer getSteal() { return steal; }
    public void setSteal(Integer steal) { this.steal = steal; }

    public Integer getTurnover() { return turnover; }
    public void setTurnover(Integer turnover) { this.turnover = turnover; }

    public Integer getBlockedShot() { return blockedShot; }
    public void setBlockedShot(Integer blockedShot) { this.blockedShot = blockedShot; }

    public Integer getFoul() { return foul; }
    public void setFoul(Integer foul) { this.foul = foul; }

    public Integer getDoubleDouble() { return doubleDouble; }
    public void setDoubleDouble(Integer doubleDouble) { this.doubleDouble = doubleDouble; }

    public Integer getTriple() { return triple; }
    public void setTriple(Integer triple) { this.triple = triple; }

    public Integer getFreeThrow() { return freeThrow; }
    public void setFreeThrow(Integer freeThrow) { this.freeThrow = freeThrow; }

    public Integer getDr() { return dr; }
    public void setDr(Integer dr) { this.dr = dr; }

    public Integer getOr() { return or; }
    public void setOr(Integer or) { this.or = or; }

    public Player getPlayer() { return player; }
    public void setPlayer(Player player) { this.player = player; }

    public Integer getIdMatch() { return idMatch; }
    public void setIdMatch(Integer idMatch) { this.idMatch = idMatch; }

    public Integer getIdPlayer() { return idPlayer; }
    public void setIdPlayer(Integer idPlayer) { this.idPlayer = idPlayer; }
}