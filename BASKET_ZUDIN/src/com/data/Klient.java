package com.data;

import java.math.BigDecimal;

public class Klient {
	 private BigDecimal kod_klient = null; // ���
	 private String fio = null; // ���
	 private String tel = null; // �������
	 private BigDecimal money = null; // ������
	public BigDecimal getKod_klient() {
		return kod_klient;
	}
	public void setKod_klient(BigDecimal kod_klient) {
		this.kod_klient = kod_klient;
	}
	public String getFio() {
		return fio;
	}
	public void setFio(String fio) {
		this.fio = fio;
	}
	public String getTel() {
		return tel;
	}
	public void setTel(String tel) {
		this.tel = tel;
	}
	public BigDecimal getMoney() {
		return money;
	}
	public void setMoney(BigDecimal money) {
		this.money = money;
	}
	@Override
	public String toString() {
		return fio;
	}
	 
	 

}
