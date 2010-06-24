package org.labis.risp.client;

import java.util.ArrayList;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface GreetingServiceAsync {
	
	void getVia(MyLatLng point, AsyncCallback<Via> callback);
	
	void getVia(Portal portal, AsyncCallback<Via> callback);


	void getPortales(MyPolygon poly, AsyncCallback<ArrayList<Portal>> callback);
	
	void getVias(MyPolygon poly, AsyncCallback<Zona> callback);

	void getPortales(Via via, AsyncCallback<ArrayList<Portal>> callback);

	void getZona(MyPolygon poly, AsyncCallback<Zona> callback);

	void getPortal(MyLatLng point, AsyncCallback<Portal> callback);

	void getPortales(String nombre, int numero,
			AsyncCallback<ArrayList<Portal>> callback);

	void getVias(String nombre, AsyncCallback<ArrayList<Via>> callback);

}