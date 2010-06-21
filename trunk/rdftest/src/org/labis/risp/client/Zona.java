package org.labis.risp.client;

import java.io.Serializable;

public class Zona implements Serializable{
	private static final long serialVersionUID = 1L;
	private MyPolygon poly;
	private int habitantes;
	private int hojas;
	
	public int getHabitantes() {
		return habitantes;
	}

	public void setHabitantes(int habitantes) {
		this.habitantes = habitantes;
	}

	public int getHojas() {
		return hojas;
	}

	public void setHojas(int hojas) {
		this.hojas = hojas;
	}

	public Zona() {}
	
	public MyPolygon getPoly() {
		return poly;
	}
	
	public void setPoly(MyPolygon poly) {
		this.poly = poly;
	}
	
}
