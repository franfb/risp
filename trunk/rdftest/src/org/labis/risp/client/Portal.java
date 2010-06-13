package org.labis.risp.client;

import java.io.Serializable;

public class Portal implements Serializable{

	private static final long serialVersionUID = 1L;
	private LatLong coordenadas;
	private int numero;
	private int habitantes;
	private int hojas;
	private String tipo;
	private String codigo;
	private String via;
	
	public Portal(LatLong coordenadas, int numero, int habitantes, int hojas,
			String tipo, String codigo, String via) {
		this.coordenadas = coordenadas;
		this.numero = numero;
		this.habitantes = habitantes;
		this.hojas = hojas;
		this.tipo = tipo;
		this.codigo = codigo;
		this.via = via;
	}
	
	public Portal(){}
	
	public LatLong getCoordenadas() {
		return coordenadas;
	}
	
	public String getVia() {
		return via;
	}

	public void setVia(String via) {
		this.via = via;
	}

	public String getTipo() {
		return tipo;
	}
	
	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public void setCoordenadas(LatLong coordenadas) {
		this.coordenadas = coordenadas;
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
	
	public String getCodigo() {
		return codigo;
	}
	
	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	public void setNumero(int numero) {
		this.numero = numero;
	}

	public int getNumero() {
		return numero;
	}
}
