package org.labis.risp.client;

import java.util.ArrayList;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface GreetingServiceAsync {
	
	
	
	void getVia(LatLong place, AsyncCallback<Via> callback);

	void getPortal(LatLong place, AsyncCallback<Portal> callback);

	void getPortales(LatLong topRight, LatLong BottomLeft,
			AsyncCallback<ArrayList<Portal>> callback);
}