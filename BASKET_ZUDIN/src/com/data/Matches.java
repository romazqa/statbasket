package com.data;

import java.math.BigDecimal;
import java.util.Date;

public class Matches {
	 private BigDecimal id_matches = null; // Код
	 private Date date_matches = null; // 
	 private BigDecimal team1score = null;
	 private BigDecimal team2score = null;
	 private BigDecimal id_event = null; // Код
	 private BigDecimal id_team1 = null; // Код
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
	private BigDecimal id_team2 = null; // Код
	 private Event event = null; // Код
	 private Team team1 = null; // Код
	 private Team team2 = null; // Код
	 private String playground = null; // 
	public BigDecimal getId_matches() {
		return id_matches;
	}
	public void setId_matches(BigDecimal id_matches) {
		this.id_matches = id_matches;
	}
	public Date getDate_matches() {
		return date_matches;
	}
	public void setDate_matches(Date date_matches) {
		this.date_matches = date_matches;
	}
	public BigDecimal getTeam1score() {
		return team1score;
	}
	public void setTeam1score(BigDecimal team1score) {
		this.team1score = team1score;
	}
	public BigDecimal getTeam2score() {
		return team2score;
	}
	public void setTeam2score(BigDecimal team2score) {
		this.team2score = team2score;
	}
	public BigDecimal getId_event() {
		return id_event;
	}
	public void setId_event(BigDecimal id_event) {
		this.id_event = id_event;
	}
	public BigDecimal getId_team1() {
		return id_team1;
	}
	public void setId_team1(BigDecimal id_team1) {
		this.id_team1 = id_team1;
	}
	public BigDecimal getId_team2() {
		return id_team2;
	}
	public void setId_team2(BigDecimal id_team2) {
		this.id_team2 = id_team2;
	}
	public String getPlayground() {
		return playground;
	}
	public void setPlayground(String playground) {
		this.playground = playground;
	}
    

}
