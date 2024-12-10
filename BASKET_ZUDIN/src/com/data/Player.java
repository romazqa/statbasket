package com.data;

import java.math.BigDecimal;
import java.util.Date;


public class Player {
	private BigDecimal id = null; // Код
	private Date birthday = null; 
	 private String name = null; 
	 private BigDecimal height = null; 
	 private BigDecimal weight = null; // 
	 private String role = null; 
	 private BigDecimal id_team = null; // Код
	 private String gender = null; 
	 private BigDecimal num = null; 
	 private Team team = null;
	public BigDecimal getId() {
		return id;
	}
	public void setId(BigDecimal id) {
		this.id = id;
	}
	public Date getBirthday() {
		return birthday;
	}
	public void setBirthday(Date birthday) {
		this.birthday = birthday;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public BigDecimal getHeight() {
		return height;
	}
	public void setHeight(BigDecimal height) {
		this.height = height;
	}
	public BigDecimal getWeight() {
		return weight;
	}
	public void setWeight(BigDecimal weight) {
		this.weight = weight;
	}
	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}
	public BigDecimal getId_team() {
		return id_team;
	}
	public void setId_team(BigDecimal id_team) {
		this.id_team = id_team;
	}
	public String getGender() {
		return gender;
	}
	public void setGender(String gender) {
		this.gender = gender;
	}
	public Team getTeam() {
		return team;
	}
	public void setTeam(Team team) {
		this.team = team;
	}
	@Override
	public String toString() {
		return name;
	}
	public BigDecimal getNum() {
		return num;
	}
	public void setNum(BigDecimal num) {
		this.num = num;
	}
}
