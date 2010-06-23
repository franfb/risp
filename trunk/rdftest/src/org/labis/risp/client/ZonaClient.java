package org.labis.risp.client;


import com.google.gwt.maps.client.overlay.Polygon;

public class ZonaClient{
	private Polygon poly;
	private int habitantes;
	private int hojas;
	private String color;
	
	
	public String getColor() {
		return color;
	}


	public String calculateColor(){
		double sizeArea = poly.getArea();
		int densidad = (int) (getHabitantes() / (sizeArea / 1000000));
		if (densidad < 50){
			color = "#888888";
		}
		if (densidad >= 50 && densidad < 1000){
			color = "#00AA00";
		}
		if (densidad >= 1000 && densidad < 5000){
			color = "#DDDD00";
		}
		if (densidad >= 5000){
			color = "#CC0000";
		}
		return color;
	}
	
	
	public ZonaClient(Zona zona){
		habitantes = zona.getHabitantes();
		hojas = zona.getHojas();
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

	public ZonaClient() {}
	
	public Polygon getPoly() {
		return poly;
	}
	
	public void setPoly(Polygon poly) {
		this.poly = poly;
	}
	
}
