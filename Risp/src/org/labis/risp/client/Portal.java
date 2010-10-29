package org.labis.risp.client;

import java.io.Serializable;

public class Portal implements Serializable{

	private static final long serialVersionUID = 1L;
	private MyLatLng coordenadas;
	private int numero;
	private int habitantes;
	private int hojas;
	private String codigoVia;
	private String codigo;
	private String via;
	
	public Portal(MyLatLng coordenadas, int numero, int habitantes, int hojas,
			String codigoVia, String codigo, String via) {
		this.coordenadas = coordenadas;
		this.numero = numero;
		this.habitantes = habitantes;
		this.hojas = hojas;
		if (codigoVia == null){
			this.codigoVia = "-";
		}
		else{
			this.codigoVia = codigoVia;
		}
		this.codigo = codigo;
		this.via = via;
	}
	
	public Portal(){}
	
	public MyLatLng getCoordenadas() {
		return coordenadas;
	}
	
	public String getVia() {
		return via;
	}

	public void setVia(String via) {
		this.via = via;
	}

	public void setCoordenadas(MyLatLng coordenadas) {
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
	
	public String getCodigoVia(){
		return codigoVia;
	}
	
	public void setCodigoVia(String codigoVia){
		if (codigoVia == null){
			this.codigoVia = "-";
		}
		else{
			this.codigoVia = codigoVia;
		}
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
