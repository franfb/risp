package org.labis.risp.client;

import java.util.ArrayList;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface GreetingServiceAsync {
	
	
	
	void getVia(MyLatLng point, AsyncCallback<Via> callback);

	void getPortal(MyLatLng point, AsyncCallback<Portal> callback);

	void getPortales(MyPolygon poly, MyLatLng topRight, MyLatLng BottomLeft,
			AsyncCallback<ArrayList<Portal>> callback);
	
	void getVias(MyPolygon poly, MyLatLng topRight, MyLatLng BottomLeft,
			AsyncCallback<ArrayList<Via>> callback);

	void getPortales(Via via, AsyncCallback<ArrayList<Portal>> callback);
}