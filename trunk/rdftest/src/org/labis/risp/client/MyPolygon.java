package org.labis.risp.client;

import java.io.Serializable;

import com.google.gwt.maps.client.overlay.Polygon;

public class MyPolygon implements Serializable{
	
	private static final long serialVersionUID = 1L;
	MyLatLng[] vertex;
	
	public MyPolygon(Polygon poly){
		vertex = new MyLatLng[poly.getVertexCount()];
		for (int i = 0; i < vertex.length; i++){
			vertex[i] = new MyLatLng(poly.getVertex(i));
		}
	}
	
	public MyPolygon(){}
	
	public int getVertexCount(){
		return vertex.length;
	}

	public MyLatLng getVertex(int index){
		return vertex[index];
	}
	
}
