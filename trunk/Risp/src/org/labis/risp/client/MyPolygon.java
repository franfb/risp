package org.labis.risp.client;

import java.io.Serializable;

import com.google.gwt.maps.client.MapWidget;
import com.google.gwt.maps.client.geom.LatLng;
import com.google.gwt.maps.client.overlay.Polygon;
import com.google.gwt.maps.client.overlay.Polyline;

public class MyPolygon implements Serializable{
	
	private static final long serialVersionUID = 1L;
	MyLatLng[][] triangles; 
	
	MyLatLng[] vertex;
	
	MyLatLng topRight, bottomLeft;
	double area;
	
	public static Polygon getPolygon(Polygon poly){
		LatLng[] coord = new LatLng[poly.getVertexCount()];
		for (int i = 0; i < poly.getVertexCount(); i++) {
			coord[i] = poly.getVertex(i);
		}
		return new Polygon(coord, "#f33f00", 5, 1, "#ff0000", 0.2);
	}
	
	public MyPolygon(Polygon poly, MapWidget map){
		poly = getPolygon(poly);
		poly.setVisible(false);
		map.addOverlay(poly);
		vertex = new MyLatLng[poly.getVertexCount()];
		for (int i = 0; i < vertex.length; i++){
			vertex[i] = new MyLatLng(poly.getVertex(i));
		}
		topRight = new MyLatLng(poly.getBounds().getNorthEast()); 
		bottomLeft = new MyLatLng(poly.getBounds().getSouthWest());
		area = poly.getArea();
		triangles = new MyLatLng[poly.getVertexCount() - 3][3];
		int size = 0;
		int count = 0;
		do{
			count++;
			for (int i = 0; i < poly.getVertexCount() - 1; i++){
				LatLng[] ear = getEar(poly, i); 
				if (ear != null){
					poly.deleteVertex(i);
					for (int j = 0; j < 3; j++){
						triangles[size][j] = new MyLatLng(ear[j]);
					}
					size++;
					break;
				}
			}
		} while (size < triangles.length && count < triangles.length * 8);
		if (count == triangles.length * 8){
			vertex = null;
			triangles = null;
		}
		map.removeOverlay(poly);
	}		

	public MyPolygon(){}
	
	public MyLatLng[] getTriangle(int index){
		if (triangles == null){
			return null;
		}
		return triangles[index];
	}
	
	public int getTriangleSize(){
		return triangles.length;
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
	
	
	
	private static LatLng[] getEar(Polygon poly, int index){
		int previous = index - 1;
		if (previous < 0){
			previous = poly.getVertexCount() - 2;
		}
		int next = index + 1;
		if (next == poly.getVertexCount() - 1){
			next = 0;
		}
		LatLng[] l = {poly.getVertex(previous), poly.getVertex(next)};
		Polyline line = new Polyline(l);
	    if (poly.getVertexCount() > 4 && ! contains(poly, line.getBounds().getCenter())){
	    	return null;
	    }
	    LatLng[] l2 = {poly.getVertex(index), poly.getVertex(previous), poly.getVertex(next), poly.getVertex(index)};
	    Polygon triangle = new Polygon(l2);
	    for (int i= 0; i < poly.getVertexCount() - 1; i++){    
	    	if (i != index && i != previous && i != next){
	    		if (contains(triangle, poly.getVertex(i))){
	    			return null;
	    		}
	    	}
	    }
	    LatLng[] l3 = {poly.getVertex(index), poly.getVertex(previous), poly.getVertex(next)};
	    return l3;
	}
	
	public  boolean contains(MyLatLng latLng) {
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
	
	public int getVertexCount(){
		return vertex.length;
	}

	public MyLatLng getVertex(int index){
		return vertex[index];
	}
	
	public static boolean contains(Polygon p, LatLng latLng) {
		int j = 0;
		boolean oddNodes = false;
		double x = latLng.getLongitude();
		double y = latLng.getLatitude();
		for (int i = 0; i < p.getVertexCount(); i++) {
			j++;
			if (j == p.getVertexCount()) {
				j = 0;
			}
			if (((p.getVertex(i).getLatitude() < y) && (p.getVertex(j)
					.getLatitude() >= y))
					|| ((p.getVertex(j).getLatitude() < y) && (p.getVertex(i)
							.getLatitude() >= y))) {
				if (p.getVertex(i).getLongitude()
						+ (y - p.getVertex(i).getLatitude())
						/ (p.getVertex(j).getLatitude() - p.getVertex(i)
								.getLatitude())
						* (p.getVertex(j).getLongitude() - p.getVertex(i)
								.getLongitude()) < x) {
					oddNodes = !oddNodes;
				}
			}
		}
		return oddNodes;
	}
	
}