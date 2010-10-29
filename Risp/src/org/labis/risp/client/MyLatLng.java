package org.labis.risp.client;

import java.io.Serializable;

import com.google.gwt.maps.client.geom.LatLng;

public class MyLatLng implements Serializable{

	private static final long serialVersionUID = 1L;
	private double lat;
	private double lng;
	
	public MyLatLng(double lat, double lng){
		this.lat = lat;
		this.lng = lng;
	}
	
	public MyLatLng(LatLng coord){
		this.lat = coord.getLatitude();
		this.lng = coord.getLongitude();
	}
	
	public MyLatLng(){}
	
	public double getLatitude() {
		return lat;
	}

	public void setLatitude(double lat) {
		this.lat = lat;
	}

	public double getLongitude() {
		return lng;
	}

	public void setLongitude(double lng) {
		this.lng = lng;
	}
}
