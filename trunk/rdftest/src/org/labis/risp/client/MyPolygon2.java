package org.labis.risp.client;

import java.io.Serializable;

import com.google.gwt.maps.client.overlay.Polygon;

public class MyPolygon2 implements Serializable{
	
	private static final long serialVersionUID = 1L;
	MyLatLng[] vertex;
	
	MyLatLng topRight, bottomLeft;
	double area;
	
	public MyPolygon2(Polygon poly){
		vertex = new MyLatLng[poly.getVertexCount()];
		for (int i = 0; i < vertex.length; i++){
			vertex[i] = new MyLatLng(poly.getVertex(i));
		}
		topRight = new MyLatLng(poly.getBounds().getNorthEast()); 
		bottomLeft = new MyLatLng(poly.getBounds().getSouthWest());
		area = poly.getArea();
	}
	
	public double getArea() {
		return area;
	}

	public MyLatLng getTopRight() {
		return topRight;
	}

	public MyLatLng getBottomLeft() {
		return bottomLeft;
	}
	
	public MyPolygon2(){}
	
	public int getVertexCount(){
		return vertex.length;
	}

	public MyLatLng getVertex(int index){
		return vertex[index];
	}
	
	public boolean contains(MyLatLng latLng) {
		int j = 0;
		boolean oddNodes = false;
		double x = latLng.getLongitude();
		double y = latLng.getLatitude();
		for (int i = 0; i < getVertexCount(); i++) {
			j++;
			if (j == getVertexCount()) {
				j = 0;
			}
			if (((getVertex(i).getLatitude() < y) && (getVertex(j)
					.getLatitude() >= y))
					|| ((getVertex(j).getLatitude() < y) && (getVertex(i)
							.getLatitude() >= y))) {
				if (getVertex(i).getLongitude()
						+ (y - getVertex(i).getLatitude())
						/ (getVertex(j).getLatitude() - getVertex(i)
								.getLatitude())
						* (getVertex(j).getLongitude() - getVertex(i)
								.getLongitude()) < x) {
					oddNodes = !oddNodes;
				}
			}
		}
		return oddNodes;
	}
}
