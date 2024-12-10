package com.data;

import java.math.BigDecimal;

public class Team {
	 private BigDecimal id_team = null; // Код
	 private String city = null; 
	
	 private String team_name = null; 

	 private String gender_team= null;

	public BigDecimal getId_team() {
		return id_team;
	}

	public void setId_team(BigDecimal id_team) {
		this.id_team = id_team;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getTeam_name() {
		return team_name;
	}

	public void setTeam_name(String team_name) {
		this.team_name = team_name;
	}

	public String getGender_team() {
		return gender_team;
	}

	public void setGender_team(String string) {
		this.gender_team = string;
	}

	@Override
	public String toString() {
		return team_name+", "+getGender_team();
	}
}
