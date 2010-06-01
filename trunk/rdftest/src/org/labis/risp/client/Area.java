package org.labis.risp.client;

import com.google.gwt.maps.client.overlay.Polygon;

public class Area {
	private Polygon poly;
	private Street[] streets;
	private int population;
	private int registers;
	
	public Area(Polygon polys, Street[] streets) {
		this.poly = polys;
		this.streets = streets;
		population = 0;
		registers = 0;
		for (int i = 0; i < streets.length; i++){
			population += streets[i].getPopulation();
			registers += streets[i].getRegisters();
		}
	}
	
	public Polygon getPoly() {
		return poly;
	}
	
	public void setPoly(Polygon poly) {
		this.poly = poly;
	}
	
	public Street[] getStreets() {
		return streets;
	}
	
	public void setStreets(Street[] streets) {
		this.streets = streets;
	}
	
	public int getPopulation() {
		return population;
	}
	
	public void setPopulation(int population) {
		this.population = population;
	}
	
	public int getRegisters() {
		return registers;
	}
	
	public void setRegisters(int registers) {
		this.registers = registers;
	}

}
