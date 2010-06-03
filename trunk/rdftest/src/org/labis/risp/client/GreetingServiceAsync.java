package org.labis.risp.client;





import java.util.ArrayList;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The async counterpart of <code>GreetingService</code>.
 */
public interface GreetingServiceAsync {
	
	
	void getStreets(LatLong topRight, LatLong BottonLeft,
			AsyncCallback<ArrayList<Street>> callback);

	void getStreet(LatLong place, AsyncCallback<Street> callback);

	void initialize(AsyncCallback<Boolean> callback);
}
