package com.data;

import java.math.BigDecimal;

public class Sotrud {
	 private BigDecimal kod_sotrud = null; // Код
	 private String fio = null;
	 private BigDecimal stag = null; // Код
	public BigDecimal getKod_sotrud() {
		return kod_sotrud;
	}
	public void setKod_sotrud(BigDecimal kod_sotrud) {
		this.kod_sotrud = kod_sotrud;
	}
	public String getFio() {
		return fio;
	}
	public void setFio(String fio) {
		this.fio = fio;
	}
	public BigDecimal getStag() {
		return stag;
	}
	public void setStag(BigDecimal stag) {
		this.stag = stag;
	}
	@Override
	public String toString() {
		return getFio();
	} 
	 
}
