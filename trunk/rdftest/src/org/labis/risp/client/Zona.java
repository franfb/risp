package org.labis.risp.client;

import com.google.gwt.maps.client.overlay.Polygon;

public class Zona {
	private Polygon poly;
	private Portal[] portales;
	private int habitantes;
	private int hojas;
	
	public Portal[] getPortales() {
		return portales;
	}

	public void setPortales(Portal[] portales) {
		this.portales = portales;
	}

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

	public Zona(Polygon polys, Portal[] portales) {
		this.poly = polys;
		this.portales = portales;
		habitantes = 0;
		hojas = 0;
		for (int i = 0; i < portales.length; i++){
			habitantes += portales[i].getHabitantes();
			hojas += portales[i].getHabitantes();
		}
	}
	
	public Polygon getPoly() {
		return poly;
	}
	
	public void setPoly(Polygon poly) {
		this.poly = poly;
	}
	
}
