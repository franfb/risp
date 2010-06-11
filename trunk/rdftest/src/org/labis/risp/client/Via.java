package org.labis.risp.client;

import java.io.Serializable;
import java.util.ArrayList;

public class Via implements Serializable{

	private static final long serialVersionUID = 1L;
	private ArrayList<Portal> portales;
	private int habitantes;
	private double longitud;
	private String nombre;
	private String tipo;

	public Via(ArrayList<Portal> portales, int habitantes, double longitud,
			String nombre, String tipo) {
		this.portales = portales;
		this.habitantes = habitantes;
		this.longitud = longitud;
		this.nombre = nombre;
		this.tipo = tipo;
	}

	public Via() {
	}

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

	public double getLongitud() {
		return longitud;
	}

	public void setLongitud(double longitud) {
		this.longitud = longitud;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}
}
