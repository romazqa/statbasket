package com.data;

import java.math.BigDecimal;

public class Event {

			@Override
	public String toString() {
		return name_event+" "+locationn;
	}
			private BigDecimal id_event = null; // Код
			 private String name_event = null; 
			 private String locationn = null; 
			 private BigDecimal year_event = null; // Код
			 private BigDecimal id_competition_level = null; // Код
			 private Level level = null;
			public BigDecimal getId_event() {
				return id_event;
			}
			public void setId_event(BigDecimal id_event) {
				this.id_event = id_event;
			}
			public String getName_event() {
				return name_event;
			}
			public void setName_event(String name_event) {
				this.name_event = name_event;
			}
			public String getLocationn() {
				return locationn;
			}
			public void setLocationn(String locationn) {
				this.locationn = locationn;
			}
			public BigDecimal getYear_event() {
				return year_event;
			}
			public void setYear_event(BigDecimal year_event) {
				this.year_event = year_event;
			}
			public BigDecimal getId_competition_level() {
				return id_competition_level;
			}
			public void setId_competition_level(BigDecimal id_competition_level) {
				this.id_competition_level = id_competition_level;
			}
			public Level getLevel() {
				return level;
			}
			public void setLevel(Level level) {
				this.level = level;
			}
		   
}