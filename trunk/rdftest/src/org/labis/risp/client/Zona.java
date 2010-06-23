package org.labis.risp.client;

import java.io.Serializable;
import java.util.ArrayList;

public class Zona implements Serializable{
	private static final long serialVersionUID = 1L;
	private int habitantes;
	private int hojas;
	private ArrayList<Via> vias;
	
	public ArrayList<Via> getVias() {
		return vias;
	}

	public void setVias(ArrayList<Via> vias) {
		this.vias = vias;
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

	public Zona() {}
}
