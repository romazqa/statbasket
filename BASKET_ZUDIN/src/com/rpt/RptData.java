package com.rpt;
import java.math.BigDecimal;

public class RptData {
	private BigDecimal cod_client; //��� �������
	private String fio_client; //��� �������
	private String name; //������������ ������ ������

	private BigDecimal count; //���-�� ������
	private BigDecimal summa; //��������� ���
	private BigDecimal avgsum; //sr
	// ������ ����������(���������) �������� �����
	public BigDecimal getCod_client() {
		return cod_client;
	}
	public void setCod_client(BigDecimal cod_client) {
		this.cod_client = cod_client;
	}
	public String getFio_client() {
		return fio_client;
	}
	public void setFio_client(String fio_client) {
		this.fio_client = fio_client;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public BigDecimal getCount() {
		return count;
	}
	public void setCount(BigDecimal count) {
		this.count = count;
	}
	public BigDecimal getSumma() {
		return summa;
	}
	public void setSumma(BigDecimal summa) {
		this.summa = summa;
	}
	public BigDecimal getAvgsum() {
		return avgsum;
	}
	public void setAvgsum(BigDecimal avgsum) {
		this.avgsum = avgsum;
	}
	
	
	
	}

