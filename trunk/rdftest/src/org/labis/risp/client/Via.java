package org.labis.risp.client;

import java.io.Serializable;

public class Via implements Serializable{

	private static final long serialVersionUID = 1L;
	private MyLatLng coordenadas;
	private int habitantes;
	private double longitud;
	private String nombre;
	private String codigo;

	public Via(MyLatLng coordenadas, int habitantes, double longitud,
			String nombre, String codigo) {
		this.coordenadas = coordenadas;
		this.habitantes = habitantes;
		this.longitud = longitud;
		this.nombre = nombre;
		this.codigo = codigo;
	}

	public Via() {
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

	public void setCoordenadas(MyLatLng coordenadas) {
		this.coordenadas = coordenadas;
	}

	public MyLatLng getCoordenadas() {
		return coordenadas;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	public String getCodigo() {
		return codigo;
	}
}
