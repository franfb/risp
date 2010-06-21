package org.labis.risp.client;

import java.io.Serializable;

import com.google.gwt.maps.client.geom.LatLng;
import com.google.gwt.maps.client.overlay.Polygon;
import com.google.gwt.maps.client.overlay.Polyline;

public class MyPolygon implements Serializable{
	
	private static final long serialVersionUID = 1L;
	MyLatLng[][] triangles; 
	
	MyLatLng topRight, bottomLeft;
	double area;
	
	public MyPolygon(Polygon poly){
		topRight = new MyLatLng(poly.getBounds().getNorthEast()); 
		bottomLeft = new MyLatLng(poly.getBounds().getSouthWest());
		area = poly.getArea();
		
		triangles = new MyLatLng[poly.getVertexCount() - 3][3];
		
		int size = 0;
		do{
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
		} while (size < triangles.length);
	}		

	public MyPolygon(){}
	
	public MyLatLng[] getTriangle(int index){
		return triangles[index];
	}
	
	public int getTriangles(){
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
		
		
	    if (! contains(poly, line.getBounds().getCenter())){
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