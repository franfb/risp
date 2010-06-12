package org.labis.risp.client;

import java.util.ArrayList;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface GreetingServiceAsync {
	
	
	
	void getVia(LatLong point, AsyncCallback<Via> callback);

	void getPortal(LatLong point, AsyncCallback<Portal> callback);

	void getPortales(LatLong topRight, LatLong BottomLeft,
			AsyncCallback<ArrayList<Portal>> callback);
	
	void getVias(LatLong topRight, LatLong BottomLeft,
			AsyncCallback<ArrayList<Via>> callback);
}