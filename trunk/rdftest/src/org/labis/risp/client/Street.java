package org.labis.risp.client;

import java.io.Serializable;



public class Street implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private LatLong coord;
	private int population;
	private int registers;
	private char kind;
	private long code;
	private String name;
	

	public Street(long code, LatLong coord, int population, int registers, char kind){
		this.code = code;
		this.coord = coord;
		this.population = population;
		this.registers = registers;
		this.kind = kind;
		this.setName(null);
	}
	
	public Street(long code, LatLong coord, int population, int registers, char kind, String name){
		this.code = code;
		this.coord = coord;
		this.population = population;
		this.registers = registers;
		this.kind = kind;
		this.setName(name);
	}
	
	public LatLong getCoord() {
		return coord;
	}

	public void setCoord(LatLong coord) {
		this.coord = coord;
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

	public char getKind() {
		return kind;
	}

	public void setKind(char kind) {
		this.kind = kind;
	}

	public long getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}
	
	public Street(){}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
}
