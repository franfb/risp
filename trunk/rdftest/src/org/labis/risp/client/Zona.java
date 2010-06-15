package org.labis.risp.client;

import java.util.ArrayList;

import com.google.gwt.maps.client.overlay.Polygon;

public class Zona {
	private Polygon poly;
	private ArrayList<Portal> portales;
	private int habitantes;
	private int hojas;
	
	public ArrayList<Portal> getPortales() {
		return portales;
	}

	public void setPortales(ArrayList<Portal> portales) {
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

	public Zona(Polygon polys, ArrayList<Portal> portales) {
		this.poly = polys;
		this.portales = portales;
		habitantes = 0;
		hojas = 0;
		for (int i = 0; i < portales.size(); i++){
			habitantes += portales.get(i).getHabitantes();
			hojas += portales.get(i).getHojas();
		}
	}
	
	public Polygon getPoly() {
		return poly;
	}
	
	public void setPoly(Polygon poly) {
		this.poly = poly;
	}
	
}
