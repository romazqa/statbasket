package com.data;

import java.math.BigDecimal;

public class Level {
	private BigDecimal kod = null; // ���
	 public BigDecimal getKod() {
		return kod;
	}
	public void setKod(BigDecimal kod) {
		this.kod = kod;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	private String name = null; // ����-�
	@Override
	public String toString() {
		return name;
	}
}
